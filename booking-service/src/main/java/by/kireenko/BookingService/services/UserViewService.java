package by.kireenko.BookingService.services;

import by.kireenko.BookingService.error.ResourceNotFoundException;
import by.kireenko.BookingService.models.UserView;
import by.kireenko.BookingService.repositories.UserViewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserViewService {
    private final UserViewRepository userViewRepository;

    @Transactional(readOnly = true)
    public UserView getCurrentUserView() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)) {

        }
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated.");
        }

        String name = authentication.getName();

        return userViewRepository.findByName(name).orElseThrow(() -> {
            log.warn("User with name {} not found", name);
            return new ResourceNotFoundException("User", "name", name);
        });
    }

    @Transactional(readOnly = true)
    public UserView getUserViewByName(String name) {
        return userViewRepository.findByName(name).orElseThrow(() -> {
            log.warn("User with name {} not found", name);
            return new ResourceNotFoundException("User", "name", name);
        });
    }
}
