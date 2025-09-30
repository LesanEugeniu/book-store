package md.usm.bookstore;

import md.usm.bookstore.dto.AuthorDto;
import md.usm.bookstore.model.Author;
import md.usm.bookstore.repository.*;
import md.usm.bookstore.service.AuthorService;
import md.usm.bookstore.utils.Mapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuthorServiceTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    BookRepository bookRepository;
    @Autowired
    AuthorRepository authorRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    OrderRepository orderRepository;

    @Autowired
    private Mapper mapper;

    @Autowired
    private AuthorService authorService;

    private AuthorDto authorDto;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        bookRepository.deleteAll();
        categoryRepository.deleteAll();
        authorRepository.deleteAll();
        userRepository.deleteAll();

        authorDto = new AuthorDto(
                null,
                "John",
                "Doe",
                null,
                null
        );
    }

    @Test
    void create_ShouldReturnDto() {
        AuthorDto result = authorService.create(authorDto);

        assertNotNull(result.id());
        assertEquals("John", result.firstName());
        assertEquals("Doe", result.lastName());
    }

    @Test
    void getById_ShouldReturnDto() {
        Author saved = authorRepository.save(mapper.toEntity(authorDto));

        AuthorDto result = authorService.getById(saved.getId());

        assertEquals(saved.getId(), result.id());
        assertEquals(saved.getFirstName(), result.firstName());
    }

    @Test
    void getAll_ShouldReturnPage() {
        authorRepository.save(mapper.toEntity(authorDto));

        Page<AuthorDto> page = authorService.getAll(PageRequest.of(0, 10));

        assertEquals(1, page.getTotalElements());
        assertEquals("John", page.getContent().get(0).firstName());
    }

    @Test
    void update_ShouldModifyAndReturnDto() {
        Author saved = authorRepository.save(mapper.toEntity(authorDto));

        AuthorDto updateDto = new AuthorDto(null, "Jane", "Smith", null, null);
        AuthorDto result = authorService.update(saved.getId(), updateDto);

        assertEquals("Jane", result.firstName());
        assertEquals("Smith", result.lastName());
    }

    @Test
    void delete_ShouldRemoveAuthor() {
        Author saved = authorRepository.save(mapper.toEntity(authorDto));

        authorService.delete(saved.getId());

        assertFalse(authorRepository.findById(saved.getId()).isPresent());
    }

}
