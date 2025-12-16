package by.kireenko.UserService.kafka;

import by.kireenko.UserService.config.KafkaTopicConfig;
import by.kireenko.UserService.dto.UserDto;
import by.kireenko.UserService.models.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class UserEventProducer {
    private final KafkaTemplate<String, UserDto> kafkaTemplate;

    public void sendUserCreatedEvent(User user) {
        log.info("Sending User CREATED event for user ID: {}", user.getId());
        kafkaTemplate.send(KafkaTopicConfig.USER_EVENTS_TOPIC, user.getId().toString(), new UserDto(user));
    }

    public void sendUserUpdatedEvent(User user) {
        log.info("Sending User UPDATED event for user ID: {}", user.getId());
        kafkaTemplate.send(KafkaTopicConfig.USER_EVENTS_TOPIC, user.getId().toString(), new UserDto(user));
    }

    public void sendUserDeletedEvent(Long userId) {
        log.info("Sending User DELETED event (tombstone) for user ID: {}", userId);
        kafkaTemplate.send(KafkaTopicConfig.USER_EVENTS_TOPIC, userId.toString(), null);
    }
}
