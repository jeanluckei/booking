package com.alten.booking.api.controller;

import com.alten.booking.infrastructure.repository.BookingRepository;
import com.alten.booking.infrastructure.repository.entity.Booking;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/booking")
@AllArgsConstructor
public class BookingController {

    private final BookingRepository repository;

    @Operation(summary = "Find all bookings by username")
    @GetMapping
    public Flux<Booking> findAllByUsername(@RequestParam String username) {
        return repository.findAllByUsername(username);
    }

    @Operation(summary = "Find booking by id")
    @GetMapping("/{id}")
    public Mono<Booking> findById(@PathVariable String id) {
        return repository.findById(id);
    }

    //Every end-user can check the room availability, place a reservation, cancel it or modify it.

    @Operation(summary = "Cancel booking by id")
    @DeleteMapping("/{id}")
    public Mono<Void> cancelBookingById(@PathVariable String id) {
        return repository.deleteById(id);
    }

    @Operation(summary = "Update booking by id")
    @PutMapping("/{id}")
    public Mono<Booking> updateBookingById(@PathVariable String id, @Valid @RequestBody Booking booking) {
        return repository.save(booking);
    }

    @Operation(summary = "Create booking")
    @PostMapping
    public Mono<Booking> createBooking(@Valid @RequestBody Booking booking) {
        return repository.save(booking);
    }

    @Operation(summary = "Find all bookings by room number")
    @GetMapping("/room/{roomNumber}")
    public Flux<Booking> findBookingsByRoomNumber(@PathVariable Long roomNumber) {
        return repository.findAllByRoomNumber(roomNumber);
    }

    @Operation(summary = "Find booking availability by room number and date")
    @GetMapping("/room/{roomNumber}/availability")
    public Flux<Booking> findBookingAvailability(@PathVariable Long roomNumber,
                                              @RequestParam("startDate") LocalDateTime startDate,
                                              @RequestParam("endDate") LocalDateTime endDate) {
        return repository.findAllByRoomNumber(roomNumber);
    }

}