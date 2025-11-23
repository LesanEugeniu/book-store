package md.usm.bookstore.repository;

import md.usm.bookstore.model.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    @Query("SELECT a FROM Author a")
    Page<Author> findAllAuthors(Pageable pageable);

    @Query("SELECT a FROM Author a LEFT JOIN FETCH a.books WHERE a IN :authors")
    List<Author> fetchBooksForAuthors(@Param("authors") List<Author> authors);

    @Query("SELECT a FROM Author a LEFT JOIN FETCH a.books WHERE a.id = :id")
    Author findByIdWithBooks(@Param("id") Long id);

    @Query("SELECT a FROM Author a JOIN a.books b WHERE b.id = :bookId")
    List<Author> findAllByBookId(@Param("bookId") Long bookId);

}
