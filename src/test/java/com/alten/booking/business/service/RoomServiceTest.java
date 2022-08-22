package com.alten.booking.business.service;

import com.alten.booking.api.dto.RoomDTO;
import com.alten.booking.business.exception.BusinessException;
import com.alten.booking.business.exception.NotFoundException;
import com.alten.booking.business.mapper.RoomMapper;
import com.alten.booking.infrastructure.repository.RoomRepository;
import com.alten.booking.infrastructure.repository.entity.Room;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class RoomServiceTest {

    @InjectMocks
    private RoomService service;
    @Mock
    private RoomRepository repository;
    @Spy
    private RoomMapper mapper = Mappers.getMapper(RoomMapper.class);

    @Test
    public void createRoomShouldWork() {
        Room stub = Room.builder().build();
        doReturn(Mono.just(Boolean.FALSE)).when(repository).existsByRoomNumber(anyLong());
        doReturn(Mono.just(stub)).when(repository).save(any());

        RoomDTO response = service.createRoom(RoomDTO.builder().roomNumber(237L).build()).block();

        assertNotNull(response);
    }

    @Test
    public void createRoomThrowsException() {
        doReturn(Mono.just(Boolean.TRUE)).when(repository).existsByRoomNumber(anyLong());

        assertThatExceptionOfType(BusinessException.class)
                .isThrownBy(() -> service.createRoom(RoomDTO.builder().roomNumber(237L).build()).block())
                .withMessage("Room already exists!");
    }

    @Test
    public void updateRoomShouldWork() {
        Room stub = Room.builder().build();
        doReturn(Mono.just(stub)).when(repository).findById(anyString());
        doReturn(Mono.just(stub)).when(repository).save(any());

        RoomDTO response = service.updateRoom("123", RoomDTO.builder().build()).block();

        assertNotNull(response);
    }

    @Test
    public void updateRoomThrowsException() {
        doReturn(Mono.empty()).when(repository).findById(anyString());

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> service.updateRoom("123", RoomDTO.builder().roomNumber(237L).build()).block())
                .withMessage("Room 123 not found!");
    }

    @Test
    public void findByRoomNumberShouldWork() {
        doReturn(Mono.just(Room.builder().build())).when(repository).findByRoomNumber(anyLong());

        RoomDTO response = service.findByRoomNumber(anyLong()).block();

        assertNotNull(response);
    }

    @Test
    public void findByRoomNumberThrowsNotFound() {
        doReturn(Mono.empty()).when(repository).findByRoomNumber(anyLong());

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> service.findByRoomNumber(232L).block());
    }

    @Test
    public void findAllShouldWork() {
        doReturn(Flux.just(Room.builder().build())).when(repository).findAll();

        RoomDTO response = service.findAll().blockFirst();

        assertNotNull(response);
    }
}
