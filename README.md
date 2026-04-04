# Gatekeeping — Headcount Planning & Management System

An internal enterprise tool for managing job positions, headcount budgets,
submission/approval workflows, org hierarchy, and analytics dashboards.

![Login page](gallery/login.png)
![Homepage](gallery/main.png)
![Admin dashboard](gallery/admin.png)
![Audit Trail](gallery/audit.png)
![Position status](gallery/position-status.png)
![Submission requests](gallery/submission-request.png)

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 21 (LTS) |
| Framework | Spring Boot 3.5.3 |
| Templates | Thymeleaf 3 + Layout Dialect |
| Frontend | Bootstrap 5.3, Alpine.js 3, htmx 2, DataTables 2, Quill 2 |
| Database | PostgreSQL 17 |
| Migrations | Flyway |
| Data access | Spring JDBC (`NamedParameterJdbcTemplate`) — no ORM |
| Security | Spring Security 6 (form login, BCrypt, CSRF, role hierarchy) |
| Email | Spring Mail (SMTP) |
| Scheduling | Spring `@Scheduled` |
| Build | Gradle 8 (Kotlin DSL) |
| Container | Docker + Docker Compose |
| Tests | JUnit 5, Testcontainers, MockMvc, Mockito |

> **Legacy branch:** The original Flask 1.1 / SQLite application is preserved on
> the `master` branch for reference.

---

## Prerequisites

- Java 21+
- Docker & Docker Compose

---

## Quick Start (Docker)

The easiest way to run the application is with Docker Compose, which starts
both PostgreSQL and the application together:

```bash
# Clone the repository and switch to the Spring Boot branch
git clone https://github.com/gabegm/Headcount-Planning-Management-System
cd Headcount-Planning-Management-System
git checkout migration/spring-boot

# Build and start everything (database + app)
docker compose up --build
```

Open <http://localhost:8080> in a browser.

> **Note:** On the first run Docker will pull images and build the JAR —
> this takes a few minutes. Subsequent starts are much faster.

Default demo credentials (change after first login):

| Email | Password | Role |
|-------|----------|------|
| `admin@example.com` | `changeme` | Admin — full access |
| `manager@example.com` | `changeme` | User — Engineering functions |
| `employee@example.com` | `changeme` | User — Backend, Frontend, Data |

---

## Local Development

Use this approach when you want fast iteration with `bootRun` and live reloading.

### 1. Start the database

The application requires a running PostgreSQL instance. Start just the database
service via Docker Compose **before** running the application:

```bash
# Run from the repository root
docker compose up db -d
```

Verify it is healthy:

```bash
docker compose ps db   # Status should show "healthy"
```

### 2. Run the application

```bash
cd app
./gradlew bootRun --args='--spring.profiles.active=dev'
```

The `dev` profile activates the seed migration (`db/seed/`) so demo positions,
users, and submissions are loaded automatically. Without it only the schema
migration runs (appropriate for production).

The application connects to `localhost:5432` with the default credentials
(`gatekeeping` / `gatekeeping`) which match the Docker Compose database service.
No extra environment variables are needed for local development.

Open <http://localhost:8080> in a browser.

### 3. Environment variables (optional overrides)

Only set these if you are connecting to a non-default database:

```bash
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=gatekeeping
export DB_USER=gatekeeping
export DB_PASSWORD=gatekeeping
export MAIL_HOST=localhost
export MAIL_PORT=1025
export MAIL_PASSWORD=
```

---

## Building

```bash
cd app
./gradlew build
```

The fat JAR is produced at `app/build/libs/headcount-*.jar`.

---

## Running Tests

Tests use [Testcontainers](https://testcontainers.com/) — Docker must be
running.

```bash
cd app
./gradlew test
```

Test categories:

| Category | What it covers |
|----------|---------------|
| Repository tests | JDBC queries against a real PostgreSQL container |
| Controller tests | Full HTTP request/response via MockMvc |
| Security tests | Auth, access control, CSRF enforcement |
| Service unit tests | Business logic with Mockito mocks |

---

## Project Structure

```
app/
├── src/main/java/com/gaucimaistre/headcount/
│   ├── config/          # SecurityConfig
│   ├── controller/      # MVC controllers (Auth, Home, Position, Submission, Admin, …)
│   ├── mapper/          # JDBC RowMappers
│   ├── model/           # Java records (domain model) + enums
│   ├── repository/      # JDBC repositories (NamedParameterJdbcTemplate)
│   ├── scheduler/       # @Scheduled background tasks
│   ├── security/        # UserDetails + UserDetailsService
│   └── service/         # Business logic layer
├── src/main/resources/
│   ├── db/
│   │   ├── migration/   # Flyway V1 — schema only (runs on ALL environments)
│   │   └── seed/        # Flyway V2 — demo data (dev profile + tests only)
│   ├── templates/       # Thymeleaf templates
│   │   ├── layout/      # Base layouts (main, admin, auth)
│   │   ├── admin/       # 17 admin pages
│   │   ├── auth/        # Login, register, password reset
│   │   ├── dashboard/   # Analytics charts (Plotly.js)
│   │   ├── position/    # Position list + detail
│   │   └── submission/  # Submission list, create, change
│   └── static/          # CSS, images
└── src/test/            # Integration + unit tests
```

---

## Seed Data & Profiles

Flyway migrations are split into two directories with different lifecycles:

| Directory | Profile | Purpose |
|-----------|---------|---------|
| `db/migration/` | Always | Schema DDL — safe to run in production |
| `db/seed/` | `dev` only | Demo positions, users, and submissions |

The `dev` profile is enabled by default in `docker-compose.yml`
(`SPRING_PROFILES_ACTIVE=dev`). To suppress seed loading (e.g., in a staging
or production deployment), override the variable:

```bash
SPRING_PROFILES_ACTIVE= docker compose up
```

### Demo data included

- **27 positions** across Backend, Frontend, Mobile, and Data Engineering
  functions — all with `start_date`, `salary`, and `hours` populated so the
  dashboard charts render immediately.
- **14 budget plan lines** (`is_budget=TRUE`) paired against 13 actual
  positions, giving the FTE and Cost charts both a Budget and Actual series.
- **5 gatekeeping cycles** (Q4 2025 through Q4 2026).
- **4 sample submissions** in different statuses (approved, rejected, on-hold)
  with associated change records.

---

## Configuration Reference

Key properties in `application.yml` (all overridable via environment variables):

| Property | Env var | Default |
|----------|---------|---------|
| `spring.datasource.url` | *(composed from below)* | `jdbc:postgresql://localhost:5432/gatekeeping` |
| `spring.datasource.username` | `DB_USER` | `gatekeeping` |
| `spring.datasource.password` | `DB_PASSWORD` | `gatekeeping` |
| `spring.mail.host` | `MAIL_HOST` | `localhost` |
| `spring.mail.port` | `MAIL_PORT` | `1025` |
| `server.servlet.session.timeout` | — | `20m` |

---

## Troubleshooting

**`password authentication failed for user "gatekeeping"`**
The application started before the database was ready. Make sure the `db`
Docker Compose service is running and healthy **before** running `bootRun`:
```bash
docker compose up db -d
docker compose ps db   # wait until Status = healthy
```

**`Connection refused` on port 5432**
Docker is not running, or the `db` service has not been started. Run
`docker compose up db -d` from the repository root.

**`java.lang.UnsupportedClassVersionError`**
You are using a JDK older than 21. Check with `java -version` and install
[Temurin 21](https://adoptium.net/).

**Port 8080 already in use**
Another process is using port 8080. Either stop it or change the application
port: `./gradlew bootRun --args='--server.port=9090'`.
