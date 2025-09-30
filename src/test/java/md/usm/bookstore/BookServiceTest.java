package md.usm.bookstore;

import md.usm.bookstore.dto.AuthorDto;
import md.usm.bookstore.dto.BookDto;
import md.usm.bookstore.dto.CategoryDto;
import md.usm.bookstore.exception.StoreException;
import md.usm.bookstore.model.Author;
import md.usm.bookstore.model.Category;
import md.usm.bookstore.repository.AuthorRepository;
import md.usm.bookstore.repository.BookRepository;
import md.usm.bookstore.repository.CategoryRepository;
import md.usm.bookstore.service.AuthorService;
import md.usm.bookstore.service.BookService;
import md.usm.bookstore.service.CategoryService;
import md.usm.bookstore.utils.Mapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static md.usm.bookstore.utils.ErrorType.VALIDATION_ERROR;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BookServiceTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private Mapper mapper;

    @Autowired
    private BookService bookService;

    private Author author;
    private Category category;
    private BookDto bookDto;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
        authorRepository.deleteAll();
        categoryRepository.deleteAll();

        author = new Author();
        author.setFirstName("John");
        author.setLastName("Doe");
        authorRepository.save(author);

        category = new Category();
        category.setName("Fiction");
        categoryRepository.save(category);

        bookDto = new BookDto(
                null,
                "Test Book",
                "12345",
                50.0,
                mapper.toDto(author),
                mapper.toDto(category),
                null
        );
    }

    @Test
    void create_ShouldReturnDtoAndAddToAuthorAndCategory() {
        BookDto result = bookService.create(bookDto);

        assertNotNull(result.id());
        assertEquals("Test Book", result.title());

        AuthorDto authorDto = authorService.getById(author.getId());
        assertTrue(authorDto.books().stream().anyMatch(b -> b.id().equals(result.id())));

        CategoryDto updatedCategory = categoryService.getById(category.getId());
        assertTrue(updatedCategory.books().stream().anyMatch(b -> b.id().equals(result.id())));
    }

    @Test
    void create_ShouldThrowValidation_WhenAuthorIdIsNull() {
        BookDto dto = new BookDto(null, "Book", "123",
                20.0, null, mapper.toDto(category), null);
        StoreException ex = assertThrows(StoreException.class, () -> bookService.create(dto));
        assertEquals("Author ID is required", ex.getMessage());
        assertEquals(VALIDATION_ERROR.name(), ex.getErrorType());
    }

    @Test
    void getById_ShouldReturnDto() {
        BookDto saved = bookService.create(bookDto);

        BookDto result = bookService.getById(saved.id());
        assertEquals(saved.id(), result.id());
        assertEquals(saved.title(), result.title());
    }

    @Test
    void update_ShouldModifyBook() {
        BookDto saved = bookService.create(bookDto);
        BookDto updateDto = new BookDto(null, "Updated Book",
                null, null, null, null, null);

        BookDto result = bookService.update(saved.id(), updateDto);
        assertEquals("Updated Book", result.title());
    }

    @Test
    void delete_ShouldRemoveBook() {
        BookDto saved = bookService.create(bookDto);

        bookService.delete(saved.id());
        assertFalse(bookRepository.findById(saved.id()).isPresent());
    }
}
