package md.usm.bookstore.exception;

import java.time.LocalDateTime;

public class StoreException extends RuntimeException {

    private int statusCode;

    private String errorType;

    private final LocalDateTime throwTime = LocalDateTime.now();

    public StoreException(String message, String errorType, int statusCode) {
        super(message);
        this.statusCode = statusCode;
        this.errorType = errorType;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public LocalDateTime getThrowTime() {
        return throwTime;
    }
}
