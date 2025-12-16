package by.kireenko.BookingService.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO for car data")
public class CarDto {
    @Schema(description = "Car ID", example = "1")
    private long id;
    @Schema(description = "Car brand", example = "Toyota")
    private String brand;
    @Schema(description = "Car model", example = "Camry")
    private String model;
    @Schema(description = "Year of manufacture", example = "2022")
    private int year;
    @Schema(description = "Daily rental price", example = "55.50")
    private double rentalPrice;
    @Schema(description = "Current status of the car", example = "Available")
    private String status;
}
