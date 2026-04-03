package com.gaucimaistre.headcount.security;

import com.gaucimaistre.headcount.model.User;
import com.gaucimaistre.headcount.model.enums.UserType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Wraps the domain User record for Spring Security.
 * Exposes getUserId() and getUserType() for use in controllers
 * via @AuthenticationPrincipal AppUserDetails.
 */
public class AppUserDetails implements UserDetails {

    private final User user;

    public AppUserDetails(User user) {
        this.user = user;
    }

    public int getUserId() {
        return user.id();
    }

    public UserType getUserType() {
        return user.type();
    }

    public User getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Map UserType to Spring Security roles: ROLE_USER and ROLE_ADMIN
        return user.type() == UserType.ADMIN
            ? List.of(new SimpleGrantedAuthority("ROLE_ADMIN"),
                      new SimpleGrantedAuthority("ROLE_USER"))
            : List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return user.password();
    }

    @Override
    public String getUsername() {
        return user.email();
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

    /** Only active users can log in. */
    @Override
    public boolean isEnabled() {
        return user.active();
    }
}
