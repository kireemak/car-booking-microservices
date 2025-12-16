package by.kireenko.CarService.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String CAR_EVENTS_TOPIC = "car-events";

    @Bean
    public NewTopic carEventsTopic() {
        return TopicBuilder.name(CAR_EVENTS_TOPIC)
                .partitions(5)
                .replicas(1)
                .build();
    }
}
