package by.kireenko.UserService.utils;

import by.kireenko.UserService.dto.UserDto;
import by.kireenko.UserService.models.User;
import by.kireenko.UserService.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class InitialDataPublisher implements CommandLineRunner {

    private final UserRepository userRepository;
    private final KafkaTemplate<String, UserDto> kafkaTemplate;

    @Override
    public void run (String... args) {
        log.info("InitialDataPublisher: Checking for existing users to publish to Kafka...");

        List<User> existingUsers = userRepository.findAll();

        if (existingUsers.isEmpty()) {
            log.info("InitialDataPublisher: No existing users found. Nothing to publish.");
            return;
        }

        log.info("InitialDataPublisher: Found {} existing users. Publishing them to the 'user-events' topic.", existingUsers.size());
        existingUsers.forEach((user) -> kafkaTemplate.send("user-events", new UserDto(user)));

        log.info("InitialDataPublisher: Finished publishing existing user data.");
    }
}
