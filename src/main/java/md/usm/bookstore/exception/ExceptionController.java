package md.usm.bookstore.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import static md.usm.bookstore.utils.ErrorType.VALIDATION_ERROR;


@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(StoreException.class)
    public ResponseEntity<?> catchLibraryException(StoreException exception) {
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setErrorType(exception.getErrorType());
        errorDetail.setMessage(exception.getMessage());
        errorDetail.setTimestamp(exception.getThrowTime());
        errorDetail.setStatusCode(exception.getStatusCode());

        return new ResponseEntity<>(errorDetail, HttpStatusCode.valueOf(errorDetail.getStatusCode()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining("; "));

        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setErrorType(VALIDATION_ERROR.name());
        errorDetail.setMessage(message);
        errorDetail.setTimestamp(LocalDateTime.now());
        errorDetail.setStatusCode(HttpStatus.BAD_REQUEST.value());

        return new ResponseEntity<>(errorDetail, HttpStatus.BAD_REQUEST);
    }

}
