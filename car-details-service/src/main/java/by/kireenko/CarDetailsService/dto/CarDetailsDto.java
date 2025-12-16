package by.kireenko.CarDetailsService.dto;

import by.kireenko.CarDetailsService.models.CarDetails;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Schema(description = "DTO for displaying car details, features, and reviews")
public class CarDetailsDto {

    @Schema(description = "A detailed description of the car")
    private String description;

    @Schema(description = "A flexible map of car features (e.g., 'Color': 'Red')", example = "{\"Color\": \"Red\", \"Engine\": \"V8\"}")
    private Map<String, String> features;

    @Schema(description = "A list of user reviews for the car")
    private List<ReviewDto> reviews;

    @Data
    @Schema(description = "DTO for a single user review")
    public static class ReviewDto {
        @Schema(description = "Username of the reviewer", example = "john_doe")
        private String username;
        @Schema(description = "Rating given by the user (1-5)", example = "5")
        private int rating;
        @Schema(description = "The user's comment", example = "Excellent car!")
        private String comment;
    }

    public CarDetailsDto(CarDetails carDetails) {
        this.description = carDetails.getDescription();
        this.features = carDetails.getFeatures();
        this.reviews = new ArrayList<>();

        List<CarDetails.Review> reviews = carDetails.getReviews();
        if (reviews != null) {
            reviews.forEach(review -> {
                ReviewDto reviewDto = new ReviewDto();
                reviewDto.setUsername(review.getUsername());
                reviewDto.setRating(review.getRating());
                reviewDto.setComment(review.getComment());
                this.reviews.add(reviewDto);
            });
        }
    }
}
