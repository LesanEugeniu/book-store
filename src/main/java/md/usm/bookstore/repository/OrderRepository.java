package md.usm.bookstore.repository;

import md.usm.bookstore.model.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.books LEFT JOIN FETCH o.user WHERE o.id = :id")
    Order findByIdWithBooksAndUser(@Param("id") Long id);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.books LEFT JOIN FETCH o.user u WHERE u.id = :userId")
    List<Order> findAllByUserIdWithBooksAndUser(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT count(o) FROM Order o WHERE o.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);

}
