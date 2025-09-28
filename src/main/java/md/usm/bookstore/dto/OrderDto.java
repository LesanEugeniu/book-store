package md.usm.bookstore.dto;

import java.time.LocalDateTime;
import java.util.List;

public record OrderDto(
        Long id,

        LocalDateTime orderDate,

        List<BookDto> books,

        LocalDateTime createdAt
) {
}
