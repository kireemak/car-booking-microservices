package by.kireenko.UserService.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "DTO for JWT response")
public class JwtResponse {
    @Schema(description = "JWT authentication token")
    private String token;
}
