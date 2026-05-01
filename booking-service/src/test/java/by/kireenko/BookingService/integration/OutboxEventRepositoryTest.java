package by.kireenko.BookingService.integration;

import by.kireenko.BookingService.models.OutboxEvent;
import by.kireenko.BookingService.repositories.OutboxEventRepository;
import by.kireenko.BookingService.config.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OutboxEventRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private OutboxEventRepository outboxEventRepository;

    @Test
    void shouldFindUnprocessedEventsOrderedByCreation() {
        OutboxEvent event1 = new OutboxEvent(
                null,
                "Booking",
                "1",
                "bookingRequested",
                "{}",
                LocalDateTime.now().minusMinutes(5),
                false
        );

        OutboxEvent event2 = new OutboxEvent(
                null,
                "Booking",
                "2",
                "bookingCreated",
                "{}",
                LocalDateTime.now().minusMinutes(10),
                false
        );

        OutboxEvent processedEvent = new OutboxEvent(
                null,
                "Booking",
                "3",
                "bookingDeleted",
                "null",
                LocalDateTime.now(),
                true
        );

        outboxEventRepository.saveAll(List.of(event1, event2, processedEvent));

        List<OutboxEvent> unprocessedEvents = outboxEventRepository.findByProcessedFalseOrderByCreatedAtAsc();

        assertThat(unprocessedEvents).hasSize(2);
        assertThat(unprocessedEvents.get(0).getAggregateId()).isEqualTo("2");
        assertThat(unprocessedEvents.get(1).getAggregateId()).isEqualTo("1");
    }
}
