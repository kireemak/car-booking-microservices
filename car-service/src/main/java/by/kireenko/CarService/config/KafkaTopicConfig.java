package by.kireenko.CarService.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String CAR_EVENTS_TOPIC = "car-events";
    public static final String CAR_RESERVATION_SUCCESS_TOPIC = "car-reservation-success-topic";
    public static final String CAR_RESERVATION_FAILED_TOPIC = "car-reservation-failed-topic";

    @Bean
    public NewTopic carEventsTopic() {
        return TopicBuilder.name(CAR_EVENTS_TOPIC)
                .partitions(5)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic carReservationSuccessTopic() {
        return TopicBuilder.name(CAR_RESERVATION_SUCCESS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic carReservationFailedTopic() {
        return TopicBuilder.name(CAR_RESERVATION_FAILED_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
