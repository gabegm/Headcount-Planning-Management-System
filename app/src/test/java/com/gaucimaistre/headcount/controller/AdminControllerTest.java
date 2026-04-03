package com.gaucimaistre.headcount.controller;

import com.gaucimaistre.headcount.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AdminControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String ADMIN_EMAIL = "gabriel.gaucimaistre@gaucimaistre.com";

    @Test
    void adminDashboard_whenUnauthenticated_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/auth/login"));
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
        mockMvc.perform(get("/admin/user")
                        .with(user(ADMIN_EMAIL).roles("ADMIN", "USER")))
                .andExpect(status().isOk());
    }

    @Test
    void adminPositions_returnsOk_forAdmin() throws Exception {
        mockMvc.perform(get("/admin/position")
                        .with(user(ADMIN_EMAIL).roles("ADMIN", "USER")))
                .andExpect(status().isOk());
    }
}
