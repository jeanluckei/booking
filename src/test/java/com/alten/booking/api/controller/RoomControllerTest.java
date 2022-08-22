package com.alten.booking.api.controller;

import com.alten.booking.api.dto.BookingRequestDTO;
import com.alten.booking.api.dto.BookingResponseDTO;
import com.alten.booking.api.dto.RoomDTO;
import com.alten.booking.business.service.BookingService;
import com.alten.booking.business.service.BookingServiceValidator;
import com.alten.booking.business.service.RoomService;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest
@ContextConfiguration(classes = TestConfiguration.class)
public class RoomControllerTest {

    private WebTestClient webClient;
    @MockBean
    private RoomService service;

    @BeforeEach
    void initClient() {
        webClient = WebTestClient.bindToController(new RoomController(service)).build();
    }

    @Test
    void findAllShouldWork() {
        when(service.findAll()).thenReturn(Flux.just(RoomDTO.builder().build()));

        List<RoomDTO> responseBody = webClient.get().uri("/room")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(RoomDTO.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(responseBody);
    }

    @Test
    void findByRoomNumberShouldWork() {
        when(service.findByRoomNumber(any())).thenReturn(Mono.just(RoomDTO.builder().build()));

        RoomDTO responseBody = webClient.get().uri("/room/237")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(RoomDTO.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(responseBody);
    }

    @Test
    void createRoomShouldWork() {
        when(service.createRoom(any())).thenReturn(Mono.just(RoomDTO.builder().build()));

        RoomDTO responseBody = webClient.post().uri("/room")
                .body(Mono.just(RoomDTO.builder().build()), RoomDTO.class)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(RoomDTO.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(responseBody);
    }

    @Test
    void updateRoomShouldWork() {
        when(service.updateRoom(anyString(), any())).thenReturn(Mono.just(RoomDTO.builder().build()));

        RoomDTO responseBody = webClient.put().uri("/room/123")
                .body(Mono.just(RoomDTO.builder().build()), RoomDTO.class)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(RoomDTO.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(responseBody);
    }
}