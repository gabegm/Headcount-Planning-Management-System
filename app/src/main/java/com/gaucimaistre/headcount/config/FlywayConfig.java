package com.gaucimaistre.headcount.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Manual Flyway configuration required because Spring Boot 4 removed
 * Flyway from its autoconfigure module. The locations property is kept
 * injectable so that tests can override it via @DynamicPropertySource
 * to include the seed data classpath.
 */
@Configuration
public class FlywayConfig {

    @Bean(initMethod = "migrate")
    public Flyway flyway(
            DataSource dataSource,
            @Value("${spring.flyway.locations:classpath:db/migration}") String locations) {
        return Flyway.configure()
                .dataSource(dataSource)
                .locations(locations.split(","))
                .load();
    }
}
