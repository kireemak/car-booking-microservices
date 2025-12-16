package by.kireenko.CarService.repositories;

import by.kireenko.CarService.models.Car;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Car> findAndLockById(Long id);

    List<Car> findByStatus(String status);
}
