package com.alten.booking.business.service;

import com.alten.booking.api.dto.BookingRequestDTO;
import com.alten.booking.api.dto.RoomDTO;
import com.alten.booking.business.exception.BusinessException;
import com.alten.booking.business.exception.NotFoundException;
import com.alten.booking.infrastructure.repository.entity.Booking;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

import static com.alten.booking.stub.Stubs.bookingStub;
import static com.alten.booking.stub.Stubs.validBookingRequestDTOStub;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class BookingServiceValidatorTest {

    @InjectMocks
    private BookingServiceValidator service;
    @Mock
    private RoomService roomService;

    @Test
    public void validateHeadersShouldWork() {
        BookingRequestDTO response =
                service.validateHeaders("jean", validBookingRequestDTOStub()).block();

        assertNotNull(response);
    }

    @Test
    public void validateHeadersThrowsException() {
        assertThatExceptionOfType(BusinessException.class)
                .isThrownBy(() -> service.validateHeaders("john", validBookingRequestDTOStub()).block())
                .withMessage("Different username between header and body!");
    }

    @Test
    public void validateRoomExistsAndDatesAreCorrectShouldWork() {
        doReturn(Mono.just(RoomDTO.builder().build())).when(roomService).findByRoomNumber(anyLong());

        Boolean response = service.validateRoomExistsAndDatesAreCorrect(bookingStub()).block();

        assertNotNull(response);
    }

    @Test
    public void validateRoomExistsAndDatesAreCorrectNotFound() {
        doThrow(new NotFoundException("Room 237 not found!")).when(roomService).findByRoomNumber(anyLong());

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> service.validateRoomExistsAndDatesAreCorrect(bookingStub()).block())
                .withMessage("Room 237 not found!");
    }

    @Test
    public void startDateAfterEndDateThrowsDateException() {
        doReturn(Mono.just(RoomDTO.builder().build())).when(roomService).findByRoomNumber(anyLong());

        Booking wrongDates = bookingStub();
        wrongDates.setStartDate(wrongDates.getEndDate().plusDays(1));

        assertThatExceptionOfType(BusinessException.class)
                .isThrownBy(() -> service.validateRoomExistsAndDatesAreCorrect(wrongDates).block())
                .withMessage("End date should be after start date");
    }

    @Test
    public void startDateIsTodayThrowsDateException() {
        doReturn(Mono.just(RoomDTO.builder().build())).when(roomService).findByRoomNumber(anyLong());

        Booking wrongDates = bookingStub();
        wrongDates.setStartDate(LocalDate.now());

        assertThatExceptionOfType(BusinessException.class)
                .isThrownBy(() -> service.validateRoomExistsAndDatesAreCorrect(wrongDates).block())
                .withMessage("Reservations start at least the next day of booking!");
    }

    @Test
    public void stayLongerThanThreeDaysThrowsDateException() {
        doReturn(Mono.just(RoomDTO.builder().build())).when(roomService).findByRoomNumber(anyLong());

        Booking wrongDates = bookingStub();
        wrongDates.setEndDate(wrongDates.getStartDate().plusDays(5));

        assertThatExceptionOfType(BusinessException.class)
                .isThrownBy(() -> service.validateRoomExistsAndDatesAreCorrect(wrongDates).block())
                .withMessage("The stay can’t be longer than 3 days!");
    }

    @Test
    public void stayWithinMoreThanThirtyDaysInAdvanceThrowsDateException() {
        doReturn(Mono.just(RoomDTO.builder().build())).when(roomService).findByRoomNumber(anyLong());

        Booking wrongDates = bookingStub();
        wrongDates.setStartDate(LocalDate.now().plusDays(50))
                .setEndDate(LocalDate.now().plusDays(50));

        assertThatExceptionOfType(BusinessException.class)
                .isThrownBy(() -> service.validateRoomExistsAndDatesAreCorrect(wrongDates).block())
                .withMessage("The stay can’t be reserved more than 30 days in advance!");
    }
}
