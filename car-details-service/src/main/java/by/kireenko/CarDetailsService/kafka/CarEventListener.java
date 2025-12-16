package by.kireenko.CarDetailsService.kafka;

import by.kireenko.CarDetailsService.services.CarDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CarEventListener {
    private static final String CAR_EVENTS_TOPIC = "car-events";
    private final CarDetailsService carDetailsService;

    @KafkaListener(topics = CAR_EVENTS_TOPIC, groupId = "car-details-group")
    public void consumeCarEvent(ConsumerRecord<String, Object> record) {
        String carId = record.key();
        Object carDto = record.value();

        if(carDto == null) {
            log.info("Received tombstone message for car ID: {}", carId);
            try {
                carDetailsService.deleteDetailsByCarId(Long.parseLong(carId));
                log.info("Successfully deleted local details for car ID: {}", carId);
            } catch (NumberFormatException e) {
                log.error("Received tombstone with invalid key: {}", carId, e);
            }
        }
    }

}
