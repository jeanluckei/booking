package com.alten.booking.api.controller;

import com.alten.booking.infrastructure.repository.RoomRepository;
import com.alten.booking.infrastructure.repository.entity.Room;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/room")
@AllArgsConstructor
public class RoomController {

    private final RoomRepository repository;

    @GetMapping
    public Flux<Room> get() {
        return repository.findAll();
    }

}