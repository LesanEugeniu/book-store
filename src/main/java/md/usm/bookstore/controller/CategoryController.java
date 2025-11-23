package md.usm.bookstore.controller;

import jakarta.validation.Valid;
import md.usm.bookstore.dto.CategoryDto;
import md.usm.bookstore.model.Role;
import md.usm.bookstore.service.CategoryService;
import md.usm.bookstore.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/category")
public class CategoryController {

    private final CategoryService categoryService;
    private final UserService userService;

    public CategoryController(CategoryService categoryService, UserService userService) {
        this.categoryService = categoryService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<CategoryDto> create(@RequestHeader("Authorization") String token,
                                              @RequestBody @Valid CategoryDto dto) {
        userService.validateRole(token, Role.ADMIN);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(dto));
    }

    @GetMapping
    public ResponseEntity<Page<CategoryDto>> getAll(@RequestHeader("Authorization") String token,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size) {
        userService.validateRoles(token, Role.USER, Role.ADMIN);
        return ResponseEntity.ok(categoryService.getAll(PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getById(@RequestHeader("Authorization") String token,
                                               @PathVariable Long id) {
        userService.validateRoles(token, Role.USER, Role.ADMIN);
        return ResponseEntity.ok(categoryService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> update(@RequestHeader("Authorization") String token,
                                              @PathVariable Long id,
                                              @RequestBody @Valid CategoryDto dto) {
        userService.validateRole(token, Role.ADMIN);
        return ResponseEntity.ok(categoryService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@RequestHeader("Authorization") String token,
                                       @PathVariable Long id) {
        userService.validateRole(token, Role.ADMIN);
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
