package by.kireenko.UserService.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO for creating a new user (Admin)")
public class CreateUserRequestDto {
    @Schema(description = "Username", requiredMode = Schema.RequiredMode.REQUIRED, example = "new_admin")
    private String name;

    @Schema(description = "User's email", requiredMode = Schema.RequiredMode.REQUIRED, example = "new.admin@example.com")
    private String email;

    @Schema(description = "User's phone number", requiredMode = Schema.RequiredMode.REQUIRED, example = "+1987654321")
    private String phoneNumber;

    @Schema(description = "User's password", requiredMode = Schema.RequiredMode.REQUIRED, example = "securePassword!_")
    private String password;
}
