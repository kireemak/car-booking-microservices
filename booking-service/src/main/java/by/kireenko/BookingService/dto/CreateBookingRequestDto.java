package by.kireenko.BookingService.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema
public class CreateBookingRequestDto {
    @Schema(description = "ID of the car to be booked", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long carId;
    @Schema(description = "Booking start date", requiredMode = Schema.RequiredMode.REQUIRED, example = "2025-09-15")
    private LocalDate startDate;
    @Schema(description = "Booking end date", requiredMode = Schema.RequiredMode.REQUIRED, example = "2025-09-20")
    private LocalDate endDate;
}
