# ── Build stage ────────────────────────────────────────────────────────────────
FROM eclipse-temurin:25-jdk-noble AS build
WORKDIR /build

# Copy Gradle wrapper and dependency descriptors first for layer caching
COPY gradlew settings.gradle.kts build.gradle.kts ./
COPY gradle/ gradle/

# Pre-fetch dependencies (cached unless build files change)
RUN ./gradlew dependencies --no-daemon -q 2>/dev/null || true

# Copy source and build the fat JAR
COPY src/ src/
RUN ./gradlew bootJar --no-daemon -q

# ── Runtime stage ───────────────────────────────────────────────────────────────
FROM eclipse-temurin:25-jre-noble AS runtime
WORKDIR /app

# Non-root user for security
RUN groupadd --system headcount && useradd --system --gid headcount headcount

COPY --from=build /build/build/libs/*.jar app.jar

RUN chown headcount:headcount app.jar
USER headcount

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
