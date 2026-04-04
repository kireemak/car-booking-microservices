package by.kireenko.BookingService.kafka;

import by.kireenko.BookingService.models.OutboxEvent;
import by.kireenko.BookingService.repositories.OutboxEventRepository;
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
public class OutboxRelayScheduler {

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
                    case "bookingRequested" -> "booking-requests-topic";
                    case "bookingCreated" -> "booking-events-topic";
                    case "bookingUpdated", "bookingCompleted" -> "booking-events-topic";
                    case "bookingDeleted" -> "booking-events-topic";
                    default -> throw new IllegalArgumentException("Unknown event type: " + outboxEvent.getEventType());
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
            case "bookingRequested" -> by.kireenko.BookingService.dto.event.BookingRequestedEvent.class;
            default -> by.kireenko.BookingService.dto.BookingEventDto.class;
        };
    }
}