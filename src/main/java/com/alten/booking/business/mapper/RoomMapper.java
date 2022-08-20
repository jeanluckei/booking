package com.alten.booking.business.mapper;

import com.alten.booking.api.dto.RoomDTO;
import com.alten.booking.infrastructure.repository.entity.Room;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RoomMapper {

    Room toEntity(RoomDTO dto);
    RoomDTO toDto(Room entity);
    Room copyFromDTO(RoomDTO dto, @MappingTarget Room entity);

}