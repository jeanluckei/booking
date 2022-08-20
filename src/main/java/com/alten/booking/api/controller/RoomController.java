package com.alten.booking.api.controller;

import com.alten.booking.infrastructure.repository.RoomRepository;
import com.alten.booking.infrastructure.repository.entity.Room;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/room")
@AllArgsConstructor
public class RoomController {

    private final RoomRepository repository;

    @Operation(summary = "List all rooms")
    @GetMapping
    public Flux<Room> findAll() {
        return repository.findAll();
    }

    @Operation(summary = "Find room by number")
    @GetMapping("/{roomNumber}")
    public Mono<Room> findByNumber(@PathVariable Long roomNumber) {
        return repository.findByRoomNumber(roomNumber);
    }

}