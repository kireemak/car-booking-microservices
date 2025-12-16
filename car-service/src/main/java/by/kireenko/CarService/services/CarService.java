package by.kireenko.CarService.services;

import by.kireenko.CarService.dto.CarDto;
import by.kireenko.CarService.dto.CarRequestDto;
import by.kireenko.CarService.error.NotValidResourceState;
import by.kireenko.CarService.error.ResourceNotFoundException;
import by.kireenko.CarService.kafka.CarEventProducer;
import by.kireenko.CarService.models.Car;
import by.kireenko.CarService.repositories.CarRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
public class CarService {

    private final CarRepository carRepository;
    private final CarEventProducer carEventProducer;
    private final CarService self;

    @Autowired
    public CarService(CarRepository carRepository, CarEventProducer carEventProducer, @Lazy CarService self) {
        this.carRepository = carRepository;
        this.carEventProducer = carEventProducer;
        this.self = self;
    }

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    public List<CarDto> getAllCarsDto() {
        List<CarDto> carDtoList = new ArrayList<>();
        getAllCars().forEach(car -> carDtoList.add(new CarDto(car)));
        return carDtoList;
    }

    public List<CarDto> getCarsDtoByIds(List<Long> ids) {
        return getAllCars().stream()
                .filter(car -> ids.contains(car.getId()))
                .map(CarDto::new)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "cars", key = "#id")
    public Car getCarById(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Car with id {} not found", id);
                    return new ResourceNotFoundException("Car", "id", id);
                });
    }

    @Transactional(readOnly = false)
    public Car getCarWithLockById(Long id) {
        return carRepository.findAndLockById(id)
                .orElseThrow(() -> {
                    log.warn("Car with id {} not found", id);
                    return new ResourceNotFoundException("Car", "id", id);
                });
    }

    @Transactional(readOnly = false)
    public Car createCar(CarRequestDto carRequestDto) {
        Car car = new Car();
        car.setBrand(carRequestDto.getBrand());
        car.setModel(carRequestDto.getModel());
        car.setYear(carRequestDto.getYear());
        car.setRentalPrice(carRequestDto.getRentalPrice());
        car.setStatus(carRequestDto.getStatus());
        Car savedCar = carRepository.save(car);
        carEventProducer.sendCarCreatedEvent(savedCar);
        return savedCar;
    }

    @CachePut(value = "cars", key = "#id")
    @Transactional(readOnly = false)
    public Car updateCar(Long id, CarRequestDto carRequestDto) {
        Car existingCar = self.getCarById(id);
        if (carRequestDto.getBrand() != null)
            existingCar.setBrand(carRequestDto.getBrand());
        if (carRequestDto.getModel() != null)
            existingCar.setModel(carRequestDto.getModel());
        if (carRequestDto.getYear() != null)
            existingCar.setYear(carRequestDto.getYear());
        if (carRequestDto.getRentalPrice() != null)
            existingCar.setRentalPrice(carRequestDto.getRentalPrice());
        if (carRequestDto.getStatus() != null)
            existingCar.setStatus(carRequestDto.getStatus());
        Car updatedCar = carRepository.save(existingCar);
        carEventProducer.sendCarUpdatedEvent(updatedCar);
        return updatedCar;
    }

    @CachePut(value = "cars", key = "#id")
    @Transactional(readOnly = false)
    public Car updateCar(Long id, Car updateCar) {
        Car existingCar = self.getCarById(id);
        if (updateCar.getBrand() != null)
            existingCar.setBrand(updateCar.getBrand());
        if (updateCar.getModel() != null)
            existingCar.setModel(updateCar.getModel());
        if (updateCar.getYear() != null)
            existingCar.setYear(updateCar.getYear());
        if (updateCar.getRentalPrice() != null)
            existingCar.setRentalPrice(updateCar.getRentalPrice());
        if (updateCar.getStatus() != null)
            existingCar.setStatus(updateCar.getStatus());
        Car updatedCar = carRepository.save(existingCar);
        carEventProducer.sendCarUpdatedEvent(updatedCar);
        return updatedCar;
    }

    @CacheEvict(value = "cars", key = "#id")
    @Transactional(readOnly = false)
    public void deleteCar(Long id) {
        carEventProducer.sendCarDeletedEvent(id);
        carRepository.deleteById(id);
    }

    public boolean isCarAvailable(Long carId) {
        Car car = self.getCarById(carId);
        return "Available".equalsIgnoreCase(car.getStatus());
    }

    @Transactional(readOnly = false)
    public Car reserveCar(Long carId) {
        Car car = carRepository.findAndLockById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car", "id", carId));

        if (!car.getStatus().equals("Available")) {
            log.error("Attempt to reserve an unavailable car {}. Status was {}", car.getId(), car.getStatus());
            throw new NotValidResourceState("Car", "status",
                    car.getStatus(), "Available");
        }

        car.setStatus("Rented");
        Car updatedCar = carRepository.save(car);

        carEventProducer.sendCarUpdatedEvent(updatedCar);

        return updatedCar;
    }

    @Transactional(readOnly = false)
    public Car releaseCar(Long carId) {
        Car car = carRepository.findAndLockById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car", "id", carId));

        car.setStatus("Available");
        Car updatedCar = carRepository.save(car);
        carEventProducer.sendCarUpdatedEvent(updatedCar);

        return updatedCar;
    }

    public List<Car> getAvailableCars() {
        return carRepository.findByStatus("Available");
    }

    public List<CarDto> getAvailableCarsDto() {
        List<CarDto> carDtoList = new ArrayList<>();
        getAvailableCars().forEach(car -> carDtoList.add(new CarDto(car)));
        return carDtoList;
    }
}

