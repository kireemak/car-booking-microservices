package by.kireenko.CarService.kafka;

import by.kireenko.CarService.config.KafkaTopicConfig;
import by.kireenko.CarService.dto.CarDto;
import by.kireenko.CarService.dto.event.CarReservationFailedEvent;
import by.kireenko.CarService.dto.event.CarReservedEvent;
import by.kireenko.CarService.models.Car;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class CarEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendCarCreatedEvent(Car car) {
        log.info("Sending Car CREATED event for car ID: {}", car.getId());
        kafkaTemplate.send(KafkaTopicConfig.CAR_EVENTS_TOPIC, car.getId().toString(), new CarDto(car));
    }

    public void sendCarUpdatedEvent(Car car) {
        log.info("Sending Car UPDATED event for car ID: {}", car.getId());
        kafkaTemplate.send(KafkaTopicConfig.CAR_EVENTS_TOPIC, car.getId().toString(), new CarDto(car));
    }

    public void sendCarDeletedEvent(Long carId) {
        log.info("Sending Car DELETED event (tombstone) for car ID: {}", carId);
        kafkaTemplate.send(KafkaTopicConfig.CAR_EVENTS_TOPIC, carId.toString(), null);
    }

    public void sendCarReservedEvent(CarReservedEvent event) {
        log.info("Sending Car RESERVED event for Booking ID: {} and Car ID: {}",
                event.getBookingId(), event.getCarId());
        kafkaTemplate.send("car-reservation-success-topic", event.getBookingId().toString(), event);
    }

    public void sendCarReservationFailedEvent(CarReservationFailedEvent event) {
        log.warn("Sending Car RESERVATION FAILED event for Booking ID: {}. Reason: {}",
                event.getBookingId(), event.getReason());
        kafkaTemplate.send("car-reservation-failed-topic", event.getBookingId().toString(), event);
    }
}
