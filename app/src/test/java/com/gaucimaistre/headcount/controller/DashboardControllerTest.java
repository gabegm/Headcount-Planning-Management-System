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

class DashboardControllerTest extends AbstractIntegrationTest {


    private AppUserDetails adminUserDetails() {
        User adminUser = new User(1, "admin@example.com", "hash", null, UserType.ADMIN, true);
        return new AppUserDetails(adminUser);
    }

    @Test
    void dashboardRender_whenUnauthenticated_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/dashboard/render/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }

    @Test
    void dashboardRender_whenAuthenticated_returnsJson() throws Exception {
        mockMvc.perform(get("/dashboard/render/1")
                        .with(user(adminUserDetails())))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"));
    }

    @Test
    void dashboardRender_withAllFunctions_returnsJson() throws Exception {
        mockMvc.perform(get("/dashboard/render/0")
                        .with(user(adminUserDetails())))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"));
    }
}

