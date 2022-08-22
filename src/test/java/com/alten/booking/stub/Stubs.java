package com.alten.booking.stub;

import com.alten.booking.api.dto.BookingRequestDTO;
import com.alten.booking.api.dto.BookingResponseDTO;
import com.alten.booking.infrastructure.repository.entity.Booking;
import com.alten.booking.infrastructure.repository.entity.BookingStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Stubs {

    public static BookingRequestDTO validBookingRequestDTOStub() {
        return BookingRequestDTO.builder()
                .username("jean")
                .startDate(LocalDate.now().plusDays(5))
                .endDate(LocalDate.now().plusDays(7))
                .roomNumber(237L)
                .build();
    }

    public static BookingResponseDTO bookingResponseDTOStub() {
        return BookingResponseDTO.builder()
                .username("jean")
                .startDate(LocalDate.now().plusDays(5))
                .endDate(LocalDate.now().plusDays(7))
                .roomNumber(237L)
                .id("6303c736c2f14e77828504ca")
                .status(BookingStatus.BOOKED)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();
    }

    public static Booking bookingStub() {
        return Booking.builder()
                .username("jean")
                .startDate(LocalDate.now().plusDays(5))
                .endDate(LocalDate.now().plusDays(7))
                .roomNumber(237L)
                .id("6303c736c2f14e77828504ca")
                .status(BookingStatus.BOOKED)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();
    }
}
