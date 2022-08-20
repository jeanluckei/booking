package com.alten.booking.infrastructure.repository;

import com.alten.booking.infrastructure.repository.entity.Booking;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends ReactiveMongoRepository<Booking, String> {
}