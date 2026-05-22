package by.kireenko.BookingService.integration;

import by.kireenko.BookingService.config.AbstractIntegrationTest;
import by.kireenko.BookingService.models.OutboxEvent;
import by.kireenko.BookingService.repositories.OutboxEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OutboxRelaySchedulerTest extends AbstractIntegrationTest {

    @Autowired
    private OutboxEventRepository outboxEventRepository;

    @BeforeEach
    void setUp() {
        outboxEventRepository.deleteAll();
    }

    @Test
    void shouldProcessOutboxEventsAndMarkAsProcessed() {
        OutboxEvent event = new OutboxEvent();
        event.setAggregateType("Booking");
        event.setAggregateId("123");
        event.setEventType("bookingCreated");
        event.setPayload("{\"id\": \"123\", \"status\": \"CREATED\"}");
        event.setProcessed(false);
        event.setCreatedAt(LocalDateTime.now());

        OutboxEvent savedEvent = outboxEventRepository.save(event);


        await()
                .atMost(10, TimeUnit.SECONDS)
                .pollInterval(1, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    OutboxEvent processedEvent = outboxEventRepository.findById(savedEvent.getId()).orElseThrow();
                    assertTrue(processedEvent.isProcessed(), "Планировщик должен был изменить статус события на processed = true");
                });
    }
}