package by.kireenko.BookingService.client;

import by.kireenko.BookingService.dto.CarDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CarServiceClient {
    private final WebClient.Builder webClientBuilder;
    private static final String CAR_SERVICE_URL = "http://car-service/api/cars";

    public CarDto getCarById(Long id) {
        return webClientBuilder.build().get()
                .uri(CAR_SERVICE_URL + "/{carId}", id)
                .retrieve()
                .bodyToMono(CarDto.class)
                .block();
    }

    public void reserveCar(Long id) {
        webClientBuilder.build().post()
                .uri(CAR_SERVICE_URL + "/{id}/reserve", id)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public void releaseCar(Long id) {
        webClientBuilder.build().post()
                .uri(CAR_SERVICE_URL + "/{id}/release", id)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public List<CarDto> getCarsByIds(List<Long> ids) {
        return webClientBuilder.build().post()
                .uri(CAR_SERVICE_URL + "/batch")
                .bodyValue(ids)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<CarDto>>() {})
                .block();
    }
}