package md.usm.bookstore.controller;

import jakarta.validation.Valid;
import md.usm.bookstore.dto.BookDto;
import md.usm.bookstore.model.Role;
import md.usm.bookstore.service.BookService;
import md.usm.bookstore.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/book")
public class BookController {

    private final BookService bookService;
    private final UserService userService;

    public BookController(BookService bookService, UserService userService) {
        this.bookService = bookService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<BookDto> create(@RequestHeader("Authorization") String token,
                                          @RequestBody @Valid BookDto dto) {
        userService.validateRole(token, Role.ADMIN);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.create(dto));
    }

    @GetMapping
    public ResponseEntity<Page<BookDto>> getAll(@RequestHeader("Authorization") String token,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        userService.validateRoles(token, Role.USER, Role.ADMIN);
        return ResponseEntity.ok(bookService.getAll(PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDto> getById(@RequestHeader("Authorization") String token,
                                           @PathVariable Long id) {
        userService.validateRoles(token, Role.USER, Role.ADMIN);
        return ResponseEntity.ok(bookService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDto> update(@RequestHeader("Authorization") String token,
                                          @PathVariable Long id,
                                          @RequestBody @Valid BookDto dto) {
        userService.validateRole(token, Role.ADMIN);
        return ResponseEntity.ok(bookService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@RequestHeader("Authorization") String token,
                                       @PathVariable Long id) {
        userService.validateRole(token, Role.ADMIN);
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
