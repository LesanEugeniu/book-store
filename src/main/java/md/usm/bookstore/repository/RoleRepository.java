package md.usm.bookstore.repository;

import md.usm.bookstore.model.Role;
import md.usm.bookstore.model.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleEnum name);
}
