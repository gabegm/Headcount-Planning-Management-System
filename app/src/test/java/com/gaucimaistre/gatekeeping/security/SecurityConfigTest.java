package com.gaucimaistre.gatekeeping.security;

import com.gaucimaistre.gatekeeping.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SecurityConfigTest extends AbstractIntegrationTest {


    private static final String ADMIN_EMAIL = "gabriel.gaucimaistre@gaucimaistre.com";

    @Test
    void publicEndpoints_areAccessibleWithoutAuth() throws Exception {
        mockMvc.perform(get("/auth/login")).andExpect(status().isOk());
        mockMvc.perform(get("/auth/register")).andExpect(status().isOk());
        mockMvc.perform(get("/auth/forgot-password")).andExpect(status().isOk());
    }

    @Test
    void protectedEndpoints_requireAuth() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));

        mockMvc.perform(get("/positions"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));

        mockMvc.perform(get("/submissions"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }

    @Test
    void adminEndpoints_requireAdminRole() throws Exception {
        // Non-admin user: accessDeniedPage("/") forwards with HTTP 403
        mockMvc.perform(get("/admin")
                        .with(user("user@gaucimaistre.com").roles("USER")))
                .andExpect(status().isForbidden());

        // Unauthenticated → redirect to login
        mockMvc.perform(get("/admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }

    @Test
    void csrf_isRequiredForPostRequests() throws Exception {
        // Explicitly pre-populate a session with a known CSRF token so that Spring
        // Security treats this as an *existing* token (isGenerated=false) and enforces
        // the mismatch between the stored token and the missing _csrf parameter → 403.
        org.springframework.mock.web.MockHttpSession session =
                new org.springframework.mock.web.MockHttpSession();
        org.springframework.security.web.csrf.DefaultCsrfToken storedToken =
                new org.springframework.security.web.csrf.DefaultCsrfToken(
                        "X-CSRF-TOKEN", "_csrf", "known-token");
        session.setAttribute(
                "org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository.CSRF_TOKEN",
                storedToken);

        mockMvc.perform(post("/auth/register")
                        .session(session)
                        .param("email", "csrf-test@gaucimaistre.com")
                        .param("password", "somepassword"))
                .andExpect(status().isForbidden());
    }
}
