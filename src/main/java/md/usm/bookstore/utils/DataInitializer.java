package md.usm.bookstore.utils;

import md.usm.bookstore.model.*;
import md.usm.bookstore.repository.AuthorRepository;
import md.usm.bookstore.repository.BookRepository;
import md.usm.bookstore.repository.CategoryRepository;
import md.usm.bookstore.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class DataInitializer {

    @Transactional
    @Bean
    CommandLineRunner init(UserRepository userRepository,
                           BookRepository bookRepository,
                           AuthorRepository authorRepository,
                           CategoryRepository categoryRepository) {
        return _ -> {
            // Admin user
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail("admin@test.com");
                admin.setPassword(BCrypt.hashpw("admin123", BCrypt.gensalt()));
                admin.setRole(Role.ADMIN);
                userRepository.save(admin);
            }

            // Authors
            Author author1 = new Author("George", "Orwell");
            Author author2 = new Author("J.K.", "Rowling");
            Author author3 = new Author("Fyodor", "Dostoevsky");
            Author author4 = new Author("Harper", "Lee");
            Author author5 = new Author("J.R.R.", "Tolkien");

            // Categories
            Category fiction = new Category("Fiction");
            Category fantasy = new Category("Fantasy");
            Category classic = new Category("Classic");

            // Books
            Book book1 = new Book("1984", "9780451524935", 15.99, List.of(author1, author2, author3), fiction);
            Book book2 = new Book("Animal Farm", "9780451526342", 12.99, List.of(author1), fiction);

            Book book3 = new Book("Harry Potter and the Philosopher's Stone", "9780747532699", 29.99, List.of(author1, author2, author3, author4, author5), fantasy);
            Book book4 = new Book("Harry Potter and the Chamber of Secrets", "9780439064873", 24.99, List.of(author2, author5), fantasy);

            Book book5 = new Book("Crime and Punishment", "9780140449136", 19.99, List.of(author3, author4, author5), classic);
            Book book6 = new Book("The Brothers Karamazov", "9780374528379", 21.99, List.of(author3, author1, author2), classic);

            Book book7 = new Book("To Kill a Mockingbird", "9780061120084", 14.99, List.of(author4), fiction);

            Book book8 = new Book("The Lord of the Rings", "9780544003415", 39.99, List.of(author5, author2, author3), fantasy);
            Book book9 = new Book("The Hobbit", "9780261103344", 25.99, List.of(author2), fantasy);

            // Save data
            categoryRepository.saveAll(List.of(fiction, fantasy, classic));
            authorRepository.saveAll(List.of(author1, author2, author3, author4, author5));
            bookRepository.saveAll(List.of(
                    book1, book2,
                    book3, book4,
                    book5, book6,
                    book7,
                    book8, book9
            ));
        };
    }
}
