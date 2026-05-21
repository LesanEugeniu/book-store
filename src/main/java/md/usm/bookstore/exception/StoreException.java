package md.usm.bookstore.exception;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class StoreException extends RuntimeException {

    private int statusCode;

    private String errorType;

    private final LocalDateTime throwTime = LocalDateTime.now();

    public StoreException(String message, String errorType, int statusCode) {
        super(message);
        this.statusCode = statusCode;
        this.errorType = errorType;
    }

}
