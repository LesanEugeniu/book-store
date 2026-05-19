package md.usm.bookstore.controller;

import jakarta.validation.Valid;
import md.usm.bookstore.dto.AuthorDto;
import md.usm.bookstore.service.AuthorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/author")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    // ADMIN & MANAGER – create an author
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<AuthorDto> create(@RequestBody @Valid AuthorDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authorService.create(dto));
    }

    // All authenticated – list authors
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<AuthorDto>> getAll(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(authorService.getAll(PageRequest.of(page, size)));
    }

    // All authenticated – view an author
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AuthorDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(authorService.getById(id));
    }

    // ADMIN & MANAGER – update an author
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<AuthorDto> update(@PathVariable Long id, @RequestBody @Valid AuthorDto dto) {
        return ResponseEntity.ok(authorService.update(id, dto));
    }

    // ADMIN only – delete an author
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        authorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
