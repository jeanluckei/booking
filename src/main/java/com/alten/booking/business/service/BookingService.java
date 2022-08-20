package com.alten.booking.business.service;

import com.alten.booking.api.dto.BookingDTO;
import com.alten.booking.business.exception.BusinessException;
import com.alten.booking.business.mapper.BookingMapper;
import com.alten.booking.infrastructure.repository.BookingRepository;
import com.alten.booking.infrastructure.repository.entity.Booking;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
@AllArgsConstructor
public class BookingService {

    private final BookingRepository repository;
    private final BookingMapper mapper;

    public Mono<BookingDTO> findById(String id) {
        return repository.findById(id)
                .map(mapper::toDto);
    }

    public Flux<BookingDTO> findAllByUsername(String username) {
        return repository.findAllByUsername(username)
                .map(mapper::toDto);
    }

    public Flux<BookingDTO> findAllByRoomNumber(Long roomNumber) {
        return repository.findAllByRoomNumber(roomNumber)
                .map(mapper::toDto);
    }

    public Mono<BookingDTO> createBooking(BookingDTO dto) {
        return Mono.just(dto)
                .flatMap(this::validateBooking)
                .map(mapper::toEntity)
                .map(Booking::booked)
                .flatMap(repository::save)
                .map(mapper::toDto);
    }

    public Mono<BookingDTO> updateBookingById(String id, BookingDTO dto) {
        return repository.findById(id)
                .flatMap(entity -> validateRoomAvailability(entity, dto))
                .map(entity -> mapper.copyFromDTO(dto, entity))
                .flatMap(repository::save)
                .map(mapper::toDto);
    }

    public Mono<BookingDTO> cancelById(String id) {
        return repository.findById(id)
                .map(Booking::cancelled)
                .flatMap(repository::save)
                .map(mapper::toDto);
    }

    private Mono<Booking> validateRoomAvailability(Booking booking, BookingDTO dto) {
        return Mono.just(booking);
        //On error throw room not available for selected dates
    }

    private Mono<BookingDTO> validateBooking(BookingDTO dto) {
        return isRoomAvailableForBooking(dto.getRoomNumber(), dto.getStartDate(), dto.getEndDate())
                .filter(BooleanUtils::isFalse)
                .flatMap(aBoolean -> Mono.error(new BusinessException("Room not available for given dates!")))
                .switchIfEmpty(Mono.just(dto))
                .cast(BookingDTO.class);
    }

    public Mono<Boolean> isRoomAvailableForBooking(Long roomNumber, LocalDate startDate, LocalDate endDate) {
        //TODO
        //Room exists?
        //Are dates valid?
        //Can’t be longer than 3 days
        //Can’t be reserved more than 30 days in advance
        //All reservations start at least the next day of booking
        //Is room available?
        return Mono.just(Boolean.TRUE);
    }
}