package by.kireenko.CarService.kafka;

import by.kireenko.CarService.dto.event.BookingRequestedEvent;
import by.kireenko.CarService.services.CarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingEventListener {

    private final CarService carService;

    @KafkaListener(topics = "booking-requests-topic", groupId = "car-service-group")
    public void handleBookingRequest(BookingRequestedEvent event) {
        log.info("Processing Saga reservation for Booking ID: {}", event.getBookingId());
        carService.processSagaReservation(event.getBookingId(), event.getCarId());
    }
}