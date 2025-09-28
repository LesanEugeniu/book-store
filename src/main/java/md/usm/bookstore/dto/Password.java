package md.usm.bookstore.dto;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Password {
    String message() default "Invalid password";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

class PasswordValidator implements ConstraintValidator<Password, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return false;
        if (value.length() < 8) return false;
        if (!value.matches(".*[A-Z].*")) return false;
        if (!value.matches(".*[a-z].*")) return false;
        if (!value.matches(".*\\d.*")) return false;
        if (!value.matches(".*[@#$%^&+=!].*")) return false;
        return true;
    }
}
