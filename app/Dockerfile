# ── Build stage ────────────────────────────────────────────────────────────────
FROM amazoncorretto:26 AS build
WORKDIR /build

# Install xargs (required by Gradle wrapper)
RUN yum install -y findutils && yum clean all

# Copy Gradle wrapper and dependency descriptors first for layer caching
COPY gradlew settings.gradle.kts build.gradle.kts ./
COPY gradle/ gradle/

# Pre-fetch dependencies (cached unless build files change)
RUN ./gradlew dependencies --no-daemon -q 2>/dev/null || true

# Copy source and build the fat JAR
COPY src/ src/
RUN ./gradlew bootJar --no-daemon -q

# ── Runtime stage ───────────────────────────────────────────────────────────────
FROM amazoncorretto:26-alpine AS runtime
WORKDIR /app

# Non-root user for security
RUN addgroup --system gatekeeping && adduser --system --ingroup gatekeeping gatekeeping

COPY --from=build /build/build/libs/*.jar app.jar

RUN chown gatekeeping:gatekeeping app.jar
USER gatekeeping

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
