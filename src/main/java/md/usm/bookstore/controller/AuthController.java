package md.usm.bookstore.controller;

import jakarta.validation.Valid;
import md.usm.bookstore.dto.LoginRequestDto;
import md.usm.bookstore.dto.RegistrationRequestDto;
import md.usm.bookstore.dto.UserDto;
import md.usm.bookstore.exception.StoreException;
import md.usm.bookstore.model.User;
import md.usm.bookstore.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody @Valid RegistrationRequestDto registrationRequest) {
        UserDto userDto = userService.create(registrationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto request) {
        try {
            String token = userService.login(request.username(), request.password());
            return ResponseEntity.ok(token);
        } catch (StoreException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        userService.logout(token);
        return ResponseEntity.ok("Logged out successfully");
    }

    @GetMapping("/me")
    public ResponseEntity<String> whoAmI(@RequestHeader("Authorization") String token) {
        User user = userService.getUserByToken(token);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        return ResponseEntity.ok("Logged in as: " + user.getUsername());
    }
}
