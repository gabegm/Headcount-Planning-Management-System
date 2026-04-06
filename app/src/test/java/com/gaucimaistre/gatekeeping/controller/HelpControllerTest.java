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

class HelpControllerTest extends AbstractIntegrationTest {


    private AppUserDetails adminUserDetails() {
        User adminUser = new User(1, "admin@example.com", "hash", null, UserType.ADMIN, true);
        return new AppUserDetails(adminUser);
    }

    @Test
    void help_whenUnauthenticated_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/help"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }

    @Test
    void help_whenAuthenticated_returnsOk() throws Exception {
        mockMvc.perform(get("/help")
                        .with(user(adminUserDetails())))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"));
    }
}
