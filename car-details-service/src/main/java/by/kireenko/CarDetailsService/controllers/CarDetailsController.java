package by.kireenko.CarDetailsService.controllers;

import by.kireenko.CarDetailsService.dto.AddReviewRequestDto;
import by.kireenko.CarDetailsService.dto.CarDetailsDto;
import by.kireenko.CarDetailsService.services.CarDetailsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/cars/{carId}/details")
@Tag(name = "Car Details Controller", description = "Endpoints for car features and reviews (MongoDB)")
@SecurityRequirement(name="bearerAuth")
public class CarDetailsController {
    private final CarDetailsService carDetailsService;

    @GetMapping
    @Operation(summary = "Get car details", description = "Returns detailed features and reviews for a car from MongoDB.")
    public CarDetailsDto getCarDetails(@PathVariable Long carId) {
        return new CarDetailsDto(carDetailsService.getDetailsByCarId(carId));
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Save car details (Admin only)", description = "Saves or updates features and reviews for a car.")
    public CarDetailsDto saveDetails(@PathVariable Long carId,@RequestBody CarDetailsDto carDetailsDto) {
        return new CarDetailsDto(carDetailsService.saveDetails(carId, carDetailsDto));
    }

    @PostMapping
    @Operation(summary = "Add a review", description = "Adds a review for a car. Requires user authentication.")
    public CarDetailsDto addReview(@PathVariable Long carId, @RequestBody AddReviewRequestDto requestDto) {
        return new CarDetailsDto(carDetailsService.addReview(carId, requestDto));
    }
}
