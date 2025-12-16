package by.kireenko.BookingService.dto;

import by.kireenko.BookingService.models.Booking;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO for booking data")
public class BookingDto {
    @Schema(description = "Booking ID", example = "1")
    private long id;
    @Schema(description = "User who made the booking")
    private UserDto user;
    @Schema(description = "The booked car")
    private CarDto car;
    @Schema(description = "Current status of the booking", example = "Created")
    private String status;
    @Schema(description = "Booking start date", example = "2025-08-15")
    private LocalDate startDate;
    @Schema(description = "Booking end date", example = "2025-08-20")
    private LocalDate endDate;

    public BookingDto(Booking booking, CarDto carDto) {
        this.id = booking.getId();
        if (booking.getUserView() != null) {
            this.user = new UserDto(booking.getUserView());
        }
        this.car = carDto;
        this.status = booking.getStatus();
        this.startDate = booking.getStartDate();
        this.endDate = booking.getEndDate();
    }
}
