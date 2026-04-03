package com.gaucimaistre.headcount.service;

import com.gaucimaistre.headcount.model.User;
import com.gaucimaistre.headcount.model.enums.UserType;
import com.gaucimaistre.headcount.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuditService auditService;

    @Mock
    private MailService mailService;

    @InjectMocks
    private UserService userService;

    @Test
    void register_savesUserWithEncodedPassword() {
        String email = "newuser@gaucimaistre.com";
        String rawPassword = "mypassword";
        String encodedPassword = "$2a$10$hashed";

        given(userRepository.findByEmail(email)).willReturn(Optional.empty());
        given(passwordEncoder.encode(rawPassword)).willReturn(encodedPassword);
        given(userRepository.save(any(User.class))).willReturn(42);

        int id = userService.register(email, rawPassword);

        assertThat(id).isEqualTo(42);
        verify(passwordEncoder).encode(rawPassword);
        verify(userRepository).save(argThat(user ->
                user.email().equals(email) &&
                user.password().equals(encodedPassword) &&
                user.type() == UserType.USER &&
                !user.active()
        ));
    }

    @Test
    void register_throwsException_whenEmailAlreadyExists() {
        String email = "existing@gaucimaistre.com";
        User existingUser = new User(1, email, "hash", null, UserType.USER, true);

        given(userRepository.findByEmail(email)).willReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> userService.register(email, "password"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already registered");
    }

    @Test
    void register_throwsException_whenEmailDomainIsInvalid() {
        assertThatThrownBy(() -> userService.register("user@otherdomain.com", "password"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("@gaucimaistre.com");
    }

    @Test
    void findByEmail_delegatesToRepository() {
        String email = "someone@gaucimaistre.com";
        User user = new User(5, email, "hash", null, UserType.USER, true);
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));

        Optional<User> result = userService.findByEmail(email);

        assertThat(result).isPresent();
        assertThat(result.get().email()).isEqualTo(email);
        verify(userRepository).findByEmail(email);
    }

    @Test
    void resetPassword_encodesNewPassword() {
        String token = "reset-token-123";
        String newPassword = "newSecurePassword";
        String encodedPassword = "$2a$10$newhash";
        User user = new User(7, "user@gaucimaistre.com", "oldhash", token, UserType.USER, true);

        given(userRepository.findByPasswordResetToken(token)).willReturn(Optional.of(user));
        given(passwordEncoder.encode(newPassword)).willReturn(encodedPassword);

        boolean result = userService.resetPassword(token, newPassword);

        assertThat(result).isTrue();
        verify(passwordEncoder).encode(newPassword);
        verify(userRepository).updatePassword(user.id(), encodedPassword);
        verify(userRepository).clearPasswordResetToken(user.id());
    }
}
