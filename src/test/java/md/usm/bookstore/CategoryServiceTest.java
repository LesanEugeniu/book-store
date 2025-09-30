package md.usm.bookstore;

import md.usm.bookstore.dto.CategoryDto;
import md.usm.bookstore.model.Category;
import md.usm.bookstore.repository.BookRepository;
import md.usm.bookstore.repository.CategoryRepository;
import md.usm.bookstore.service.CategoryService;
import md.usm.bookstore.utils.Mapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CategoryServiceTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private Mapper mapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BookRepository bookRepository;

    private CategoryDto categoryDto;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
        categoryRepository.deleteAll();

        categoryDto = new CategoryDto(
                null,
                "Fiction",
                Collections.emptyList(),
                null
        );
    }

    @Test
    void create_ShouldReturnDto() {
        CategoryDto result = categoryService.create(categoryDto);

        assertNotNull(result.id());
        assertEquals("Fiction", result.name());
    }

    @Test
    void getById_ShouldReturnDto() {
        Category saved = categoryRepository.save(mapper.toEntity(categoryDto));

        CategoryDto result = categoryService.getById(saved.getId());

        assertEquals(saved.getId(), result.id());
        assertEquals(saved.getName(), result.name());
    }

    @Test
    void getAll_ShouldReturnPage() {
        categoryRepository.save(mapper.toEntity(categoryDto));

        Page<CategoryDto> page = categoryService.getAll(PageRequest.of(0, 10));

        assertEquals(1, page.getTotalElements());
        assertEquals("Fiction", page.getContent().get(0).name());
    }

    @Test
    void update_ShouldModifyAndReturnDto() {
        Category saved = categoryRepository.save(mapper.toEntity(categoryDto));

        CategoryDto updateDto = new CategoryDto(null, "Non-Fiction", null, null);
        CategoryDto result = categoryService.update(saved.getId(), updateDto);

        assertEquals("Non-Fiction", result.name());
    }

    @Test
    void delete_ShouldRemoveCategory() {
        Category saved = categoryRepository.save(mapper.toEntity(categoryDto));

        categoryService.delete(saved.getId());

        assertFalse(categoryRepository.findById(saved.getId()).isPresent());
    }
}
