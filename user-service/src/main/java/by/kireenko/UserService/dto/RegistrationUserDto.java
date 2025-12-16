package by.kireenko.UserService.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO for user registration")
public class RegistrationUserDto {
    @Schema(description = "Username", example = "John Doe")
    private String name;
    @Schema(description = "User password", example = "strongPassword123")
    private String password;
    @Schema(description = "Password confirmation", example = "strongPassword123")
    private String confirmPassword;
    @Schema(description = "User email", example = "john.doe@example.com")
    private String email;
    @Schema(description = "User phone number", example = "+1234567890")
    private String phone;
}
