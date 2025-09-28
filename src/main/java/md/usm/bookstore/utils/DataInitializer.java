package md.usm.bookstore.utils;

import md.usm.bookstore.model.Role;
import md.usm.bookstore.model.User;
import md.usm.bookstore.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

    @Bean
    CommandLineRunner init(UserRepository userRepository, PasswordEncoder encoder) {
        return _ -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail("admin@test.com");
                admin.setPassword(encoder.encode("admin123"));
                admin.setRole(Role.ADMIN);
                userRepository.save(admin);
            }
        };
    }
    
}
