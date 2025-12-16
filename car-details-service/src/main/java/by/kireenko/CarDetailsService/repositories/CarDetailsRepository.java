package by.kireenko.CarDetailsService.repositories;

import by.kireenko.CarDetailsService.models.CarDetails;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarDetailsRepository extends MongoRepository<CarDetails, String> {
    Optional<CarDetails> findByCarId(Long carId);
    void deleteByCarId(Long carId);
}
