package md.usm.bookstore.controller;

import jakarta.validation.Valid;
import md.usm.bookstore.dto.UserDto;
import md.usm.bookstore.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ADMIN only – list all users
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserDto>> getAll(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(userService.getAll(PageRequest.of(page, size)));
    }

    // ADMIN or MANAGER – get any user by id
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<UserDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    // ADMIN or self-USER – update own profile; ADMIN can update anyone
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<UserDto> update(@PathVariable Long id,
                                          @RequestBody @Valid UserDto dto,
                                          Principal principal) {
        return ResponseEntity.ok(userService.update(id, dto, principal));
    }

    // ADMIN only – delete user
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Any authenticated user – own profile
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDto> getProfile(Principal principal) {
        return ResponseEntity.ok(userService.getProfile(principal));
    }

    // ADMIN only – assign a set of roles to a user
    @PutMapping("/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> assignRoles(@PathVariable Long id,
                                               @RequestBody Set<String> roleNames) {
        return ResponseEntity.ok(userService.assignRoles(id, roleNames));
    }
}
