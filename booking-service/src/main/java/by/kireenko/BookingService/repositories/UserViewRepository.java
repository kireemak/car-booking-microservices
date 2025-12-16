package by.kireenko.BookingService.repositories;

import by.kireenko.BookingService.models.UserView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserViewRepository extends JpaRepository<UserView, Long> {
    Optional<UserView> findByName(String name);
}
