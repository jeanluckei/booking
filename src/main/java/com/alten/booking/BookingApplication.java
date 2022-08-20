package com.alten.booking;

import com.alten.booking.infrastructure.repository.RoomRepository;
import com.alten.booking.infrastructure.repository.entity.Room;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@AllArgsConstructor
@SpringBootApplication
public class BookingApplication implements CommandLineRunner {

	private RoomRepository roomRepository;

	public static void main(String[] args) {
		SpringApplication.run(BookingApplication.class, args);
	}

	@Override
	public void run(String... args) {
		//For the purpose of the test, we assume the hotel has only one room available, room number 237.
		roomRepository.save(Room.builder()
				.roomNumber(237L)
				.guests(4L)
				.bedrooms(2L)
				.suites(1L)
				.beds(2L)
				.bathrooms(2L)
				.build())
				.subscribe();
	}

}
