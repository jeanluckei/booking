package com.alten.booking.api.controller;

import com.alten.booking.api.dto.BookingRequestDTO;
import com.alten.booking.api.dto.BookingResponseDTO;
import com.alten.booking.business.service.BookingService;
import com.alten.booking.infrastructure.repository.entity.BookingStatus;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.time.LocalDate;

@RestController
@RequestMapping("/booking")
@AllArgsConstructor
public class BookingController {

    private final BookingService service;

    @Operation(summary = "Find booking by id")
    @GetMapping("/{id}")
    public Mono<BookingResponseDTO> findById(@PathVariable String id) {
        return service.findById(id);
    }

    @Operation(summary = "Find all bookings by username")
    @GetMapping
    public Flux<BookingResponseDTO> findAllByUsername(@RequestParam String username) {
        return service.findAllByUsername(username);
    }

    @Operation(summary = "Create booking")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping
    public Mono<BookingResponseDTO> createBooking(@Valid @RequestBody BookingRequestDTO dto) {
        return service.createBooking(dto);
    }

    @Operation(summary = "Update booking by id")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PutMapping("/{id}")
    public Mono<BookingResponseDTO> updateBookingById(@PathVariable String id, @Valid @RequestBody BookingRequestDTO dto) {
        return service.updateBookingById(id, dto);
    }

    @Operation(summary = "Cancel booking by id")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @DeleteMapping("/{id}")
    public Mono<BookingResponseDTO> cancelBookingById(@PathVariable String id) {
        return service.cancelById(id);
    }

    @Operation(summary = "Find all bookings by room number")
    @GetMapping("/room/{roomNumber}")
    public Flux<BookingResponseDTO> findBookingsByRoomNumber(@PathVariable Long roomNumber,
                                                             @RequestParam BookingStatus status) {
        return service.findAllByRoomNumberAndStatus(roomNumber, status);
    }

    @Operation(summary = "Find booking availability by room number and date")
    @GetMapping("/room/{roomNumber}/availability")
    public Mono<Boolean> findBookingAvailability(@PathVariable Long roomNumber,
                                                 @RequestParam("startDate")
                                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                 @RequestParam("endDate")
                                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return service.isValidRequestAndRoomAvailable(roomNumber, startDate, endDate);
    }

}