package by.kireenko.CarDetailsService.models;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@Document(collection = "car_details")
public class CarDetails implements Serializable {
    @Id
    private String id;

    private Long carId;

    private String description;

    private Map<String, String> features;

    private List<Review> reviews;

    @Data
    public static class Review implements Serializable {
        private String username;
        private int rating;
        private String comment;
    }
}
