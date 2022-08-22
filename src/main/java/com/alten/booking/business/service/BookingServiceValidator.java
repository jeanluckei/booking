package com.alten.booking.business.service;

import com.alten.booking.api.dto.BookingRequestDTO;
import com.alten.booking.business.exception.BusinessException;
import com.alten.booking.infrastructure.repository.entity.Booking;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
@AllArgsConstructor
public class BookingServiceValidator {

    private final RoomService roomService;

    public Mono<BookingRequestDTO> validateHeaders(String username, BookingRequestDTO it) {
        return username.equals(it.getUsername())
                ? Mono.just(it)
                : Mono.error(new BusinessException("Different username between header and body!"));
    }

    public Mono<Boolean> validateRoomExistsAndDatesAreCorrect(Booking booking) {
        return roomService.findByRoomNumber(booking.getRoomNumber())
                .flatMap(it -> datesValidator(booking.getStartDate(), booking.getEndDate()));
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