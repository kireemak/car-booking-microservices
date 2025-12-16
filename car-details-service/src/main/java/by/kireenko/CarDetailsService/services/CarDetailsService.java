package by.kireenko.CarDetailsService.services;

import by.kireenko.CarDetailsService.dto.AddReviewRequestDto;
import by.kireenko.CarDetailsService.dto.CarDetailsDto;
import by.kireenko.CarDetailsService.error.ResourceNotFoundException;
import by.kireenko.CarDetailsService.kafka.CarDetailsEventPublisher;
import by.kireenko.CarDetailsService.models.CarDetails;
import by.kireenko.CarDetailsService.repositories.CarDetailsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class CarDetailsService {
    private final CarDetailsRepository carDetailsRepository;
    private final CarDetailsEventPublisher carDetailsEventPublisher;
    private final WebClient.Builder webClientBuilder;
    private final MongoTemplate mongoTemplate;

    @Transactional(readOnly = true)
    @Cacheable(value = "cars_details", key = "#carId")
    public CarDetails getDetailsByCarId(Long carId) {
        checkCarExists(carId).block();
        return carDetailsRepository.findByCarId(carId).orElseGet(
                () -> {
                    CarDetails carDetails = new CarDetails();
                    carDetails.setCarId(carId);
                    return carDetails;
                }
        );
    }

    @Transactional
    @CachePut(value = "cars_details", key = "#carId")
    public CarDetails saveDetails(Long carId, CarDetailsDto detailsDto) {
        checkCarExists(carId).block();

        CarDetails carDetails = carDetailsRepository.findByCarId(carId).orElseGet(() -> {
            CarDetails newCarDetails = new CarDetails();
            newCarDetails.setCarId(carId);
            return newCarDetails;
        });

        carDetails.setFeatures(detailsDto.getFeatures());
        carDetails.setDescription(detailsDto.getDescription());

        CarDetails savedCarDetails = carDetailsRepository.save(carDetails);

        carDetailsEventPublisher.sendCarDetailsSavedEvent(savedCarDetails);

        return carDetailsRepository.save(carDetails);
    }

    @Transactional
    @CachePut(value = "cars_details", key = "#carId")
    public CarDetails addReview(Long carId, AddReviewRequestDto reviewDto) {
        checkCarExists(carId).block();
        String username = getCurrentUsername();

        CarDetails.Review newReview = new CarDetails.Review();
        newReview.setUsername(username);
        newReview.setComment(reviewDto.getComment());
        newReview.setRating(reviewDto.getRating());

        Query query = new Query(Criteria.where("carId").is(carId));

        Update update = new Update()
                .push("reviews", newReview)
                .setOnInsert("carId", carId);

        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true).upsert(true);

        CarDetails updatedCarDetails = mongoTemplate.findAndModify(query, update, options, CarDetails.class);

        carDetailsEventPublisher.sendCarDetailsUpdatedEvent(updatedCarDetails);

        return updatedCarDetails;
    }

    @Transactional
    @CacheEvict(value = "car_details", key = "#carId")
    public void deleteDetailsByCarId(Long carId) {
        carDetailsEventPublisher.sendCarDetailsDeletedEvent(carId);
        carDetailsRepository.deleteByCarId(carId);
        log.info("Deleted details for car ID: {}", carId);
    }

    private Mono<Void> checkCarExists(Long carId) {
        return webClientBuilder.build().get()
                .uri("http://car-service:/api/cars/{carId}", carId)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals,
                        response -> Mono.error(new ResourceNotFoundException("CarDetails", "car id", carId)))
                .bodyToMono(Void.class);
    }

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
