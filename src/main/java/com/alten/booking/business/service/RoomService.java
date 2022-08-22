package com.alten.booking.business.service;

import com.alten.booking.api.dto.RoomDTO;
import com.alten.booking.business.exception.BusinessException;
import com.alten.booking.business.exception.NotFoundException;
import com.alten.booking.business.mapper.RoomMapper;
import com.alten.booking.infrastructure.repository.RoomRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class RoomService {

    private final RoomRepository repository;
    private final RoomMapper mapper;

    public Flux<RoomDTO> findAll() {
        return repository.findAll()
                .map(mapper::toDto);
    }

    public Mono<RoomDTO> findByRoomNumber(Long roomNumber) {
        return repository.findByRoomNumber(roomNumber)
                .map(mapper::toDto)
                .switchIfEmpty(Mono.error(new NotFoundException("Room " + roomNumber + " not found!")));
    }

    public Mono<RoomDTO> createRoom(RoomDTO dto) {
        return validateRoomCreation(dto)
                .map(mapper::toEntity)
                .flatMap(repository::save)
                .map(mapper::toDto);
    }

    private Mono<RoomDTO> validateRoomCreation(RoomDTO dto) {
        return repository.existsByRoomNumber(dto.getRoomNumber())
                .filter(BooleanUtils::isTrue)
                .flatMap(aBoolean -> Mono.error(new BusinessException("Room already exists!")))
                .switchIfEmpty(Mono.just(dto))
                .cast(RoomDTO.class);
    }

    public Mono<RoomDTO> updateRoom(String id, RoomDTO dto) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Room " + id + " not found!")))
                .map(entity -> mapper.copyFromDTO(dto, entity))
                .flatMap(repository::save)
                .map(mapper::toDto);
    }
}