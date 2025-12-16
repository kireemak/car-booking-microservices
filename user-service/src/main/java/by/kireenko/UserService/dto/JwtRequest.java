package by.kireenko.UserService.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO for authentication request")
public class JwtRequest {
    @Schema(description = "Username", example = "John Doe")
    private String name;
    @Schema(description = "User password", example = "strongPassword123")
    private String password;
}
