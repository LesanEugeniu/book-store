package md.usm.bookstore.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.List;

public record CategoryDto(
        Long id,

        @NotBlank(message = "Category name is required")
        String name,

        List<BookDto> books,

        LocalDateTime createdAt
) {
}
