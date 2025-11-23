package md.usm.bookstore.controller;

import jakarta.validation.Valid;
import md.usm.bookstore.dto.UserDto;
import md.usm.bookstore.model.Role;
import md.usm.bookstore.model.User;
import md.usm.bookstore.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Page<UserDto>> getAll(@RequestHeader("Authorization") String token,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        userService.validateRole(token, Role.ADMIN);
        return ResponseEntity.ok(userService.getAll(PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(@RequestHeader("Authorization") String token,
                                           @PathVariable Long id) {
        userService.validateRole(token, Role.ADMIN);
        return ResponseEntity.ok(userService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> update(@RequestHeader("Authorization") String token,
                                          @PathVariable Long id,
                                          @RequestBody @Valid UserDto dto) {
        User user = userService.getUserByToken(token);
        return ResponseEntity.ok(userService.update(id, dto, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@RequestHeader("Authorization") String token,
                                       @PathVariable Long id) {
        userService.validateRole(token, Role.ADMIN);
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDto> getProfile(@RequestHeader("Authorization") String token) {
        User user = userService.getUserByToken(token);
        return ResponseEntity.ok(userService.getProfile(user));
    }

}
