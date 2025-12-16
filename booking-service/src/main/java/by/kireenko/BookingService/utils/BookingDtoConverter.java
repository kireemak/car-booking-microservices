package by.kireenko.BookingService.utils;

import by.kireenko.BookingService.dto.BookingDto;
import by.kireenko.BookingService.dto.CarDto;
import by.kireenko.BookingService.models.Booking;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class BookingDtoConverter {
    private final WebClient.Builder webClientBuilder;

    private CarDto getCarDtoById(Long carId) {
        return webClientBuilder.build().get()
                .uri("http://car-service/api/cars/{id}", carId)
                .retrieve()
                .bodyToMono(CarDto.class)
                .block();
    }

    public BookingDto convertToDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        CarDto carDto = getCarDtoById(booking.getCarId());
        return new BookingDto(booking, carDto);
    }
}
