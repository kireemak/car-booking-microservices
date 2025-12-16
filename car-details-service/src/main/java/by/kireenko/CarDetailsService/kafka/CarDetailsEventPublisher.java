package by.kireenko.CarDetailsService.kafka;

import by.kireenko.CarDetailsService.config.KafkaConfig;
import by.kireenko.CarDetailsService.config.KafkaTopicConfig;
import by.kireenko.CarDetailsService.dto.CarDetailsDto;
import by.kireenko.CarDetailsService.models.CarDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CarDetailsEventPublisher {

    private final KafkaTemplate<String, CarDetailsDto> kafkaTemplate;

    public void sendCarDetailsUpdatedEvent(CarDetails carDetails) {
        log.info("Sending CarDetails UPDATED event for car ID: {}", carDetails.getCarId());
        String key = carDetails.getCarId().toString();
        kafkaTemplate.send(KafkaTopicConfig.CAR_DETAILS_EVENTS_TOPIC, key, new CarDetailsDto(carDetails));
    }

    public void sendCarDetailsSavedEvent(CarDetails carDetails) {
        log.info("Sending CarDetails' Review ADDED event for car ID: {}", carDetails.getCarId());
        String key = carDetails.getCarId().toString();
        kafkaTemplate.send(KafkaTopicConfig.CAR_DETAILS_EVENTS_TOPIC, key, new CarDetailsDto(carDetails));
    }

    public void sendCarDetailsDeletedEvent(Long carId) {
        log.info("Sending CarDetails DELETED event for car ID: {}", carId);
        kafkaTemplate.send(KafkaTopicConfig.CAR_DETAILS_EVENTS_TOPIC, carId.toString(), null);
    }
}
