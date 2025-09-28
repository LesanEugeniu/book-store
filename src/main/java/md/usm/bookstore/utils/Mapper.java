package md.usm.bookstore.utils;

import md.usm.bookstore.dto.*;
import md.usm.bookstore.model.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class Mapper {

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
        }
        return author;
    }

    public BookDto toDto(Book book) {
        if (book == null) return null;

        return new BookDto(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getPrice(),
                toDtoWithoutBooks(book.getAuthor()),
                toDtoWithoutBooks(book.getCategory()),
                book.getCreatedAt()
        );
    }

    public CategoryDto toDtoWithoutBooks(Category category) {
        if (category == null) return null;

        return new CategoryDto(
                category.getId(),
                category.getName(),
                null,
                category.getCreatedAt()
        );
    }

    public AuthorDto toDtoWithoutBooks(Author author) {
        if (author == null) return null;

        return new AuthorDto(
                author.getId(),
                author.getFirstName(),
                author.getLastName(),
                null,
                author.getCreatedAt()
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
        book.setAuthor(toEntity(dto.author()));
        book.setCategory(toEntity(dto.category()));
        return book;
    }

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

    public Category toEntity(CategoryDto dto) {
        if (dto == null) return null;

        Category category = new Category();
        category.setId(dto.id());
        category.setName(dto.name());
        if (dto.books() != null) {
            category.setBooks(dto.books().stream()
                    .map(this::toEntity)
                    .collect(Collectors.toList()));
        }
        return category;
    }

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
                order.getCreatedAt()
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
        }
        return order;
    }

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
