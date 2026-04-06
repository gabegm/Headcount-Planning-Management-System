package com.gaucimaistre.gatekeeping;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Provides a PostgreSQL Testcontainer as a Spring bean with @ServiceConnection.
 * Because this is a @TestConfiguration, the bean is scoped to the Spring
 * ApplicationContext — which Spring Test caches and reuses across all test
 * classes that import this configuration. The container therefore starts exactly
 * once per test suite run and is stopped when the JVM exits.
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>("postgres:17-alpine");
    }
}
