package md.usm.bookstore.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import static md.usm.bookstore.utils.ErrorType.*;


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

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(
            AccessDeniedException ex
    ) {

        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setErrorType(ACCESS_DENIED.name());
        errorDetail.setMessage("You do not have permission to access this resource");
        errorDetail.setTimestamp(LocalDateTime.now());
        errorDetail.setStatusCode(HttpStatus.FORBIDDEN.value());

        return new ResponseEntity<>(errorDetail, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({
            AuthenticationException.class,
            BadCredentialsException.class
    })
    public ResponseEntity<?> handleAuthenticationException(
            Exception ex
    ) {

        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setErrorType(UNAUTHORIZED.name());
        errorDetail.setMessage("Invalid username or password");
        errorDetail.setTimestamp(LocalDateTime.now());
        errorDetail.setStatusCode(HttpStatus.UNAUTHORIZED.value());

        return new ResponseEntity<>(errorDetail, HttpStatus.UNAUTHORIZED);
    }

}
