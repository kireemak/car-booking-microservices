package services;

import by.kireenko.UserService.dto.UpdateUserRequestDto;
import by.kireenko.UserService.dto.UserDto;
import by.kireenko.UserService.error.ResourceNotFoundException;
import by.kireenko.UserService.kafka.UserEventProducer;
import by.kireenko.UserService.models.Role;
import by.kireenko.UserService.models.User;
import by.kireenko.UserService.repositories.UserRepository;
import by.kireenko.UserService.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserEventProducer userEventProducer;

    @InjectMocks
    private UserService userService;

    private List<User> users;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(userService, "self", userService);

        users = List.of(
                new User(1L, "User1", "email1@test.com", "111", "pass1", List.of(new Role(1, "ROLE_USER"))),
                new User(2L, "User2", "email2@test.com", "222", "pass2", List.of(new Role(1, "ROLE_USER")))
        );
    }

    @Test
    public void getAllUsersDto_ShouldReturnList() {
        when(userRepository.findAll()).thenReturn(users);

        List<UserDto> result = userService.getAllUsersDto();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("User1");
        verify(userRepository).findAll();
    }

    @Test
    public void getUserById_WhenExists_ShouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(users.get(0)));

        User user = userService.getUserById(1L);

        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(1L);
    }

    @Test
    public void getUserById_WhenNotExists_ShouldThrowException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(99L));
    }

    @Test
    public void createUser_ShouldEncodePasswordAndSave() {
        User newUser = new User(null, "NewUser", "new@test.com", "333", "rawPassword", List.of());

        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> {
            User u = i.getArgument(0);
            u.setId(3L);
            return u;
        });

        User createdUser = userService.createUser(newUser);

        assertThat(createdUser.getPassword()).isEqualTo("encodedPassword");
        assertThat(createdUser.getId()).isEqualTo(3L);

        verify(userRepository).save(newUser);
    }

    @Test
    public void updateUser_ShouldUpdateAndSendEvent() {
        Long userId = 1L;
        UpdateUserRequestDto updateDto = new UpdateUserRequestDto();
        updateDto.setName("UpdatedName");

        User existingUser = users.get(0);
        User updatedUser = new User(1L, "UpdatedName", "email1@test.com", "111", "pass1", List.of(new Role(1, "ROLE_USER")));

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        User result = userService.updateUser(userId, updateDto);

        assertThat(result.getName()).isEqualTo("UpdatedName");

        verify(userRepository).save(existingUser);
        verify(userEventProducer).sendUserUpdatedEvent(result);
    }

    @Test
    public void deleteUser_ShouldDeleteAndSendEvent() {
        Long userId = 1L;
        doNothing().when(userRepository).deleteById(userId);

        userService.deleteUser(userId);

        verify(userEventProducer).sendUserDeletedEvent(userId);
        verify(userRepository).deleteById(userId);
    }
}