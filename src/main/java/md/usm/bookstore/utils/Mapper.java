package md.usm.bookstore.utils;

import md.usm.bookstore.dto.*;
import md.usm.bookstore.model.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class Mapper {

    /* =======================
     *     AUTHOR MAPPING
     * ======================= */

    public AuthorDto toDto(Author author) {
        if (author == null) return null;

        List<BookDto> booksDto = author.getBooks() == null ? null :
                author.getBooks().stream()
                        .map(this::toDtoWithoutAuthorCategory)
                        .collect(Collectors.toList());

        return new AuthorDto(
                author.getId(),
                author.getFirstName(),
                author.getLastName(),
                booksDto,
                author.getCreatedAt()
        );
    }

    public AuthorDto toAuthorDtoWithoutBooks(Author author) {
        if (author == null) return null;

        return new AuthorDto(
                author.getId(),
                author.getFirstName(),
                author.getLastName(),
                null,
                author.getCreatedAt()
        );
    }

    public Author toEntity(AuthorDto dto) {
        if (dto == null) return null;

        Author author = new Author();
        author.setId(dto.id());
        author.setFirstName(dto.firstName());
        author.setLastName(dto.lastName());

        if (dto.books() != null) {
            author.setBooks(dto.books().stream()
                    .map(this::toEntity)
                    .collect(Collectors.toList()));
        } else {
            author.setBooks(new ArrayList<>());
        }

        return author;
    }

    /* =======================
     *      BOOK MAPPING
     * ======================= */

    public BookDto toDto(Book book) {
        if (book == null) return null;

        return new BookDto(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getPrice(),
                book.getAuthors() == null ? null : book.getAuthors().stream().map(this::toAuthorDtoWithoutBooks).toList(),
                book.getCategory() == null ? null : toCategoryDtoWithoutBooks(book.getCategory()),
                book.getCreatedAt()
        );
    }

    public BookDto toDtoWithoutAuthorCategory(Book book) {
        if (book == null) return null;

        return new BookDto(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getPrice(),
                null,
                null,
                book.getCreatedAt()
        );
    }

    public Book toEntity(BookDto dto) {
        if (dto == null) return null;

        Book book = new Book();
        book.setId(dto.id());
        book.setTitle(dto.title());
        book.setIsbn(dto.isbn());
        book.setPrice(dto.price());

        if (dto.authors() != null) {
            book.setAuthors(dto.authors().stream()
                    .map(this::toEntity)
                    .collect(Collectors.toList()));
        } else {
            book.setAuthors(new ArrayList<>());
        }

        book.setCategory(toEntity(dto.category()));

        return book;
    }

    /* =======================
     *    CATEGORY MAPPING
     * ======================= */

    public CategoryDto toDto(Category category) {
        if (category == null) return null;

        List<BookDto> booksDto = category.getBooks() == null ? null :
                category.getBooks().stream()
                        .map(this::toDtoWithoutAuthorCategory)
                        .collect(Collectors.toList());

        return new CategoryDto(
                category.getId(),
                category.getName(),
                booksDto,
                category.getCreatedAt()
        );
    }

    public CategoryDto toCategoryDtoWithoutBooks(Category category) {
        if (category == null) return null;

        return new CategoryDto(
                category.getId(),
                category.getName(),
                null,
                category.getCreatedAt()
        );
    }

    public Category toEntity(CategoryDto dto) {
        if (dto == null) return null;

        Category category = new Category();
        category.setId(dto.id());
        category.setName(dto.name());

        if (dto.books() != null) {
            category.setBooks(dto.books().stream()
                    .map(this::toEntity)
                    .collect(Collectors.toList()));
        } else {
            category.setBooks(new ArrayList<>());
        }

        return category;
    }

    /* =======================
     *      ORDER MAPPING
     * ======================= */

    public OrderDto toDto(Order order) {
        if (order == null) return null;

        List<BookDto> booksDto = order.getBooks() == null ? null :
                order.getBooks().stream()
                        .map(this::toDtoWithoutAuthorCategory)
                        .collect(Collectors.toList());

        return new OrderDto(
                order.getId(),
                order.getOrderDate(),
                booksDto,
                order.getCreatedAt(),
                order.getStatus()
        );
    }

    public Order toEntity(OrderDto dto) {
        if (dto == null) return null;

        Order order = new Order();
        order.setId(dto.id());
        order.setOrderDate(dto.orderDate());

        if (dto.books() != null) {
            order.setBooks(dto.books().stream()
                    .map(this::toEntity)
                    .collect(Collectors.toSet()));
        } else {
            order.setBooks(Set.of());
        }

        return order;
    }

    /* =======================
     *       USER MAPPING
     * ======================= */

    public UserDto toDto(User user) {
        if (user == null) return null;

        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                null,
                user.getRole(),
                user.getCreatedAt()
        );
    }

    public User toEntity(UserDto dto) {
        if (dto == null) return null;

        User user = new User();
        user.setId(dto.id());
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setPassword(dto.password());

        return user;
    }
}
