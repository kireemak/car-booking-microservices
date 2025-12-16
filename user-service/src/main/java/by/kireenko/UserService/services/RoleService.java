package by.kireenko.UserService.services;

import by.kireenko.UserService.error.ResourceNotFoundException;
import by.kireenko.UserService.models.Role;
import by.kireenko.UserService.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleService {
    private final RoleRepository roleRepository;

    @Cacheable(value = "roles")
    public Role getUserRole() {
        return roleRepository.findByName("ROLE_USER").get();
    }

    @Cacheable(value = "roles")
    public Role getAdminRole() {
        return roleRepository.findByName("ROLE_ADMIN").get();
    }
}
