package by.kireenko.CarService.controllers;

import by.kireenko.CarService.dto.CarDto;
import by.kireenko.CarService.dto.CarRequestDto;
import by.kireenko.CarService.services.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Car Controller", description = "Endpoints for managing cars")
public class CarController {

    private final CarService carService;

    @Autowired
    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping
    @Operation(summary = "Get all cars", description = "Returns a list of all cars in the system.")
    public List<CarDto> getAllCars() {
        return carService.getAllCarsDto();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get car by ID", description = "Returns a single car by its ID.")
    public CarDto getCarById(@PathVariable Long id) {
        return new CarDto(carService.getCarById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "Create a new car (Admin only)", description = "Adds a new car to the system. Requires ADMIN role.")
    public CarDto createCar(@RequestBody CarRequestDto carRequestDto) {
        return new CarDto(carService.createCar(carRequestDto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Update a car (Admin only)", description = "Updates an existing car's information.")
    public CarDto updateCar(@PathVariable Long id, @RequestBody CarRequestDto carRequestDto) {
        return new CarDto(carService.updateCar(id, carRequestDto));
    }

    @PostMapping("/batch")
    @Operation(summary = "Get multiple cars by ID", description = "Returns a list of cars for a given list of IDs.")
    public List<CarDto> getCarsByIds(@RequestBody List<Long> ids) {
        return carService.getCarsDtoByIds(ids);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a car (Admin only)", description = "Deletes a car from the system by its ID.")
    public void deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
    }

    @GetMapping("/{id}/availability")
    @Operation(summary = "Check car availability", description = "Checks if a specific car is available for booking.")
    public boolean isCarAvailable(@PathVariable Long id) {
        return carService.isCarAvailable(id);
    }

    @GetMapping("/available")
    @Operation(summary = "Get available cars", description = "Returns a list of all cars with 'Available' status.")
    public List<CarDto> getAvailableCars() {
        return carService.getAvailableCarsDto();
    }

    @PostMapping("/{id}/reserve")
    @Operation(summary = "Reserve a car", description = "Atomically checks if a car is available and updates its status to 'Rented'.")
    public CarDto reserveCar(@PathVariable Long id) {
        return new CarDto(carService.reserveCar(id));
    }

    @PostMapping("/{id}/release")
    @Operation(summary = "Release a car", description = "Updates a car's status back to 'Available'.")
    public CarDto releaseCar(@PathVariable Long id) {
        return new CarDto(carService.releaseCar(id));
    }
}
