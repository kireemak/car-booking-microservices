package by.kireenko.CarDetailsService.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO for adding a new review to a car")
public class AddReviewRequestDto {

    @Schema(description = "Rating from 1 to 5", requiredMode = Schema.RequiredMode.REQUIRED, example = "5")
    private int rating;

    @Schema(description = "User's comment", requiredMode = Schema.RequiredMode.REQUIRED, example = "Great car, very comfortable!")
    private String comment;
}
