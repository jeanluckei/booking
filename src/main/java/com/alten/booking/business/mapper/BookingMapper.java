package com.alten.booking.business.mapper;

import com.alten.booking.api.dto.BookingRequestDTO;
import com.alten.booking.api.dto.BookingResponseDTO;
import com.alten.booking.infrastructure.repository.entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BookingMapper {

    Booking toEntity(BookingRequestDTO dto);
    BookingResponseDTO toDto(Booking entity);
    Booking copyFromDTO(BookingRequestDTO bookingRequestDTO, @MappingTarget Booking entity);

}