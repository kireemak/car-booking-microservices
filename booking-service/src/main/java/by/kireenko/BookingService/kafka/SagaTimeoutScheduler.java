package by.kireenko.BookingService.kafka;

import by.kireenko.BookingService.models.Booking;
import by.kireenko.BookingService.repositories.BookingRepository;
import by.kireenko.BookingService.services.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class SagaTimeoutScheduler {

    private final BookingRepository bookingRepository;
    private final BookingService bookingService;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void processTimedOutBookings() {
        LocalDateTime timeoutThreshold = LocalDateTime.now().minusMinutes(10);

        List<Booking> abandonedBookings = bookingRepository
                .findByStatusAndCreatedAtBefore("PENDING", timeoutThreshold);

        if (!abandonedBookings.isEmpty()) {
            log.warn("Found {} abandoned Saga transactions. Initiating timeout compensations.", abandonedBookings.size());

            for (Booking booking : abandonedBookings) {
                try {
                    bookingService.rejectBookingSaga(
                            booking.getId(),
                            "Saga Timeout: Car service failed to respond within 10 minutes."
                    );
                    log.info("Successfully timed out booking ID {}", booking.getId());
                } catch (Exception e) {
                    log.error("Failed to process timeout for booking ID {}", booking.getId(), e);
                }
            }
        }
    }
}
