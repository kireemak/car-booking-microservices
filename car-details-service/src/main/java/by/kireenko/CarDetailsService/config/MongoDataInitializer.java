package by.kireenko.CarDetailsService.config;

import by.kireenko.CarDetailsService.models.CarDetails;
import by.kireenko.CarDetailsService.repositories.CarDetailsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class MongoDataInitializer implements CommandLineRunner {
    private final CarDetailsRepository carDetailsRepository;

    @Override
    public void run (String... args) {
        if (carDetailsRepository.count() != 0) {
            log.info("MongoDataInitializer: MongoDB database already contains data.");
            return;
        }

        log.info("MongoDataInitializer: MongoDB database is empty. Seeding initial data...");

        CarDetails toyota = new CarDetails();
        toyota.setCarId(1L);
        toyota.setDescription("A reliable and economical sedan, perfect for city driving.");
        toyota.setFeatures(Map.of(
                "Color", "Silver",
                "Transmission", "Automatic",
                "Air Conditioning", "Yes"
        ));
        CarDetails.Review toyotaReview = new CarDetails.Review();
        toyotaReview.setUsername("John Doe");
        toyotaReview.setRating(5);
        toyotaReview.setComment("Excellent car, very smooth ride!");
        toyota.setReviews(List.of(toyotaReview));

        CarDetails bmw = new CarDetails();
        bmw.setCarId(6L);
        bmw.setDescription("A sports sedan with excellent handling and dynamics.");
        bmw.setFeatures(Map.of(
                "Color", "Black",
                "Transmission", "Automatic",
                "Drive", "Rear-wheel",
                "Power", "184 hp"
        ));

        carDetailsRepository.saveAll(List.of(toyota, bmw));
        log.info("MongoDataInitializer: Initial data for MongoDB has been loaded successfully.");
    }
}
