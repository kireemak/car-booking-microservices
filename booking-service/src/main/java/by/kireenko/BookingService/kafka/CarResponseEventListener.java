package by.kireenko.BookingService.kafka;

import by.kireenko.BookingService.dto.event.CarReservationFailedEvent;
import by.kireenko.BookingService.dto.event.CarReservedEvent;
import by.kireenko.BookingService.services.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CarResponseEventListener {

    private final BookingService bookingService;

    @KafkaListener(topics = "car-reservation-success-topic", groupId = "booking-service-group")
    public void handleCarReserved(CarReservedEvent event) {
        bookingService.confirmBookingSaga(event.getBookingId());
    }

    @KafkaListener(topics = "car-reservation-failed-topic", groupId = "booking-service-group")
    public void handleCarReservationFailed(CarReservationFailedEvent event) {
        bookingService.rejectBookingSaga(event.getBookingId(), event.getReason());
    }
}