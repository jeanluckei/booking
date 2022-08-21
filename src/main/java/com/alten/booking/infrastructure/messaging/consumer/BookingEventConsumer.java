package com.alten.booking.infrastructure.messaging.consumer;

import com.alten.booking.business.exception.BusinessException;
import com.alten.booking.business.service.BookingService;
import com.alten.booking.infrastructure.repository.entity.Booking;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

import static com.alten.booking.infrastructure.repository.entity.BookingStatus.CANCELLED;

@Component
@AllArgsConstructor
public class BookingEventConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookingEventConsumer.class);

    private final BookingService bookingService;

    @Bean
    public Consumer<Message<Booking>> bookingEventInput() {
        return message -> Mono.just(message.getPayload())
                .switchIfEmpty(Mono.error(new BusinessException("invalid event input " + message.getPayload())))
                .doOnNext(dto -> LOGGER.info("Booking message received: {}", dto))
                .flatMap(booking -> CANCELLED == booking.getStatus()
                        ? bookingService.cancel(booking)
                        : bookingService.createOrUpdate(booking))
                .doOnSuccess(booking -> LOGGER.info("Booking processed with success: {}", booking))
                .doOnError(error -> LOGGER.error("Error processing message: {}", error.getMessage()))
                .subscribe();
    }
}

