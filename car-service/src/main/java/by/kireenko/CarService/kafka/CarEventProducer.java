package by.kireenko.CarService.kafka;

import by.kireenko.CarService.config.KafkaTopicConfig;
import by.kireenko.CarService.dto.CarDto;
import by.kireenko.CarService.models.Car;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class CarEventProducer {
    private final KafkaTemplate<String, CarDto> kafkaTemplate;

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
}
