package by.kireenko.CarService.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO for creating or updating a car")
public class CarRequestDto {
    @Schema(description = "Brand of the car", requiredMode = Schema.RequiredMode.REQUIRED, example = "Audi")
    private String brand;

    @Schema(description = "Model of the car", requiredMode = Schema.RequiredMode.REQUIRED, example = "Q8")
    private String model;

    @Schema(description = "Year of manufacture", requiredMode = Schema.RequiredMode.REQUIRED, example = "2023")
    private Integer year;

    @Schema(description = "Daily rental price", requiredMode = Schema.RequiredMode.REQUIRED, example = "150.0")
    private Double rentalPrice;

    @Schema(description = "Current status of the car", requiredMode = Schema.RequiredMode.REQUIRED, example = "Available")
    private String status;
}
