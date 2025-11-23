package md.usm.bookstore.controller;

import jakarta.validation.Valid;
import md.usm.bookstore.dto.AuthorDto;
import md.usm.bookstore.model.Role;
import md.usm.bookstore.service.AuthorService;
import md.usm.bookstore.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/author")
public class AuthorController {

    private final AuthorService authorService;

    private final UserService userService;

    public AuthorController(AuthorService authorService, UserService userService) {
        this.authorService = authorService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<AuthorDto> create(@RequestHeader("Authorization") String token,
                                            @RequestBody @Valid AuthorDto dto) {
        userService.validateRole(token, Role.ADMIN);
        return ResponseEntity.status(HttpStatus.CREATED).body(authorService.create(dto));
    }

    @GetMapping
    public ResponseEntity<Page<AuthorDto>> getAll(@RequestHeader("Authorization") String token,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        userService.validateRoles(token, Role.USER, Role.ADMIN);
        return ResponseEntity.ok(authorService.getAll(PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorDto> getById(@RequestHeader("Authorization") String token,
                                             @PathVariable Long id) {
        userService.validateRoles(token, Role.USER, Role.ADMIN);
        return ResponseEntity.ok(authorService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthorDto> update(@RequestHeader("Authorization") String token,
                                            @PathVariable Long id,
                                            @RequestBody @Valid AuthorDto dto) {
        userService.validateRole(token, Role.ADMIN);
        return ResponseEntity.ok(authorService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@RequestHeader("Authorization") String token,
                                       @PathVariable Long id) {
        userService.validateRole(token, Role.ADMIN);
        authorService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
