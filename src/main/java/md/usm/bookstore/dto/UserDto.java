package md.usm.bookstore.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import md.usm.bookstore.model.Role;

import java.time.LocalDateTime;
import java.util.List;

public record UserDto(
        Long id,

        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        String username,

        @Email(message = "Email must be valid")
        String email,

        @Size(min = 8, message = "Password must be at least 8 characters long")
        @Pattern(regexp = ".*[A-Z].*", message = "Password must contain at least one uppercase letter")
        @Pattern(regexp = ".*[a-z].*", message = "Password must contain at least one lowercase letter")
        @Pattern(regexp = ".*\\d.*", message = "Password must contain at least one digit")
        @Pattern(regexp = ".*[@#$%^&+=!].*", message = "Password must contain at least one special character")
        String password,

        List<OrderDto> orders,

        Role role,

        LocalDateTime createdAt
) {
}
