package by.kireenko.CarService.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingEventDto {
    private Long id;
    private Long carId;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
}
