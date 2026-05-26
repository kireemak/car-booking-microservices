package by.kireenko.CarService.kafka;

import by.kireenko.CarService.dto.event.BookingEventDto;
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

    @KafkaListener(topics = "booking-events-topic", groupId = "car-service-group")
    public void handleBookingEvents(BookingEventDto event) {
        log.info("Received booking event for Booking ID: {} with status: {}", event.getId(), event.getStatus());

        if ("Completed".equalsIgnoreCase(event.getStatus()) || "Cancelled".equalsIgnoreCase(event.getStatus())) {
            log.info("Releasing car ID: {} because booking {} is {}", event.getCarId(), event.getId(), event.getStatus());
            try {
                carService.releaseCar(event.getCarId());
            } catch (Exception e) {
                log.error("Failed to release car ID: {}", event.getCarId(), e);
            }
        }
    }
}