package com.alten.booking.api.controller;

import com.alten.booking.api.dto.BookingRequestDTO;
import com.alten.booking.api.dto.BookingResponseDTO;
import com.alten.booking.business.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
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
    @PostMapping
    public Mono<BookingResponseDTO> createBooking(@Valid @RequestBody BookingRequestDTO dto) {
        return service.createBooking(dto);
    }

    @Operation(summary = "Update booking by id")
    @PutMapping("/{id}")
    public Mono<BookingResponseDTO> updateBookingById(@PathVariable String id, @Valid @RequestBody BookingRequestDTO dto) {
        return service.updateBookingById(id, dto);
    }

    @Operation(summary = "Cancel booking by id")
    @DeleteMapping("/{id}")
    public Mono<BookingResponseDTO> cancelBookingById(@PathVariable String id) {
        return service.cancelById(id);
    }

    @Operation(summary = "Find all bookings by room number")
    @GetMapping("/room/{roomNumber}")
    public Flux<BookingResponseDTO> findBookingsByRoomNumber(@PathVariable Long roomNumber) {
        return service.findAllByRoomNumber(roomNumber);
    }

    @Operation(summary = "Find booking availability by room number and date")
    @GetMapping("/room/{roomNumber}/availability")
    public Mono<Boolean> findBookingAvailability(@PathVariable Long roomNumber,
                                              @RequestParam("startDate") LocalDate startDate,
                                              @RequestParam("endDate") LocalDate endDate) {
        return service.isRoomAvailableForBooking(roomNumber, startDate, endDate);
    }

}