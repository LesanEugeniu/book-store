package md.usm.bookstore.service;

import md.usm.bookstore.dto.RegistrationRequestDto;
import md.usm.bookstore.dto.UserDto;
import md.usm.bookstore.exception.StoreException;
import md.usm.bookstore.model.Role;
import md.usm.bookstore.model.User;
import md.usm.bookstore.repository.UserRepository;
import md.usm.bookstore.utils.Mapper;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static md.usm.bookstore.utils.ErrorType.*;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final Mapper mapper;
    private final Map<String, SessionToken> sessions = new HashMap<>();
    private static final long TOKEN_VALIDITY_MS = 20 * 60 * 1000;

    public UserService(UserRepository userRepository, Mapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Transactional
    public UserDto create(RegistrationRequestDto registrationRequestDto) {
        checkUsernameUnique(registrationRequestDto.username());

        User user = new User();
        user.setRole(Role.USER);
        user.setPassword(hashPassword(registrationRequestDto.password()));
        user.setUsername(registrationRequestDto.username());
        user.setEmail(registrationRequestDto.email());

        User saved = userRepository.save(user);
        return mapper.toDto(saved);
    }

    public Page<UserDto> getAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(mapper::toDto);
    }

    public UserDto getById(Long id) {
        return mapper.toDto(getEntityById(id));
    }

    public User getEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new StoreException(
                        "User not found with id " + id,
                        NOT_FOUND.name(),
                        HttpStatus.NOT_FOUND.value()
                ));
    }

    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new StoreException(
                        "User not found with username " + username,
                        NOT_FOUND.name(),
                        HttpStatus.NOT_FOUND.value()
                ));
    }

    public UserDto getProfile(User user) {
        return mapper.toDto(user);
    }

    @Transactional
    public UserDto update(Long id, UserDto userDto, User userAuthenticated) {
        User userToUpdate = getEntityById(id);

        if (!userToUpdate.getUsername().equals(userAuthenticated.getUsername()) && userAuthenticated.getRole().equals(Role.USER)) {
            throw new StoreException(
                    "No permission to perform this action",
                    FORBIDDEN.name(),
                    HttpStatus.FORBIDDEN.value()
            );
        }

        checkUsernameUnique(userDto.username());

        if (userDto.username() != null) userToUpdate.setUsername(userDto.username());
        if (userDto.email() != null) userToUpdate.setEmail(userDto.email());
        if (userDto.password() != null) userToUpdate.setPassword(hashPassword(userDto.password()));

        return mapper.toDto(userRepository.save(userToUpdate));
    }

    public void delete(Long id) {
        User user = getEntityById(id);
        userRepository.delete(user);
    }

    private void checkUsernameUnique(String username) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new StoreException(
                    "Username already exists",
                    VALIDATION_ERROR.name(),
                    HttpStatus.BAD_REQUEST.value()
            );
        }
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public boolean verifyPassword(String rawPassword, String hashedPassword) {
        return BCrypt.checkpw(rawPassword, hashedPassword);
    }

    public String login(String username, String password) {
        User user = getByUsername(username);
        if (!verifyPassword(password, user.getPassword())) {
            throw new StoreException(
                    "Invalid credentials",
                    FORBIDDEN.name(),
                    HttpStatus.FORBIDDEN.value()
            );
        }

        String token = UUID.randomUUID().toString();
        long expiresAt = System.currentTimeMillis() + TOKEN_VALIDITY_MS;
        sessions.put(token, new SessionToken(user, expiresAt));

        return token;
    }

    public void logout(String token) {
        sessions.remove(token);
    }

    public User getUserByToken(String token) {
        SessionToken session = sessions.get(token);
        if (session == null) return null;

        if (System.currentTimeMillis() > session.getExpiresAt()) {
            sessions.remove(token);
            return null;
        }

        return session.getUser();
    }

    public User validateToken(String token) {
        if (token == null || token.isBlank()) {
            throw new StoreException(
                    "Authentication required: token is missing",
                    FORBIDDEN.name(),
                    HttpStatus.FORBIDDEN.value()
            );
        }

        User user = getUserByToken(token);
        if (user == null) {
            throw new StoreException(
                    "Invalid or expired token",
                    FORBIDDEN.name(),
                    HttpStatus.FORBIDDEN.value()
            );
        }
        return user;
    }

    public User validateRole(String token, Role requiredRole) {
        User user = validateToken(token);
        if (user.getRole() != requiredRole) {
            throw new StoreException("Insufficient permissions", FORBIDDEN.name(), HttpStatus.FORBIDDEN.value());
        }
        return user;
    }

    public User validateRoles(String token, Role... allowedRoles) {
        User user = validateToken(token);
        boolean allowed = Arrays.stream(allowedRoles).anyMatch(r -> r == user.getRole());
        if (!allowed) {
            throw new StoreException("Insufficient permissions", FORBIDDEN.name(), HttpStatus.FORBIDDEN.value());
        }
        return user;
    }

    private static class SessionToken {
        private final User user;
        private final long expiresAt;

        public SessionToken(User user, long expiresAt) {
            this.user = user;
            this.expiresAt = expiresAt;
        }

        public User getUser() {
            return user;
        }

        public long getExpiresAt() {
            return expiresAt;
        }
    }

}
