package com.gaucimaistre.gatekeeping.controller;

import com.gaucimaistre.gatekeeping.AbstractIntegrationTest;
import com.gaucimaistre.gatekeeping.model.User;
import com.gaucimaistre.gatekeeping.model.enums.UserType;
import com.gaucimaistre.gatekeeping.security.AppUserDetails;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PositionControllerTest extends AbstractIntegrationTest {


    private AppUserDetails adminUserDetails() {
        User adminUser = new User(1, "admin@example.com", "hash", null, UserType.ADMIN, true);
        return new AppUserDetails(adminUser);
    }

    @Test
    void positions_whenUnauthenticated_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/positions"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }

    @Test
    void positions_whenAuthenticated_returnsOk() throws Exception {
        mockMvc.perform(get("/positions")
                        .with(user(adminUserDetails())))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"));
    }

    @Test
    void positionsCreate_whenUnauthenticated_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/positions/create"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }

    @Test
    void positionsCreate_whenAuthenticatedAsAdmin_returnsOk() throws Exception {
        mockMvc.perform(get("/positions/create")
                        .with(user(adminUserDetails())))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"));
    }
}
