package by.kireenko.BookingService.kafka;

import by.kireenko.BookingService.config.KafkaTopicConfig;
import by.kireenko.BookingService.dto.BookingEventDto;
import by.kireenko.BookingService.dto.CarDto;
import by.kireenko.BookingService.models.Booking;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingEventPublisher {
    private final KafkaTemplate<String, BookingEventDto> kafkaTemplate;

    public void sendBookingCreatedEvent(Booking booking) {
        log.info("Sending Booking CREATED event for booking ID: {}", booking.getId());
        String key = booking.getId().toString();
        kafkaTemplate.send(KafkaTopicConfig.BOOKING_EVENTS_TOPIC, key, new BookingEventDto(booking));
    }

    public void sendBookingUpdatedEvent(Booking booking) {
        log.info("Sending Booking UPDATED event for booking ID: {}", booking.getId());
        String key = booking.getId().toString();
        kafkaTemplate.send(KafkaTopicConfig.BOOKING_EVENTS_TOPIC, key, new BookingEventDto(booking));
    }

    public void sendBookingDeletedEvent(Long bookingId) {
        log.info("Sending Booking DELETED event for booking ID: {}", bookingId);
        kafkaTemplate.send(KafkaTopicConfig.BOOKING_EVENTS_TOPIC, bookingId.toString(), null);
    }
}
