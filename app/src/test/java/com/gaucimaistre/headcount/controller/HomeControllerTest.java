package com.gaucimaistre.headcount.controller;

import com.gaucimaistre.headcount.AbstractIntegrationTest;
import com.gaucimaistre.headcount.model.User;
import com.gaucimaistre.headcount.model.enums.UserType;
import com.gaucimaistre.headcount.security.AppUserDetails;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class HomeControllerTest extends AbstractIntegrationTest {


    private static final String ADMIN_EMAIL = "admin@example.com";

    /** Builds an AppUserDetails backed by the real admin user from seed data. */
    private AppUserDetails adminUserDetails() {
        User adminUser = new User(1, ADMIN_EMAIL, "hash", null, UserType.ADMIN, true);
        return new AppUserDetails(adminUser);
    }

    @Test
    void home_whenUnauthenticated_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }

    @Test
    void home_whenAuthenticated_returnsOk() throws Exception {
        mockMvc.perform(get("/")
                        .with(user(adminUserDetails())))
                .andExpect(status().isOk());
    }

    @Test
    void home_containsChartsAndFunctionDropdown() throws Exception {
        mockMvc.perform(get("/")
                        .with(user(adminUserDetails())))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("fteChart")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("costChart")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("functionSelect")));
    }
}
