package com.gaucimaistre.gatekeeping.controller;

import com.gaucimaistre.gatekeeping.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AdminControllerTest extends AbstractIntegrationTest {


    private static final String ADMIN_EMAIL = "admin@example.com";

    @Test
    void adminDashboard_whenUnauthenticated_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }

    @Test
    void adminDashboard_whenUserRole_redirectsToHome() throws Exception {
        // SecurityConfig sets accessDeniedPage("/") which forwards the request to "/"
        // with HTTP 403 status; Spring Security does not redirect in this case.
        mockMvc.perform(get("/admin")
                        .with(user("user@gaucimaistre.com").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminDashboard_whenAdminRole_returnsOk() throws Exception {
        mockMvc.perform(get("/admin")
                        .with(user(ADMIN_EMAIL).roles("ADMIN", "USER")))
                .andExpect(status().isOk());
    }

    @Test
    void adminUsers_returnsOk_forAdmin() throws Exception {
        mockMvc.perform(get("/admin/users")
                        .with(user(ADMIN_EMAIL).roles("ADMIN", "USER")))
                .andExpect(status().isOk());
    }

    @Test
    void adminPositions_returnsOk_forAdmin() throws Exception {
        mockMvc.perform(get("/admin/positions")
                        .with(user(ADMIN_EMAIL).roles("ADMIN", "USER")))
                .andExpect(status().isOk());
    }
}
