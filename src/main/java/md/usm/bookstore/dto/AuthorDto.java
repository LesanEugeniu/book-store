package md.usm.bookstore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public record AuthorDto(
        Long id,

        @NotBlank(message = "First name cannot be blank")
        @Size(max = 50, message = "First name must be at most 50 characters")
        String firstName,

        @NotBlank(message = "Last name cannot be blank")
        @Size(max = 50, message = "Last name must be at most 50 characters")
        String lastName,

        List<BookDto> books,

        LocalDateTime createdAt
) {
}
