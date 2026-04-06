package com.gaucimaistre.gatekeeping.security;

import com.gaucimaistre.gatekeeping.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Loads a user from the database by email address for Spring Security.
 * Spring Security calls this during form login with the submitted email.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
            .map(AppUserDetails::new)
            .orElseThrow(() -> {
                log.debug("Login attempt for unknown email: {}", email);
                return new UsernameNotFoundException("No account found for: " + email);
            });
    }
}
