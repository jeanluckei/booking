package com.alten.booking.api.controller;

import com.alten.booking.api.dto.BookingDTO;
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
    public Mono<BookingDTO> findById(@PathVariable String id) {
        return service.findById(id);
    }

    @Operation(summary = "Find all bookings by username")
    @GetMapping
    public Flux<BookingDTO> findAllByUsername(@RequestParam String username) {
        return service.findAllByUsername(username);
    }

    @Operation(summary = "Create booking")
    @PostMapping
    public Mono<BookingDTO> createBooking(@Valid @RequestBody BookingDTO dto) {
        return service.createBooking(dto);
    }

    @Operation(summary = "Update booking by id")
    @PutMapping("/{id}")
    public Mono<BookingDTO> updateBookingById(@PathVariable String id, @Valid @RequestBody BookingDTO dto) {
        return service.updateBookingById(id, dto);
    }

    @Operation(summary = "Cancel booking by id")
    @DeleteMapping("/{id}")
    public Mono<BookingDTO> cancelBookingById(@PathVariable String id) {
        return service.cancelById(id);
    }

    @Operation(summary = "Find all bookings by room number")
    @GetMapping("/room/{roomNumber}")
    public Flux<BookingDTO> findBookingsByRoomNumber(@PathVariable Long roomNumber) {
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