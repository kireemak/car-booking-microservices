package by.kireenko.UserService.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class NotValidResourceState extends RuntimeException {
    public NotValidResourceState(String message) {
        super(message);
    }
    public NotValidResourceState(String resourceName, String resourceFieldName, Object resourceFieldValue,
                                 Object expFieldState) {
        super(resourceName + " has not valid " + resourceFieldName + ": " + resourceFieldValue
                + " (expected: " + expFieldState +")");
    }
}
