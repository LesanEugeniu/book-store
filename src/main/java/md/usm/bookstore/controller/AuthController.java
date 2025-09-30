package md.usm.bookstore.controller;

import jakarta.validation.Valid;
import md.usm.bookstore.dto.RegistrationRequestDto;
import md.usm.bookstore.dto.UserDto;
import md.usm.bookstore.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody @Valid RegistrationRequestDto registrationRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(registrationRequest));
    }

}
