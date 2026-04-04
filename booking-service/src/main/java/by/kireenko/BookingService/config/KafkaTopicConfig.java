package by.kireenko.BookingService.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String BOOKING_EVENTS_TOPIC = "booking-events";
    public static final String BOOKING_REQUESTS_TOPIC = "booking-requests-topic";

    @Bean
    public NewTopic bookingEventsTopic() {
        return TopicBuilder.name(BOOKING_EVENTS_TOPIC)
                .partitions(5)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic bookingRequestsTopic() {
        return TopicBuilder.name(BOOKING_REQUESTS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
