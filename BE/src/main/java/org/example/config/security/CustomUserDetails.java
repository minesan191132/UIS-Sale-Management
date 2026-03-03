package org.example.config.security;

import lombok.Getter;
import org.example.features.company.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Custom UserDetails implementation
 * Extends Spring Security UserDetails with additional user information
 */
@Getter
public class CustomUserDetails implements UserDetails {

    private final Long userId;
    private final Long companyId;
    private final String email;
    private final String fullName;
    private final String password;
    private final String role;
    private final boolean enabled;

    public CustomUserDetails(User user) {
        this.userId = user.getId();
        this.companyId = user.getCompany() != null ? user.getCompany().getId() : null;
        this.email = user.getEmail();
        this.fullName = user.getFullName();
        this.password = user.getPassword();
        this.role = user.getRole().name();
        this.enabled = user.getIsActive() != null && user.getIsActive();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email; // Using email as username
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
