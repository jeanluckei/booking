package com.alten.booking.api.controller;

import com.alten.booking.api.dto.BookingRequestDTO;
import com.alten.booking.api.dto.BookingResponseDTO;
import com.alten.booking.business.service.BookingService;
import com.alten.booking.business.service.BookingServiceValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.alten.booking.stub.Stubs.bookingResponseDTOStub;
import static com.alten.booking.stub.Stubs.validBookingRequestDTOStub;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest
@ContextConfiguration(classes = TestConfiguration.class)
public class BookingControllerTest {

    private WebTestClient webClient;
    @MockBean
    private BookingService service;
    @MockBean
    private BookingServiceValidator serviceValidator;

    @BeforeEach
    void initClient() {
        webClient = WebTestClient.bindToController(new BookingController(service, serviceValidator)).build();
    }

    @Test
    void findByIdShouldWork() {
        when(service.findById(any())).thenReturn(Mono.just(bookingResponseDTOStub()));

        BookingResponseDTO responseBody = webClient.get().uri("/booking/123")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(BookingResponseDTO.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(responseBody);
    }

    @Test
    void findAllByUsernameShouldWork() {
        when(service.findAllByUsername(any()))
                .thenReturn(Flux.just(bookingResponseDTOStub(), bookingResponseDTOStub()));

        List<BookingResponseDTO> responseBody = webClient.get().uri("/booking")
                .header("username", "jean")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(BookingResponseDTO.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(responseBody);
    }

    @Test
    void createBookingShouldWork() {
        when(serviceValidator.validateHeaders(any(), any())).thenReturn(Mono.just(validBookingRequestDTOStub()));
        when(service.createBooking(any())).thenReturn(Mono.just(bookingResponseDTOStub()));

        BookingResponseDTO responseBody = webClient.post().uri("/booking")
                .body(Mono.just(validBookingRequestDTOStub()), BookingRequestDTO.class)
                .header("username", "jean")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(BookingResponseDTO.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(responseBody);
    }

    @Test
    void updateBookingShouldWork() {
        when(serviceValidator.validateHeaders(any(), any())).thenReturn(Mono.just(validBookingRequestDTOStub()));
        when(service.updateBookingById(any(), any())).thenReturn(Mono.just(bookingResponseDTOStub()));

        BookingResponseDTO responseBody = webClient.put().uri("/booking/123")
                .body(Mono.just(validBookingRequestDTOStub()), BookingRequestDTO.class)
                .header("username", "jean")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(BookingResponseDTO.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(responseBody);
    }

    @Test
    void cancelByIdShouldWork() {
        when(service.cancelById(any())).thenReturn(Mono.just(bookingResponseDTOStub()));

        BookingResponseDTO responseBody = webClient.delete().uri("/booking/123")
                .header("username", "jean")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(BookingResponseDTO.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(responseBody);
    }

    @Test
    void findBookingsByRoomNumberShouldWork() {
        when(service.findAllByRoomNumberAndStatus(any(), any()))
                .thenReturn(Flux.just(bookingResponseDTOStub(), bookingResponseDTOStub()));

        List<BookingResponseDTO> responseBody = webClient.get().uri("/booking/room/123?status=BOOKED")
                .header("username", "jean")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(BookingResponseDTO.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(responseBody);
    }

    @Test
    void findBookingAvailabilityShouldWork() {
        when(service.isValidRequestAndRoomAvailable(any(), any(), any())).thenReturn(Mono.just(Boolean.TRUE));

        Boolean responseBody = webClient.get()
                .uri("/booking/room/123/availability?startDate=2022-08-26&endDate=2022-08-27")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(Boolean.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(responseBody);
    }
}