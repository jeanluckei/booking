package com.alten.booking.infrastructure.repository;

import com.alten.booking.infrastructure.repository.entity.Room;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends ReactiveMongoRepository<Room, String> {
}