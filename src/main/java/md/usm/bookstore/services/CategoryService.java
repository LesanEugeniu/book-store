package md.usm.bookstore.services;

import md.usm.bookstore.dto.CategoryDto;
import md.usm.bookstore.exception.StoreException;
import md.usm.bookstore.model.Category;
import md.usm.bookstore.repository.CategoryRepository;
import md.usm.bookstore.utils.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static md.usm.bookstore.utils.ErrorType.NOT_FOUND;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final Mapper mapper;

    public CategoryService(CategoryRepository categoryRepository, Mapper mapper) {
        this.categoryRepository = categoryRepository;
        this.mapper = mapper;
    }

    @Transactional
    public CategoryDto create(CategoryDto dto) {
        Category category = mapper.toEntity(dto);
        return mapper.toDto(categoryRepository.save(category));
    }

    public Page<CategoryDto> getAll(Pageable pageable) {
        Page<Category> page = categoryRepository.findAllCategories(pageable);
        List<Category> categoriesWithBooks = categoryRepository.fetchBooksForCategories(page.getContent());
        return new PageImpl<>(categoriesWithBooks.stream().map(mapper::toDto).toList(), pageable, page.getTotalElements());
    }

    public CategoryDto getById(Long id) {
        Category category = categoryRepository.findByIdWithBooks(id);
        if (category == null) {
            throw new StoreException(
                    "Category not found with id " + id,
                    NOT_FOUND.name(),
                    HttpStatus.NOT_FOUND.value()
            );
        }
        return mapper.toDto(category);
    }

    public Category getEntityById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new StoreException(
                        "Category not found with id " + id,
                        NOT_FOUND.name(),
                        HttpStatus.NOT_FOUND.value()
                ));
    }

    @Transactional
    public CategoryDto update(Long id, CategoryDto dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new StoreException(
                        "Category not found with id " + id,
                        NOT_FOUND.name(),
                        HttpStatus.NOT_FOUND.value()
                ));

        if (dto.name() != null) category.setName(dto.name());

        Category saved = categoryRepository.save(category);
        return mapper.toDto(saved);
    }

    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new StoreException(
                        "Category not found with id " + id,
                        NOT_FOUND.name(),
                        HttpStatus.NOT_FOUND.value()
                ));
        categoryRepository.delete(category);
    }

}
