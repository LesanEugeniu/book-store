package md.usm.bookstore.service;

import md.usm.bookstore.dto.RegisterRequest;
import md.usm.bookstore.dto.UserDto;
import md.usm.bookstore.exception.StoreException;
import md.usm.bookstore.model.Role;
import md.usm.bookstore.model.RoleEnum;
import md.usm.bookstore.model.User;
import md.usm.bookstore.repository.RoleRepository;
import md.usm.bookstore.repository.UserRepository;
import md.usm.bookstore.utils.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.Set;

import static md.usm.bookstore.utils.ErrorType.*;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final Mapper mapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       Mapper mapper,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User create(RegisterRequest request) {
        checkUsernameUnique(request.username());

        Role userRole = roleRepository.findByName(RoleEnum.USER)
                .orElseThrow(() -> new StoreException(
                        "Default role USER not found",
                        NOT_FOUND.name(),
                        HttpStatus.INTERNAL_SERVER_ERROR.value()
                ));

        User user = new User();
        user.setRoles(Set.of(userRole));
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setUsername(request.username());
        user.setEmail(request.email());

        return userRepository.save(user);
    }

    public Page<UserDto> getAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(mapper::toDto);
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

    public UserDto getProfile(Principal principal) {
        return mapper.toDto(getByUsername(principal.getName()));
    }

    @Transactional
    public UserDto update(Long id, UserDto userDto, Principal principal) {
        User currentUser = getByUsername(principal.getName());
        User targetUser = getEntityById(id);

        boolean isAdmin = currentUser.hasRole(RoleEnum.ADMIN);
        boolean isSelf = currentUser.getUsername().equals(targetUser.getUsername());

        if (!isAdmin && !isSelf) {
            throw new StoreException(
                    "No permission to perform this action",
                    FORBIDDEN.name(),
                    HttpStatus.FORBIDDEN.value()
            );
        }

        if (userDto.username() != null && !userDto.username().equals(targetUser.getUsername())) {
            checkUsernameUnique(userDto.username());
            targetUser.setUsername(userDto.username());
        }
        if (userDto.email() != null) targetUser.setEmail(userDto.email());
        if (userDto.password() != null) targetUser.setPassword(passwordEncoder.encode(userDto.password()));

        return mapper.toDto(userRepository.save(targetUser));
    }

    @Transactional
    public UserDto assignRoles(Long id, Set<RoleEnum> roleNames) {
        User user = getEntityById(id);
        Set<Role> roles = new java.util.HashSet<>();
        for (RoleEnum roleName : roleNames) {
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new StoreException(
                            "Role not found: " + roleName,
                            NOT_FOUND.name(),
                            HttpStatus.NOT_FOUND.value()
                    ));
            roles.add(role);
        }
        user.setRoles(roles);
        return mapper.toDto(userRepository.save(user));
    }

    public void delete(Long id) {
        userRepository.delete(getEntityById(id));
    }

    private void checkUsernameUnique(String userName) {
        if (userName != null && userRepository.findByUsername(userName).isPresent()) {
            throw new StoreException(
                    "Username already exists",
                    VALIDATION_ERROR.name(),
                    HttpStatus.BAD_REQUEST.value()
            );
        }
    }

}
