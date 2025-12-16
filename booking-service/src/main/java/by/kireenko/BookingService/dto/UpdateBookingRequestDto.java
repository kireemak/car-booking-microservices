package by.kireenko.BookingService.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "DTO for updating an existing booking")
public class UpdateBookingRequestDto {
    @Schema(description = "New booking start date", example = "2025-10-01")
    private LocalDate startDate;
    @Schema(description = "New booking end date", example = "2025-10-05")
    private LocalDate endDate;
    @Schema(description = "New status for the booking", example = "Cancelled")
    private String status;
}
