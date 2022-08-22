package com.alten.booking.business.service;

import com.alten.booking.api.dto.BookingRequestDTO;
import com.alten.booking.api.dto.BookingResponseDTO;
import com.alten.booking.business.exception.BusinessException;
import com.alten.booking.business.exception.NotFoundException;
import com.alten.booking.business.mapper.BookingMapper;
import com.alten.booking.infrastructure.messaging.producer.BookingEventProducer;
import com.alten.booking.infrastructure.repository.BookingRepository;
import com.alten.booking.infrastructure.repository.entity.Booking;
import com.alten.booking.infrastructure.repository.entity.BookingStatus;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Objects;

@Service
@AllArgsConstructor
public class BookingService {

    private final BookingRepository repository;
    private final BookingMapper mapper;
    private final BookingEventProducer producer;
    private final BookingServiceValidator validator;

    public Mono<BookingResponseDTO> findById(String id) {
        return repository.findById(id)
                .map(mapper::toDto)
                .switchIfEmpty(Mono.error(new NotFoundException("Booking " + id + " not found!")));
    }

    public Flux<BookingResponseDTO> findAllByUsername(String username) {
        return repository.findAllByUsername(username)
                .map(mapper::toDto)
                .switchIfEmpty(Mono.error(new NotFoundException("User " + username + " has no bookings!")));
    }

    public Flux<BookingResponseDTO> findAllByRoomNumberAndStatus(Long roomNumber, BookingStatus status) {
        return repository.findAllByRoomNumberAndStatus(roomNumber, status)
                .map(mapper::toDto)
                .switchIfEmpty(Mono.error(new NotFoundException("Room " + roomNumber + " has no bookings!")));
    }

    public Mono<BookingResponseDTO> createBooking(BookingRequestDTO dto) {
        return Mono.just(dto)
                .map(mapper::toEntity)
                .flatMap(this::validateBooking)
                .map(Booking::pending)
                .flatMap(producer::bookingEventOutput)
                .map(mapper::toDto);
    }

    public Mono<BookingResponseDTO> updateBookingById(String id, BookingRequestDTO dto) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Booking not found for update!")))
                .map(entity -> mapper.copyFromDTO(dto, entity))
                .flatMap(this::validateBooking)
                .flatMap(producer::bookingEventOutput)
                .map(mapper::toDto);
    }

    public Mono<BookingResponseDTO> cancelById(String id) {
        return repository.findById(id)
                .map(Booking::cancelled)
                .flatMap(producer::bookingEventOutput)
                .map(mapper::toDto);
    }

    public Mono<Booking> createOrUpdate(Booking booking) {
        return isRoomAvailable(booking)
                .filter(BooleanUtils::isTrue)
                .map(aBoolean -> booking.booked())
                .flatMap(repository::save)
                .switchIfEmpty(Mono.defer(() -> {
                    if (Objects.isNull(booking.getId())) {
                        return repository.save(booking.overbooked());
                    }
                    return Mono.error(new BusinessException("Sending message: Could not update your booking!"));
                }));
    }

    public Mono<Booking> cancel(Booking booking) {
        return repository.save(booking);
    }

    public Mono<Boolean> isValidRequestAndRoomAvailable(Long roomNumber, LocalDate startDate, LocalDate endDate) {
        return isValidRequestAndRoomAvailable(Booking.builder().roomNumber(roomNumber)
                .startDate(startDate)
                .endDate(endDate)
                .build())
                .filter(BooleanUtils::isFalse)
                .flatMap(aBoolean -> Mono.error(new BusinessException("Room not available for given dates!")));
    }

    private Mono<Booking> validateBooking(Booking booking) {
        return isValidRequestAndRoomAvailable(booking)
                .filter(BooleanUtils::isFalse)
                .flatMap(aBoolean -> Mono.error(new BusinessException("Room not available for given dates!")))
                .switchIfEmpty(Mono.just(booking))
                .cast(Booking.class);
    }

    private Mono<Boolean> isValidRequestAndRoomAvailable(Booking booking) {
        return validator.validateRoomExistsAndDatesAreCorrect(booking)
                .flatMap(it -> isRoomAvailable(booking));
    }

    private Mono<Boolean> isRoomAvailable(Booking booking) {
        return repository.findAllByRoomNumberAndDatesConflict(booking.getRoomNumber(), booking.getStartDate(),
                        booking.getEndDate(), booking.getId())
                .collectList()
                .map(CollectionUtils::isEmpty);
    }
}