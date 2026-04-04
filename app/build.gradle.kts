plugins {
    java
    id("org.springframework.boot") version "3.5.3"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.gaucimaistre.headcount"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Web
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Thymeleaf + Spring Security dialect
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")

    // Security
    implementation("org.springframework.boot:spring-boot-starter-security")

    // JDBC (no ORM — NamedParameterJdbcTemplate)
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    runtimeOnly("org.postgresql:postgresql")

    // Flyway
    implementation("org.flywaydb:flyway-core")
    runtimeOnly("org.flywaydb:flyway-database-postgresql")

    // Mail
    implementation("org.springframework.boot:spring-boot-starter-mail")

    // Validation
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // CSV import
    implementation("com.opencsv:opencsv:5.12.0")

    // HTML sanitisation
    implementation("org.owasp.antisamy:antisamy:1.7.8")

    // Thymeleaf Layout Dialect — layout:decorate (equivalent to Jinja2 {% extends %})
    implementation("nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect:3.4.0")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
}

tasks.withType<Test> {
    useJUnitPlatform()
    // Testcontainers shaded docker-java reads 'api.version' system property
    // Docker 29+ requires minimum API 1.44; ensure the client sends a supported version
    systemProperty("api.version", "1.47")
}
