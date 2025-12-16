package by.kireenko.UserService.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO for updating an existing user (Admin)")
public class UpdateUserRequestDto {
    @Schema(description = "New username", example = "jane_doe_updated")
    private String name;

    @Schema(description = "New email", example = "jane.doe.updated@example.com")
    private String email;

    @Schema(description = "New phone number", example = "+1122334455")
    private String phoneNumber;
}
