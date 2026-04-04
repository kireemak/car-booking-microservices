package by.kireenko.CarService.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestedEvent {
    private Long bookingId;
    private Long carId;
}
