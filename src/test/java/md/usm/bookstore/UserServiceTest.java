package md.usm.bookstore;

import md.usm.bookstore.dto.RegistrationRequestDto;
import md.usm.bookstore.dto.UserDto;
import md.usm.bookstore.exception.StoreException;
import md.usm.bookstore.model.Role;
import md.usm.bookstore.model.User;
import md.usm.bookstore.repository.UserRepository;
import md.usm.bookstore.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        RegistrationRequestDto registrationDto = new RegistrationRequestDto("john", "john@example.com", "pass123");
        UserDto createdUser = userService.create(registrationDto);
        testUser = userRepository.findByUsername("john").orElseThrow();
    }

    @Test
    void createUser_ShouldSaveUser() {
        UserDto userDto = userService.getById(testUser.getId());
        assertEquals("john", userDto.username());
        assertEquals("john@example.com", userDto.email());
    }

    @Test
    void login_ShouldReturnToken() {
        String token = userService.login("john", "pass123");
        assertNotNull(token);
        User userFromToken = userService.getUserByToken(token);
        assertEquals("john", userFromToken.getUsername());
    }

    @Test
    void loginWithInvalidPassword_ShouldThrow() {
        assertThrows(StoreException.class, () -> userService.login("john", "wrongpass"));
    }

    @Test
    void validateToken_ShouldReturnUser() {
        String token = userService.login("john", "pass123");
        User user = userService.validateToken(token);
        assertEquals("john", user.getUsername());
    }

    @Test
    void logout_ShouldInvalidateToken() {
        String token = userService.login("john", "pass123");
        userService.logout(token);
        assertNull(userService.getUserByToken(token));
        assertThrows(StoreException.class, () -> userService.validateToken(token));
    }

    @Test
    void validateRole_ShouldCheckRole() {
        String token = userService.login("john", "pass123");
        User user = userService.validateRole(token, Role.USER);
        assertEquals(Role.USER, user.getRole());

        assertThrows(StoreException.class, () -> userService.validateRole(token, Role.ADMIN));
    }

    @Test
    void validateRoles_ShouldCheckMultipleRoles() {
        String token = userService.login("john", "pass123");
        User user = userService.validateRoles(token, Role.USER, Role.ADMIN);
        assertEquals(Role.USER, user.getRole());

        assertThrows(StoreException.class, () -> userService.validateRoles(token, Role.ADMIN));
    }
}
