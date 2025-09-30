package md.usm.bookstore.service;

import md.usm.bookstore.dto.BookDto;
import md.usm.bookstore.exception.StoreException;
import md.usm.bookstore.model.Author;
import md.usm.bookstore.model.Book;
import md.usm.bookstore.model.Category;
import md.usm.bookstore.repository.AuthorRepository;
import md.usm.bookstore.repository.BookRepository;
import md.usm.bookstore.repository.CategoryRepository;
import md.usm.bookstore.utils.ErrorType;
import md.usm.bookstore.utils.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static md.usm.bookstore.utils.ErrorType.VALIDATION_ERROR;


@Service
public class BookService {

    private final BookRepository bookRepository;
    private final CategoryService categoryService;
    private final AuthorService authorService;
    private final CategoryRepository categoryRepository;
    private final AuthorRepository authorRepository;
    private final Mapper mapper;

    public BookService(BookRepository bookRepository, CategoryService categoryService, AuthorService authorService, CategoryRepository categoryRepository, AuthorRepository authorRepository, Mapper mapper) {
        this.bookRepository = bookRepository;
        this.categoryService = categoryService;
        this.authorService = authorService;
        this.categoryRepository = categoryRepository;
        this.authorRepository = authorRepository;
        this.mapper = mapper;
    }

    @Transactional
    public BookDto create(BookDto bookDto) {
        checkAuthorAndAuthorId(bookDto);
        checkCategoryAndCategoryId(bookDto);

        Author author = authorService.getEntityById(bookDto.author().id());
        Category category = categoryService.getEntityById(bookDto.category().id());

        Book book = mapper.toEntity(bookDto);

        Book savedBook = bookRepository.save(book);

        author.addBook(book);
        authorRepository.save(author);

        category.addBook(book);
        categoryRepository.save(category);

        return mapper.toDto(savedBook);
    }

    public Page<BookDto> getAll(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .map(mapper::toDto);
    }

    public BookDto getById(Long id) {
        return mapper.toDto(getEntityById(id));
    }

    public Book getEntityById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new StoreException(
                        "Book not found with id " + id,
                        ErrorType.NOT_FOUND.name(),
                        HttpStatus.NOT_FOUND.value()
                ));
    }

    @Transactional
    public BookDto update(Long id, BookDto bookDto) {
        Book book = getEntityById(id);

        if (bookDto.title() != null) book.setTitle(bookDto.title());
        if (bookDto.isbn() != null) book.setIsbn(bookDto.isbn());
        if (bookDto.price() != null) book.setPrice(bookDto.price());
        if (bookDto.author() != null) {
            checkAuthorAndAuthorId(bookDto);
            Author author = authorService.getEntityById(bookDto.author().id());
            author.addBook(book);
            authorRepository.save(author);
        }
        if (bookDto.category() != null) {
            checkCategoryAndCategoryId(bookDto);
            Category category = categoryService.getEntityById(bookDto.category().id());
            category.addBook(book);
            categoryRepository.save(category);
        }

        Book saved = bookRepository.save(book);
        return mapper.toDto(saved);
    }

    public void delete(Long id) {
        Book existing = getEntityById(id);
        bookRepository.delete(existing);
    }

    private void checkAuthorAndAuthorId(BookDto bookDto) {
        if (bookDto.author() == null || bookDto.author().id() == null) {
            throw new StoreException(
                    "Author ID is required",
                    VALIDATION_ERROR.name(),
                    HttpStatus.BAD_REQUEST.value()
            );
        }
    }

    private void checkCategoryAndCategoryId(BookDto bookDto) {
        if (bookDto.category() == null || bookDto.category().id() == null) {
            throw new StoreException(
                    "Category ID is required",
                    VALIDATION_ERROR.name(),
                    HttpStatus.BAD_REQUEST.value()
            );
        }
    }

    public Set<Book> getAllByIds(List<Long> ids) {
        return new HashSet<>(bookRepository.findAllById(ids));
    }
}
