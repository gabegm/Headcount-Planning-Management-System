package com.gaucimaistre.headcount.service;

import com.gaucimaistre.headcount.model.User;
import com.gaucimaistre.headcount.model.enums.UserType;
import com.gaucimaistre.headcount.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final AuditService auditService;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    public Optional<User> findById(int id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<User> findAllActive() {
        return userRepository.findAllActive();
    }

    public int createUser(String email, String rawPassword, UserType type, boolean active) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email address is already registered: " + email);
        }
        String hashed = passwordEncoder.encode(rawPassword);
        return userRepository.save(new User(0, email, hashed, null, type, active));
    }

    public int register(String email, String rawPassword) {
        if (email == null || !email.toLowerCase().endsWith("@gaucimaistre.com")) {
            throw new IllegalArgumentException("Registration is restricted to @gaucimaistre.com email addresses");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email address is already registered: " + email);
        }
        String hashed = passwordEncoder.encode(rawPassword);
        User user = new User(0, email, hashed, null, UserType.USER, false);
        return userRepository.save(user);
    }

    public void activate(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        User updated = new User(user.id(), user.email(), user.password(), user.passwordResetToken(),
                user.type(), true);
        userRepository.update(updated);
    }

    public void updateTypeAndActive(int id, UserType type, boolean active) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        User updated = new User(user.id(), user.email(), user.password(), user.passwordResetToken(),
                type, active);
        userRepository.update(updated);
    }

    public void update(int id, UserType type, boolean active, List<Integer> functionIds) {
        updateTypeAndActive(id, type, active);
        setFunctions(id, functionIds);
    }

    @Transactional
    public void setFunctions(int userId, List<Integer> functionIds) {
        userRepository.removeAllFunctions(userId);
        if (functionIds != null) {
            for (int functionId : functionIds) {
                userRepository.addFunction(userId, functionId);
            }
        }
    }

    public List<Integer> getFunctionIds(int userId) {
        return userRepository.findFunctionIdsByUserId(userId);
    }

    public boolean hasFunctions(int userId) {
        return userRepository.hasFunctions(userId);
    }

    public String initiatePasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("No user found with email: " + email));
        String token = UUID.randomUUID().toString();
        userRepository.setPasswordResetToken(user.id(), token);
        return token;
    }

    public boolean resetPassword(String token, String newRawPassword) {
        Optional<User> maybeUser = userRepository.findByPasswordResetToken(token);
        if (maybeUser.isEmpty()) {
            return false;
        }
        User user = maybeUser.get();
        String hashed = passwordEncoder.encode(newRawPassword);
        userRepository.updatePassword(user.id(), hashed);
        userRepository.clearPasswordResetToken(user.id());
        return true;
    }

    public void sendPasswordResetEmail(String email, String token, String appBaseUrl) {
        mailService.sendPasswordResetEmail(email, token, appBaseUrl);
    }

    public void delete(int id) {
        throw new UnsupportedOperationException("User deletion not supported");
    }
}
