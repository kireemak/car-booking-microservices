package by.kireenko.CarService.dto;

import by.kireenko.CarService.models.Car;
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

    public CarDto(Car car) {
        this.id = car.getId();
        this.brand = car.getBrand();
        this.model = car.getModel();
        this.year = car.getYear();
        this.rentalPrice = car.getRentalPrice();
        this.status = car.getStatus();
    }
}
