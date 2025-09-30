package md.usm.bookstore.service;

import md.usm.bookstore.dto.RegistrationRequestDto;
import md.usm.bookstore.dto.UserDto;
import md.usm.bookstore.exception.StoreException;
import md.usm.bookstore.model.Role;
import md.usm.bookstore.model.User;
import md.usm.bookstore.repository.UserRepository;
import md.usm.bookstore.utils.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

import static md.usm.bookstore.utils.ErrorType.*;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final Mapper mapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, Mapper mapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserDto create(RegistrationRequestDto registrationRequestDto) {
        checkUsernameUnique(registrationRequestDto.username());

        User user = new User();
        user.setRole(Role.USER);
        user.setPassword(passwordEncoder.encode(registrationRequestDto.password()));
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
        User user = getEntityById(id);
        return mapper.toDto(user);
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

    public UserDto getProfile(Principal principal) {
        return mapper.toDto(getByUsername(principal.getName()));
    }

    @Transactional
    public UserDto update(Long id, UserDto userDto, Principal principal) {
        User user = getEntityById(id);

        if (!user.getUsername().equals(principal.getName()) || user.getRole().equals(Role.USER)) {
            throw new StoreException(
                    "No permission to perform this action",
                    FORBIDDEN.name(),
                    HttpStatus.FORBIDDEN.value()
            );
        }

        checkUsernameUnique(userDto.username());

        if (userDto.username() != null) user.setUsername(userDto.username());
        if (userDto.email() != null) user.setEmail(userDto.email());
        if (userDto.password() != null) user.setPassword(passwordEncoder.encode(userDto.password()));

        return mapper.toDto(userRepository.save(user));
    }

    public void delete(Long id) {
        User user = getEntityById(id);
        userRepository.delete(user);
    }

    private void checkUsernameUnique(String userName) {
        if (userRepository.findByUsername(userName).isPresent()) {
            throw new StoreException(
                    "Username already exists",
                    VALIDATION_ERROR.name(),
                    HttpStatus.BAD_REQUEST.value()
            );
        }
    }

}
