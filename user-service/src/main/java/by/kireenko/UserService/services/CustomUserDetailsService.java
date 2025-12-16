package by.kireenko.UserService.services;

import by.kireenko.UserService.dto.RegistrationUserDto;
import by.kireenko.UserService.models.User;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserService userService;
    private final RoleService roleService;

    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.getUserByName(username);

        var authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
        return new org.springframework.security.core.userdetails.User(
                user.getName(),
                user.getPassword(),
                authorities
        );
    }

    public User createNewUser(RegistrationUserDto registrationUserDto) {
        User user = userFieldsInit(registrationUserDto);
        user.setRoles(List.of(roleService.getUserRole()));
        return userService.createUser(user);
    }

    public User createNewAdmin(RegistrationUserDto registrationUserDto) {
        User user = userFieldsInit(registrationUserDto);
        user.setRoles(List.of(roleService.getAdminRole()));
        return userService.createUser(user);
    }

    private User userFieldsInit(RegistrationUserDto registrationUserDto) {
        User user = new User();
        user.setName(registrationUserDto.getName());
        user.setPassword(registrationUserDto.getPassword());
        user.setEmail(registrationUserDto.getEmail());
        user.setPhoneNumber(registrationUserDto.getPhone());
        return user;
    }
}
