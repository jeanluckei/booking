package com.alten.booking.api.controller;

import com.alten.booking.api.dto.BookingRequestDTO;
import com.alten.booking.api.dto.BookingResponseDTO;
import com.alten.booking.business.service.BookingService;
import com.alten.booking.business.service.BookingServiceValidator;
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
    private final BookingServiceValidator validator;

    @Operation(summary = "Find booking by id")
    @GetMapping("/{id}")
    public Mono<BookingResponseDTO> findById(@RequestHeader(required = false) String username,
                                             @PathVariable String id) {
        return service.findById(id);
    }

    @Operation(summary = "Find all bookings for logged user")
    @GetMapping
    public Flux<BookingResponseDTO> findAllByUsername(@RequestHeader String username) {
        return service.findAllByUsername(username);
    }

    @Operation(summary = "Create booking")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping
    public Mono<BookingResponseDTO> createBooking(@RequestHeader String username,
                                                  @Valid @RequestBody Mono<BookingRequestDTO> dto) {
        return dto
                .flatMap(it -> validator.validateHeaders(username, it))
                .flatMap(service::createBooking);
    }

    @Operation(summary = "Update booking by id")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PutMapping("/{id}")
    public Mono<BookingResponseDTO> updateBookingById(@RequestHeader String username,
                                                      @PathVariable String id,
                                                      @Valid @RequestBody Mono<BookingRequestDTO> dto) {
        return dto
                .flatMap(it -> validator.validateHeaders(username, it))
                .flatMap(it -> service.updateBookingById(id, it));
    }

    @Operation(summary = "Cancel booking by id")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @DeleteMapping("/{id}")
    public Mono<BookingResponseDTO> cancelBookingById(@RequestHeader String username, @PathVariable String id) {
        return service.cancelById(id);
    }

    @Operation(summary = "Find all bookings by room number")
    @GetMapping("/room/{roomNumber}")
    public Flux<BookingResponseDTO> findBookingsByRoomNumber(@RequestHeader(required = false) String username,
                                                             @PathVariable Long roomNumber,
                                                             @RequestParam BookingStatus status) {
        return service.findAllByRoomNumberAndStatus(roomNumber, status);
    }

    @Operation(summary = "Find booking availability by room number and date")
    @GetMapping("/room/{roomNumber}/availability")
    public Mono<Boolean> findBookingAvailability(@RequestHeader(required = false) String username,
                                                 @PathVariable Long roomNumber,
                                                 @RequestParam("startDate")
                                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                 @RequestParam("endDate")
                                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return service.isValidRequestAndRoomAvailable(roomNumber, startDate, endDate);
    }

}