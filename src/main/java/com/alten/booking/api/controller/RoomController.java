package com.alten.booking.api.controller;

import com.alten.booking.api.dto.RoomDTO;
import com.alten.booking.business.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/room")
@AllArgsConstructor
public class RoomController {

    private final RoomService service;

    @Operation(summary = "List all rooms")
    @GetMapping
    public Flux<RoomDTO> findAll() {
        return service.findAll();
    }

    @Operation(summary = "Find room by number")
    @GetMapping("/{roomNumber}")
    public Mono<RoomDTO> findByNumber(@PathVariable Long roomNumber) {
        return service.findByRoomNumber(roomNumber);
    }

    @Operation(summary = "Create room")
    @PostMapping
    public Mono<RoomDTO> createRoom(@RequestBody RoomDTO dto) {
        return service.createRoom(dto);
    }

    @Operation(summary = "Update room")
    @PutMapping("/{id}")
    public Mono<RoomDTO> updateRoom(@PathVariable String id, @RequestBody RoomDTO dto) {
        return service.updateRoom(id, dto);
    }
}