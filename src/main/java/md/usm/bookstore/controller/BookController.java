package md.usm.bookstore.controller;

import jakarta.validation.Valid;
import md.usm.bookstore.dto.BookDto;
import md.usm.bookstore.service.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/book")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // ADMIN & MANAGER – create a book
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<BookDto> create(@RequestBody @Valid BookDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.create(dto));
    }

    // All authenticated – browse catalogue
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<BookDto>> getAll(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(bookService.getAll(PageRequest.of(page, size)));
    }

    // All authenticated – view a book
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BookDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getById(id));
    }

    // ADMIN & MANAGER – update a book
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<BookDto> update(@PathVariable Long id, @RequestBody @Valid BookDto dto) {
        return ResponseEntity.ok(bookService.update(id, dto));
    }

    // ADMIN only – delete a book
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
