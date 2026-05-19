package md.usm.bookstore.security;

import md.usm.bookstore.model.Role;
import md.usm.bookstore.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;

public final class JwtUserFactory {
    public JwtUserFactory() {
    }

    public static JwtUser create(User user) {
        return new JwtUser(
                user.getPassword(),
                user.getUsername(),
                true,
                mapToGrantedAuthorities(user.getRoles())
        );
    }

    private static List<GrantedAuthority> mapToGrantedAuthorities(Set<Role> roles) {
        return roles.stream()
                .<GrantedAuthority>map(role ->
                        new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .toList();
    }

}
