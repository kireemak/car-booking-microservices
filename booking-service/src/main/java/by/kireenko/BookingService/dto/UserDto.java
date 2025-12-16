package by.kireenko.BookingService.dto;

import by.kireenko.BookingService.models.UserView;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO for user data")
public class UserDto {
    @Schema(description = "User ID", example = "1")
    private Long id;
    @Schema(description = "Username", example = "John Doe")
    private String name;
    @Schema(description = "User email", example = "john.doe@example.com")
    private String email;
    @Schema(description = "User phone number", example = "+1234567890")
    private String phoneNumber;

    public UserDto(UserView userView) {
        this.id = userView.getId();
        this.name = userView.getName();
        this.email = userView.getEmail();
        this.phoneNumber = userView.getPhoneNumber();
    }
}
