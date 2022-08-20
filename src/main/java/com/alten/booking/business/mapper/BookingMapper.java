package com.alten.booking.business.mapper;

import com.alten.booking.api.dto.BookingDTO;
import com.alten.booking.infrastructure.repository.entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BookingMapper {

    Booking toEntity(BookingDTO dto);
    BookingDTO toDto(Booking entity);
    Booking copyFromDTO(BookingDTO bookingDTO, @MappingTarget Booking entity);

}