package com.gaucimaistre.headcount.controller;

import com.gaucimaistre.headcount.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String ADMIN_EMAIL = "admin@example.com";

    @Test
    void loginPage_returnsOk() throws Exception {
        mockMvc.perform(get("/auth/login"))
                .andExpect(status().isOk());
    }

    @Test
    void loginPage_whenAlreadyAuthenticated_redirects() throws Exception {
        // Spring Security 6 by default does NOT redirect authenticated users away from
        // the login page (no special handling configured); it returns 200.
        mockMvc.perform(get("/auth/login")
                        .with(user(ADMIN_EMAIL).roles("ADMIN", "USER")))
                .andExpect(status().isOk());
    }

    @Test
    void login_withValidCredentials_redirectsToHome() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .param("email", ADMIN_EMAIL)
                        .param("password", "changeme"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void login_withInvalidPassword_showsError() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .param("email", ADMIN_EMAIL)
                        .param("password", "wrong-password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login?error=true"));
    }

    @Test
    void login_withUnknownEmail_showsError() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .param("email", "unknown@example.com")
                        .param("password", "anypassword"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login?error=true"));
    }

    @Test
    void logout_redirectsToLogin() throws Exception {
        mockMvc.perform(post("/logout")
                        .with(csrf())
                        .with(user(ADMIN_EMAIL).roles("ADMIN", "USER")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login?logout=true"));
    }

    @Test
    void registerPage_returnsOk() throws Exception {
        mockMvc.perform(get("/auth/register"))
                .andExpect(status().isOk());
    }

    @Test
    void register_withValidData_redirectsToLogin() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .param("email", "newuser@gaucimaistre.com")
                        .param("password", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }

    @Test
    void register_withDuplicateEmail_showsError() throws Exception {
        // ADMIN_EMAIL is already in seed data; registering it again should fail
        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .param("email", ADMIN_EMAIL)
                        .param("password", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/register"));
    }

    @Test
    void forgotPasswordPage_returnsOk() throws Exception {
        mockMvc.perform(get("/auth/forgot-password"))
                .andExpect(status().isOk());
    }
}
