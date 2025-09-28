package md.usm.bookstore.repository;

import md.usm.bookstore.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c")
    Page<Category> findAllCategories(Pageable pageable);

    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.books WHERE c IN :categories")
    List<Category> fetchBooksForCategories(@Param("categories") List<Category> categories);

    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.books WHERE c.id = :id")
    Category findByIdWithBooks(@Param("id") Long id);
}
