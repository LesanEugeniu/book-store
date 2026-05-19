package md.usm.bookstore.model;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role extends BaseEntity {

    public static final String ADMIN   = "ADMIN";
    public static final String MANAGER = "MANAGER";
    public static final String USER    = "USER";

    @Column(unique = true, nullable = false)
    private String name;

    public Role() {}

    public Role(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
