package com.gaucimaistre.headcount.controller;

import com.gaucimaistre.headcount.AbstractIntegrationTest;
import com.gaucimaistre.headcount.model.User;
import com.gaucimaistre.headcount.model.enums.UserType;
import com.gaucimaistre.headcount.security.AppUserDetails;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SubmissionControllerTest extends AbstractIntegrationTest {


    private AppUserDetails adminUserDetails() {
        User adminUser = new User(1, "admin@example.com", "hash", null, UserType.ADMIN, true);
        return new AppUserDetails(adminUser);
    }

    @Test
    void submissions_whenUnauthenticated_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/submissions"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }

    @Test
    void submissions_whenAuthenticated_returnsOk() throws Exception {
        mockMvc.perform(get("/submissions")
                        .with(user(adminUserDetails())))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"))
                // Verify links in the rendered page point to the correct plural paths
                .andExpect(content().string(containsString("href=\"/submissions/create\"")));
    }

    @Test
    void submissionsCreate_whenUnauthenticated_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/submissions/create"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }

    @Test
    void submissionsCreate_whenAuthenticated_returnsOk() throws Exception {
        mockMvc.perform(get("/submissions/create")
                        .with(user(adminUserDetails())))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"))
                // Verify form action points to the correct plural path
                .andExpect(content().string(containsString("action=\"/submissions/create\"")));
    }
}
