package com.alten.booking.infrastructure.repository.entity;

public enum BookingStatus {

    BOOKED("Booked"),
    CANCELLED("Cancelled");

    private final String status;

    BookingStatus(final String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}