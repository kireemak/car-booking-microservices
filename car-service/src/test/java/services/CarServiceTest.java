package services;

import by.kireenko.CarService.dto.CarDto;
import by.kireenko.CarService.dto.CarRequestDto;
import by.kireenko.CarService.error.ResourceNotFoundException;
import by.kireenko.CarService.kafka.CarEventProducer;
import by.kireenko.CarService.models.Car;
import by.kireenko.CarService.repositories.CarRepository;
import by.kireenko.CarService.services.CarService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CarServiceTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private CarEventProducer carEventProducer;

    @InjectMocks
    private CarService carService;

    private List<Car> cars;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(carService, "self", carService);

        cars = List.of(
                new Car(1L, "Toyota", "Corolla", 2020, 40.0, "Available"),
                new Car(2L, "Honda", "Civic", 2019, 45.0, "Available"),
                new Car(3L, "Ford", "Focus", 2021, 50.0, "Rented"),
                new Car(4L, "Chevrolet", "Malibu", 2018, 35.0, "Rented")
        );
    }

    @Test
    public void getAllCarsDto_ShouldReturnAllCarsDtoList() {
        when(carRepository.findAll()).thenReturn(cars);

        List<CarDto> carDtoList = carService.getAllCarsDto();

        assertThat(carDtoList).isNotNull();
        assertThat(carDtoList).hasSize(4);
        assertThat(carDtoList.get(0).getBrand()).isEqualTo("Toyota");
        verify(carRepository, times(1)).findAll();
    }

    @Test
    public void getCarById_WhenCarExists_ShouldReturnCar() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(cars.get(0)));

        Car car = carService.getCarById(1L);

        assertThat(car).isNotNull();
        assertThat(car.getId()).isEqualTo(1L);
        assertThat(car.getBrand()).isEqualTo("Toyota");
        verify(carRepository, times(1)).findById(1L);
    }

    @Test
    public void getCarById_WhenCarDoesNotExist_ShouldThrowException() {
        when(carRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> carService.getCarById(999L));
    }

    @Test
    public void getAvailableCarsDto_ShouldReturnOnlyAvailableCars() {
        List<Car> availableCars = cars.stream()
                .filter(c -> "Available".equals(c.getStatus()))
                .collect(Collectors.toList());

        when(carRepository.findByStatus("Available")).thenReturn(availableCars);

        List<CarDto> result = carService.getAvailableCarsDto();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getStatus()).isEqualTo("Available");
        assertThat(result.get(1).getStatus()).isEqualTo("Available");
    }

    @Test
    public void createCar_ShouldSaveAndSendEvent() {
        CarRequestDto requestDto = new CarRequestDto();
        requestDto.setBrand("NewBrand");
        requestDto.setModel("NewModel");
        requestDto.setYear(2022);
        requestDto.setRentalPrice(100.0);
        requestDto.setStatus("Available");

        Car savedCar = new Car(5L, "NewBrand", "NewModel", 2022, 100.0, "Available");

        when(carRepository.save(any(Car.class))).thenReturn(savedCar);

        Car result = carService.createCar(requestDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(5L);

        verify(carRepository).save(any(Car.class));
        verify(carEventProducer).sendCarCreatedEvent(savedCar);
    }

    @Test
    public void updateCar_ShouldUpdateAndSendEvent() {
        Long carId = 1L;
        CarRequestDto updateRequest = new CarRequestDto();
        updateRequest.setRentalPrice(55.0);

        Car existingCar = cars.get(0);
        Car updatedCar = new Car(1L, "Toyota", "Corolla", 2020, 55.0, "Available");

        when(carRepository.findById(carId)).thenReturn(Optional.of(existingCar));
        when(carRepository.save(any(Car.class))).thenReturn(updatedCar);

        Car result = carService.updateCar(carId, updateRequest);

        assertThat(result.getRentalPrice()).isEqualTo(55.0);

        verify(carRepository).save(any(Car.class));
        verify(carEventProducer).sendCarUpdatedEvent(updatedCar);
    }

    @Test
    public void deleteCar_ShouldDeleteAndSendEvent() {
        Long carId = 1L;
        doNothing().when(carRepository).deleteById(carId);

        carService.deleteCar(carId);

        verify(carEventProducer).sendCarDeletedEvent(carId);
        verify(carRepository).deleteById(carId);
    }
}