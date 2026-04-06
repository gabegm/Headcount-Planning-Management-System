package com.gaucimaistre.gatekeeping.controller;

import com.gaucimaistre.gatekeeping.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @Value("${app.base-url:http://localhost:8080}")
    private String appBaseUrl;

    // ── Login ────────────────────────────────────────────────────────────────

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            @RequestParam(required = false) String expired,
                            Model model) {
        if (error != null)   model.addAttribute("errorMessage", "Invalid email or password.");
        if (logout != null)  model.addAttribute("successMessage", "You have been logged out.");
        if (expired != null) model.addAttribute("errorMessage", "Your session has expired. Please log in again.");
        return "auth/login";
    }

    // ── Register ─────────────────────────────────────────────────────────────

    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String email,
                           @RequestParam String password,
                           RedirectAttributes redirect) {
        try {
            userService.register(email.trim().toLowerCase(), password);
            redirect.addFlashAttribute("successMessage",
                "Account created. Please wait for an admin to activate it.");
            return "redirect:/auth/login";
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/auth/register";
        }
    }

    // ── Forgot password ───────────────────────────────────────────────────────

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email,
                                 HttpServletRequest request,
                                 RedirectAttributes redirect) {
        // Always show the same message to prevent email enumeration
        try {
            String token = userService.initiatePasswordReset(email.trim().toLowerCase());
            String baseUrl = appBaseUrl.isBlank()
                ? request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
                : appBaseUrl;
            userService.sendPasswordResetEmail(email.trim().toLowerCase(), token, baseUrl);
        } catch (Exception e) {
            log.debug("Password reset request for unknown/errored email: {}", email);
        }
        redirect.addFlashAttribute("successMessage",
            "If an account exists for that email, a reset link has been sent.");
        return "redirect:/auth/forgot-password";
    }

    // ── Reset password ────────────────────────────────────────────────────────

    @GetMapping("/reset/{token}")
    public String resetPasswordPage(@PathVariable String token, Model model) {
        model.addAttribute("token", token);
        return "auth/reset";
    }

    @PostMapping("/reset/{token}")
    public String resetPassword(@PathVariable String token,
                                @RequestParam String password,
                                @RequestParam String confirmPassword,
                                RedirectAttributes redirect) {
        if (!password.equals(confirmPassword)) {
            redirect.addFlashAttribute("errorMessage", "Passwords do not match.");
            return "redirect:/auth/reset/" + token;
        }
        boolean ok = userService.resetPassword(token, password);
        if (!ok) {
            redirect.addFlashAttribute("errorMessage", "Reset link is invalid or has expired.");
            return "redirect:/auth/forgot-password";
        }
        redirect.addFlashAttribute("successMessage", "Password updated. Please log in.");
        return "redirect:/auth/login";
    }
}
