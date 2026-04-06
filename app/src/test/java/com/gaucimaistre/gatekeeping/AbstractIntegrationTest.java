package com.gaucimaistre.gatekeeping;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

/**
 * Base class for all integration tests. Imports TestcontainersConfiguration
 * which provides a PostgreSQL container as a Spring bean — this means the
 * container lifecycle is tied to the shared Spring ApplicationContext, so it
 * starts once and is reused across every test class.
 *
 * MockMvc is configured manually here because @AutoConfigureMockMvc was removed
 * in Spring Boot 4. The instance is injected into subclasses via the protected field.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Import(TestcontainersConfiguration.class)
public abstract class AbstractIntegrationTest {

    @Autowired
    private WebApplicationContext wac;

    protected MockMvc mockMvc;

    @BeforeEach
    void setUpMockMvc() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(springSecurity())
                .build();
    }

    @DynamicPropertySource
    static void mailProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.mail.host", () -> "localhost");
        registry.add("spring.mail.port", () -> "1025");
        registry.add("spring.mail.password", () -> "");
        // Include seed data in tests so repositories have realistic data to assert against
        registry.add("spring.flyway.locations", () -> "classpath:db/migration,classpath:db/seed");
    }
}
