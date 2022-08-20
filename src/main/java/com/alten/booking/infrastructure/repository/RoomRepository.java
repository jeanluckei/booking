package com.alten.booking.infrastructure.repository;

import com.alten.booking.infrastructure.repository.entity.Room;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface RoomRepository extends ReactiveMongoRepository<Room, String> {

    Mono<Room> findByRoomNumber(Long roomNumber);
    Mono<Boolean> existsByRoomNumber(Long roomNumber);

}