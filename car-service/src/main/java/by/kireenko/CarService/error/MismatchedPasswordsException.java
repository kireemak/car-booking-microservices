package by.kireenko.CarService.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class MismatchedPasswordsException extends RuntimeException {
  public MismatchedPasswordsException(String message) {
    super(message);
  }
}
