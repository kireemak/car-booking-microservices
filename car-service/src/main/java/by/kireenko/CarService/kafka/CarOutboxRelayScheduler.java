package by.kireenko.CarService.kafka;

import by.kireenko.CarService.models.OutboxEvent;
import by.kireenko.CarService.repositories.OutboxEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CarOutboxRelayScheduler {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void processOutboxEvents() {
        List<OutboxEvent> events = outboxEventRepository.findByProcessedFalseOrderByCreatedAtAsc();

        for (OutboxEvent outboxEvent : events) {
            try {
                String topic = switch (outboxEvent.getEventType()) {
                    case "carReserved" -> "car-reservation-success-topic";
                    case "carReservationFailed" -> "car-reservation-failed-topic";
                    case "car" -> "car-events-topic";
                    default -> throw new IllegalArgumentException("Unknown type: " + outboxEvent.getEventType());
                };

                Object payload = null;
                if (!outboxEvent.getPayload().equals("null")) {
                    Class<?> eventClass = getEventClass(outboxEvent.getEventType());
                    payload = objectMapper.readValue(outboxEvent.getPayload(), eventClass);
                }

                kafkaTemplate.send(topic, outboxEvent.getAggregateId(), payload);

                outboxEvent.setProcessed(true);
                outboxEventRepository.save(outboxEvent);

                log.info("Successfully processed outbox event: ID {}, Type {}", outboxEvent.getId(), outboxEvent.getEventType());

            } catch (Exception e) {
                log.error("Failed to process outbox event ID {}. It will be retried.", outboxEvent.getId(), e);
            }
        }
    }

    private Class<?> getEventClass(String eventType) {
        return switch (eventType) {
            case "carReserved" -> by.kireenko.CarService.dto.event.CarReservedEvent.class;
            case "carReservationFailed" -> by.kireenko.CarService.dto.event.CarReservationFailedEvent.class;
            case "car" -> by.kireenko.CarService.dto.CarDto.class;
            default -> throw new IllegalArgumentException("Unknown event type for deserialization: " + eventType);
        };
    }
}