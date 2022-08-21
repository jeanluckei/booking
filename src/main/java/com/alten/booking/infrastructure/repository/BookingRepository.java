package com.alten.booking.infrastructure.repository;

import com.alten.booking.infrastructure.repository.entity.Booking;
import com.alten.booking.infrastructure.repository.entity.BookingStatus;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@Repository
public interface BookingRepository extends ReactiveMongoRepository<Booking, String> {

    Flux<Booking> findAllByUsername(String username);
    Flux<Booking> findAllByRoomNumberAndStatus(Long roomNumber, BookingStatus status);

    @Query(value = "{$and: [" +
            "        {roomNumber: ?0}," +
            "        {$or: [" +
            "            {startDate: {$gte: ?1, $lte: ?2}}," +
            "            {endDate: {$gte: ?1, $lte: ?2}}," +
            "            {$and: [" +
            "              {startDate: {$lt: ?1}}," +
            "              {endDate: {$gt: ?2}}" +
            "            ]}" +
            "        ]}," +
            "        {_id: {$ne: ?3}}," +
            "        {status: 'BOOKED'}" +
            "    ]}")
    Flux<Booking> findAllByRoomNumberAndDatesConflict(Long room, LocalDate start, LocalDate end, String id);
}