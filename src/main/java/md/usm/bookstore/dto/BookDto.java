package md.usm.bookstore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.List;

public record BookDto(
        Long id,

        @NotBlank(message = "Title is required")
        String title,

        @NotBlank(message = "ISBN is required")
        String isbn,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be positive")
        Double price,

        List<AuthorDto> authors,

        CategoryDto category,

        LocalDateTime createdAt
) {
}
