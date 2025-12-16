package by.kireenko.BookingService.dto;

import by.kireenko.BookingService.models.Booking;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "DTO for Kafka events related to a booking. Contains only the carId, not the full car object.")
public class BookingEventDto {

    @Schema(description = "Unique identifier for the booking", example = "101")
    private Long id;

    @Schema(description = "Details of the user who made the booking")
    private UserDto user;

    @Schema(description = "Identifier of the booked car", example = "5")
    private Long carId;

    @Schema(description = "Current status of the booking", example = "Created")
    private String status;

    @Schema(description = "Booking start date", example = "2025-10-20")
    private LocalDate startDate;

    @Schema(description = "Booking end date", example = "2025-10-25")
    private LocalDate endDate;

    public BookingEventDto(Booking booking) {
        this.id = booking.getId();
        if (booking.getUserView() != null) {
            this.user = new UserDto(booking.getUserView());
        }
        this.carId = booking.getCarId();
        this.status = booking.getStatus();
        this.startDate = booking.getStartDate();
        this.endDate = booking.getEndDate();
    }
}
