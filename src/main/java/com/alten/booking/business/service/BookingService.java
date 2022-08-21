package com.alten.booking.business.service;

import com.alten.booking.api.dto.BookingRequestDTO;
import com.alten.booking.api.dto.BookingResponseDTO;
import com.alten.booking.business.exception.BusinessException;
import com.alten.booking.business.exception.NotFoundException;
import com.alten.booking.business.mapper.BookingMapper;
import com.alten.booking.infrastructure.repository.BookingRepository;
import com.alten.booking.infrastructure.repository.entity.Booking;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
@AllArgsConstructor
public class BookingService {

    private final BookingRepository repository;
    private final BookingMapper mapper;
    private final RoomService roomService;

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

    public Flux<BookingResponseDTO> findAllByRoomNumber(Long roomNumber) {
        return repository.findAllByRoomNumber(roomNumber)
                .map(mapper::toDto)
                .switchIfEmpty(Mono.error(new NotFoundException("Room " + roomNumber + " has no bookings!")));
    }

    public Mono<BookingResponseDTO> createBooking(BookingRequestDTO dto) {
        return Mono.just(dto)
                .map(mapper::toEntity)
                .flatMap(this::validateBooking)
                .map(Booking::booked)
                .flatMap(repository::save)
                .map(mapper::toDto);
    }

    public Mono<BookingResponseDTO> updateBookingById(String id, BookingRequestDTO dto) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Booking not found for update!")))
                .map(entity -> mapper.copyFromDTO(dto, entity))
                .flatMap(this::validateBooking)
                .flatMap(repository::save)
                .map(mapper::toDto);
    }

    public Mono<BookingResponseDTO> cancelById(String id) {
        return repository.findById(id)
                .map(Booking::cancelled)
                .flatMap(repository::save)
                .map(mapper::toDto);
    }

    public Mono<Boolean> isRoomAvailableForBooking(Long roomNumber, LocalDate startDate, LocalDate endDate) {
        return isRoomAvailableForBooking(Booking.builder().roomNumber(roomNumber)
                .startDate(startDate)
                .endDate(endDate)
                .build());
    }

    private Mono<Booking> validateBooking(Booking booking) {
        return isRoomAvailableForBooking(booking)
                .filter(BooleanUtils::isFalse)
                .flatMap(aBoolean -> Mono.error(new BusinessException("Room not available for given dates!")))
                .switchIfEmpty(Mono.just(booking))
                .cast(Booking.class);
    }

    private Mono<Boolean> isRoomAvailableForBooking(Booking booking) {
        return roomService.findByRoomNumber(booking.getRoomNumber())
                .flatMap(it -> datesValidator(booking.getStartDate(), booking.getEndDate()))
                .flatMap(it -> isRoomAvailable(booking));
    }

    private Mono<Boolean> isRoomAvailable(Booking booking) {
        return repository.findAllByRoomNumberAndDatesConflict(booking.getRoomNumber(), booking.getStartDate(),
                        booking.getEndDate(), booking.getId())
                .collectList()
                .map(CollectionUtils::isEmpty);
    }

    private Mono<Boolean> datesValidator(LocalDate startDate, LocalDate endDate) {

        if (!startDate.isEqual(endDate) && startDate.isAfter(endDate)) {
            return Mono.error(new BusinessException("End date should be after start date"));
        }

        if (startDate.isBefore(LocalDate.now()) || startDate.isEqual(LocalDate.now())) {
            return Mono.error(new BusinessException("Reservations start at least the next day of booking!"));
        }

        if (startDate.datesUntil(endDate).count() > 2) {
            return Mono.error(new BusinessException("The stay can’t be longer than 3 days!"));
        }

        if (LocalDate.now().datesUntil(startDate).count() > 30) {
            return Mono.error(new BusinessException("The stay can’t be reserved more than 30 days in advance!"));
        }

        return Mono.just(Boolean.TRUE);
    }
}