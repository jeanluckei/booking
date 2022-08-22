package com.alten.booking.business.service;

import com.alten.booking.api.dto.BookingRequestDTO;
import com.alten.booking.api.dto.BookingResponseDTO;
import com.alten.booking.business.exception.BusinessException;
import com.alten.booking.business.exception.NotFoundException;
import com.alten.booking.business.mapper.BookingMapper;
import com.alten.booking.infrastructure.messaging.producer.BookingEventProducer;
import com.alten.booking.infrastructure.repository.BookingRepository;
import com.alten.booking.infrastructure.repository.entity.Booking;
import com.alten.booking.infrastructure.repository.entity.BookingStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.alten.booking.stub.Stubs.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class BookingServiceTest {

    @InjectMocks
    private BookingService service;
    @Mock
    private BookingRepository repository;
    @Spy
    private BookingMapper mapper = Mappers.getMapper(BookingMapper.class);
    @Mock
    private BookingEventProducer producer;
    @Mock
    private BookingServiceValidator validator;

    @Test
    public void findByIdShouldWork() {
        doReturn(Mono.just(Booking.builder().build())).when(repository).findById(anyString());

        BookingResponseDTO response = service.findById(anyString()).block();

        assertNotNull(response);
    }

    @Test
    public void findByIdThrowsNotFound() {
        doReturn(Mono.empty()).when(repository).findById(anyString());

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> service.findById("232").block());
    }

    @Test
    public void findAllByUsernameShouldWork() {
        doReturn(Flux.just(Booking.builder().build())).when(repository).findAllByUsername(anyString());

        BookingResponseDTO response = service.findAllByUsername(anyString()).blockFirst();

        assertNotNull(response);
    }

    @Test
    public void findAllByUsernameThrowsNotFound() {
        doReturn(Flux.empty()).when(repository).findAllByUsername(anyString());

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> service.findAllByUsername("232").blockFirst());
    }

    @Test
    public void findAllByRoomNumberAndStatusShouldWork() {
        doReturn(Flux.just(Booking.builder().build())).when(repository).findAllByRoomNumberAndStatus(anyLong(), any());

        BookingResponseDTO response = service.findAllByRoomNumberAndStatus(anyLong(), any()).blockFirst();

        assertNotNull(response);
    }

    @Test
    public void findAllByRoomNumberAndStatusThrowsNotFound() {
        doReturn(Flux.empty()).when(repository).findAllByRoomNumberAndStatus(anyLong(), any());

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> service.findAllByRoomNumberAndStatus(anyLong(), any()).blockFirst());
    }

    @Test
    public void createBookingShouldWork() {
        BookingRequestDTO stub = validBookingRequestDTOStub();
        Booking bookingStub = bookingStub();

        doReturn(Mono.just(Boolean.TRUE)).when(validator).validateRoomExistsAndDatesAreCorrect(any());
        doReturn(Flux.empty()).when(repository).findAllByRoomNumberAndDatesConflict(any(), any(), any(), any());
        doReturn(Mono.just(bookingStub)).when(producer).bookingEventOutput(any());

        BookingResponseDTO response = service.createBooking(stub).block();

        assertNotNull(response);
        assertEquals(bookingStub.getUsername(), response.getUsername());
    }

    @Test
    public void createBookingThrowsValidatorException() {
        BookingRequestDTO stub = validBookingRequestDTOStub();

        doThrow(new BusinessException("Validator")).when(validator).validateRoomExistsAndDatesAreCorrect(any());

        assertThatExceptionOfType(BusinessException.class)
                .isThrownBy(() -> service.createBooking(stub).block())
                .withMessage("Validator");
    }

    @Test
    public void createBookingThrowsRoomNotAvailable() {
        BookingRequestDTO stub = validBookingRequestDTOStub();
        Booking bookingStub = bookingStub();

        doReturn(Mono.just(Boolean.TRUE)).when(validator).validateRoomExistsAndDatesAreCorrect(any());
        doReturn(Flux.just(bookingStub)).when(repository).findAllByRoomNumberAndDatesConflict(any(), any(), any(), any());

        assertThatExceptionOfType(BusinessException.class)
                .isThrownBy(() -> service.createBooking(stub).block())
                .withMessage("Room not available for given dates!");
    }

    @Test
    public void updateBookingShouldWork() {
        BookingRequestDTO stub = validBookingRequestDTOStub();
        Booking bookingStub = bookingStub();

        doReturn(Mono.just(bookingStub)).when(repository).findById(anyString());
        doReturn(Mono.just(Boolean.TRUE)).when(validator).validateRoomExistsAndDatesAreCorrect(any());
        doReturn(Flux.empty()).when(repository).findAllByRoomNumberAndDatesConflict(any(), any(), any(), any());
        doReturn(Mono.just(bookingStub)).when(producer).bookingEventOutput(any());

        BookingResponseDTO response = service.updateBookingById("6303c736c2f14e77828504ca", stub).block();

        assertNotNull(response);
        assertEquals(bookingStub.getUsername(), response.getUsername());
    }

    @Test
    public void updateBookingThrowsNotFound() {
        BookingRequestDTO stub = validBookingRequestDTOStub();

        doReturn(Mono.empty()).when(repository).findById(anyString());

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> service.updateBookingById("6303c736c2f14e77828504ca", stub).block())
                .withMessage("Booking not found for update!");
    }

    @Test
    public void updateBookingThrowsValidatorException() {
        BookingRequestDTO stub = validBookingRequestDTOStub();
        Booking bookingStub = bookingStub();

        doReturn(Mono.just(bookingStub)).when(repository).findById(anyString());
        doThrow(new BusinessException("Validator")).when(validator).validateRoomExistsAndDatesAreCorrect(any());

        assertThatExceptionOfType(BusinessException.class)
                .isThrownBy(() -> service.updateBookingById("6303c736c2f14e77828504ca", stub).block())
                .withMessage("Validator");
    }

    @Test
    public void updateBookingThrowsRoomNotAvailable() {
        BookingRequestDTO stub = validBookingRequestDTOStub();
        Booking bookingStub = bookingStub();

        doReturn(Mono.just(bookingStub)).when(repository).findById(anyString());
        doReturn(Mono.just(Boolean.TRUE)).when(validator).validateRoomExistsAndDatesAreCorrect(any());
        doReturn(Flux.just(bookingStub)).when(repository).findAllByRoomNumberAndDatesConflict(any(), any(), any(), any());

        assertThatExceptionOfType(BusinessException.class)
                .isThrownBy(() -> service.updateBookingById("6303c736c2f14e77828504ca", stub).block())
                .withMessage("Room not available for given dates!");
    }

    @Test
    public void cancelByIdShouldWork() {
        Booking bookingStub = bookingStub();

        doReturn(Mono.just(bookingStub)).when(repository).findById(anyString());
        doReturn(Mono.just(bookingStub)).when(producer).bookingEventOutput(any());

        BookingResponseDTO response = service.cancelById("6303c736c2f14e77828504ca").block();

        assertNotNull(response);
        assertEquals(bookingStub.getUsername(), response.getUsername());
    }

    @Test
    public void cancelByIdShouldThrowsNotFound() {
        doReturn(Mono.empty()).when(repository).findById(anyString());

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> service.cancelById("6303c736c2f14e77828504ca").block())
                .withMessage("Booking not found for cancelling!");
    }

    @Test
    public void isValidRequestAndRoomAvailableShouldBeTrue() {
        BookingRequestDTO stub = validBookingRequestDTOStub();

        doReturn(Mono.just(Boolean.TRUE)).when(validator).validateRoomExistsAndDatesAreCorrect(any());
        doReturn(Flux.empty()).when(repository).findAllByRoomNumberAndDatesConflict(any(), any(), any(), any());

        Boolean response = service.isValidRequestAndRoomAvailable(stub.getRoomNumber(), stub.getStartDate(), stub.getEndDate()).block();

        assertNotNull(response);
        assertTrue(response);
    }

    @Test
    public void isValidRequestAndRoomAvailableThrowsException() {
        BookingRequestDTO stub = validBookingRequestDTOStub();

        doReturn(Mono.just(Boolean.TRUE)).when(validator).validateRoomExistsAndDatesAreCorrect(any());
        doReturn(Flux.just(Booking.builder().build())).when(repository).findAllByRoomNumberAndDatesConflict(any(), any(), any(), any());

        assertThatExceptionOfType(BusinessException.class)
                .isThrownBy(() -> service.isValidRequestAndRoomAvailable(stub.getRoomNumber(), stub.getStartDate(), stub.getEndDate()).block())
                .withMessage("Room not available for given dates!");
    }

    @Test
    public void createOrUpdateShouldWork() {
        Booking bookingStub = bookingStub();

        doReturn(Flux.empty()).when(repository).findAllByRoomNumberAndDatesConflict(any(), any(), any(), any());
        doReturn(Mono.just(bookingStub)).when(repository).save(any());

        Booking response = service.createOrUpdate(bookingStub).block();

        assertNotNull(response);
        assertEquals(bookingStub.getUsername(), response.getUsername());
    }

    @Test
    public void createOrUpdateShouldUpdateOverbooked() {
        Booking bookingStub = bookingStub();

        doReturn(Flux.just(Booking.builder().build())).when(repository).findAllByRoomNumberAndDatesConflict(any(), any(), any(), any());
        doReturn(Mono.just(bookingStub)).when(repository).save(any());

        Booking response = service.createOrUpdate(bookingStub.setId(null)).block();

        assertNotNull(response);
        assertEquals(bookingStub.getUsername(), response.getUsername());
        assertEquals(BookingStatus.OVERBOOKED, response.getStatus());
    }

    @Test
    public void createOrUpdateThrowsException() {
        Booking bookingStub = bookingStub();

        doReturn(Flux.just(Booking.builder().build())).when(repository).findAllByRoomNumberAndDatesConflict(any(), any(), any(), any());
        doReturn(Mono.just(bookingStub)).when(repository).save(any());

        assertThatExceptionOfType(BusinessException.class)
                .isThrownBy(() -> service.createOrUpdate(bookingStub).block())
                .withMessage("Sending message: Could not update your booking!");
    }

    @Test
    public void cancelShouldWork() {
        Booking bookingStub = bookingStub();

        doReturn(Mono.just(bookingStub)).when(repository).save(any());

        Booking response = service.cancel(bookingStub).block();

        assertNotNull(response);
        assertEquals(bookingStub.getUsername(), response.getUsername());
    }
}
