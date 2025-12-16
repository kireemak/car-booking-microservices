package by.kireenko.UserService.controllers;

import by.kireenko.UserService.dto.UpdateUserRequestDto;
import by.kireenko.UserService.dto.UserDto;
import by.kireenko.UserService.models.User;
import by.kireenko.UserService.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "User Controller", description = "Endpoints for managing users (Admin only)")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    @Operation(summary = "Get all users", description = "Returns a list of all users in the system.")
    public List<UserDto> getAllUsers() {
        return userService.getAllUsersDto();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Returns a single user by their ID.")
    public UserDto getUserById(@PathVariable Long id) {
        return new UserDto(userService.getUserById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "Create a new user", description = "Creates a new user. Requires ADMIN role.")
    public UserDto createUser(@RequestBody User user) {
        return new UserDto(userService.createUser(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing user", description = "Updates an existing user's information.")
    public UserDto updateUser(@PathVariable Long id, @RequestBody UpdateUserRequestDto userRequestDto) {
        return new UserDto(userService.updateUser(id, userRequestDto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user", description = "Deletes a user from the system by their ID.")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
