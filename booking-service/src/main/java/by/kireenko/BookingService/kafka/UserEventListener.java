package by.kireenko.BookingService.kafka;

import by.kireenko.BookingService.dto.UserDto;
import by.kireenko.BookingService.models.UserView;
import by.kireenko.BookingService.repositories.UserViewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventListener {
    private final UserViewRepository userViewRepository;
    private static final String USER_EVENTS_TOPIC = "user-events";

    @KafkaListener(topics = USER_EVENTS_TOPIC, groupId = "car-booking-group")
    @Transactional
    public void consumeUserEvent(ConsumerRecord<String, UserDto> record) {
        String userIdStr = record.key();
        UserDto userDto = record.value();

        if (userDto == null) {
            log.info("Received tombstone message for user ID: {}", userIdStr);
            try {
                Long userId = Long.parseLong(userIdStr);
                userViewRepository.deleteById(userId);
                log.info("Successfully deleted local view for user ID: {}", userId);
            } catch (NumberFormatException e) {
                log.error("Received tombstone with invalid key: {}", userIdStr, e);
            }
            return;
        }

        log.info("Consumed user event for user: {}", userDto.getName());
        UserView userView = new UserView(userDto.getId(), userDto.getName(), userDto.getEmail(), userDto.getPhoneNumber());
        userViewRepository.save(userView);
        log.info("Successfully created/updated local view for user ID: {}", userDto.getId());
    }
}
