package com.alten.booking.infrastructure.messaging.producer;

import com.alten.booking.business.exception.BusinessException;
import com.alten.booking.infrastructure.repository.entity.Booking;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class BookingEventProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookingEventProducer.class);

    private StreamBridge streamBridge;

    public Mono<Booking> bookingEventOutput(Booking booking) {
        return Mono.just(streamBridge.send("bookingEventOutput-out-0", booking))
                .filter(Boolean::booleanValue)
                .doOnError(error -> LOGGER.error("Error producing message: " + booking, error))
                .doOnSuccess(dto -> LOGGER.info("Booking message sent: {}", booking))
                .switchIfEmpty(Mono.error(new BusinessException("Error producing message: " + booking)))
                .map(it -> booking);
    }

}