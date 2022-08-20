package com.alten.booking.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomDTO {

    @NotNull(message = "Room number is required")
    private Long roomNumber;
    @NotNull(message = "Number of bedrooms is required")
    private Long bedrooms;
    @NotNull(message = "Number of beds is required")
    private Long beds;
    @NotNull(message = "Number of guests is required")
    private Long guests;
    @NotNull(message = "Number of suites is required")
    private Long suites;
    @NotNull(message = "Number of bathrooms is required")
    private Long bathrooms;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}