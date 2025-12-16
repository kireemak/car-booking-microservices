package by.kireenko.CarDetailsService.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String CAR_DETAILS_EVENTS_TOPIC = "car-details-events";

    @Bean
    public NewTopic carEventsTopic() {
        return TopicBuilder.name(CAR_DETAILS_EVENTS_TOPIC)
                .partitions(5)
                .replicas(1)
                .build();
    }
}
