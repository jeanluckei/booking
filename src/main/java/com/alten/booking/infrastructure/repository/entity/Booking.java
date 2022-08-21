package com.alten.booking.infrastructure.repository.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Accessors(chain = true)
@Document(collection = "booking")
public class Booking {

    @Id
    @Indexed
    private String id;

    @Indexed
    private Long roomNumber;

    @Indexed
    private String username;

    private BookingStatus status;

    @Indexed
    private LocalDate startDate;

    @Indexed
    private LocalDate endDate;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime updatedDate;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

    public Booking booked() {
        return this.setStatus(BookingStatus.BOOKED);
    }

    public Booking pending() {
        return this.setStatus(BookingStatus.PENDING);
    }

    public Booking cancelled() {
        return this.setStatus(BookingStatus.CANCELLED);
    }

    public Booking overbooked() {
        return this.setStatus(BookingStatus.OVERBOOKED);
    }
}