# Headcount Planning System – Migration Plan
## Flask → Java + Spring Boot

---

## Important Correction

The application is **not Django** — it is a **Flask** application. The core framework is Flask 1.1.1 with raw SQLite3 queries (no ORM), Jinja2 templates, Blueprint-based routing, and a separate Docker service for background task scheduling.

---

## Framework Recommendation: Java 21 + Spring Boot 3

Java + Spring Boot is the right call for this application. Here's why it fits well:

| Concern | Flask (current) | Spring Boot (target) |
|---|---|---|
| Auth & RBAC | Custom decorator + session | Spring Security (battle-tested, built-in RBAC) |
| Database | SQLite + raw SQL | PostgreSQL + Spring JDBC (`NamedParameterJdbcTemplate`) |
| Templates | Jinja2 | Thymeleaf (almost identical concept) |
| Background tasks | `schedule` library (separate Docker service) | `@Scheduled` (built into the app process) |
| Email | smtplib + Office365 SMTP | Spring Mail (JavaMailSender) |
| File upload | werkzeug | Spring `MultipartFile` |
| CSV import | pandas | OpenCSV / Apache Commons CSV |
| Analytics data | pandas DataFrames | Java Streams / a lightweight SQL aggregation |
| Charts | Plotly.js (client-side) | Plotly.js — keep, no migration needed |
| Audit trail | Custom SQL inserts | Explicit `INSERT` via `JdbcTemplate` in `AuditService` |
| CSS framework | Bootstrap 4.1.0 (2018) | Bootstrap 5.3 |
| JS interactivity | jQuery 3.3.1 | Alpine.js (3KB reactive) |
| AJAX / partial updates | jQuery `$.ajax` + `location.reload()` | htmx (declarative server-driven partials) |
| Icon library | Feather Icons | Bootstrap Icons 1.11 |
| Data tables | DataTables 1.10.16 + jQuery | DataTables 2.x (no jQuery dependency) |
| Rich text editor | Summernote (jQuery-based) | Quill 2.x |

### Why not alternatives?
- **Kotlin + Spring Boot**: A valid modern option, but Java 21 virtual threads (Project Loom) close much of the verbosity gap. Stick with Java for broadest hiring/maintainability pool.
- **Go + Gin**: Excellent performance, but the team is already clearly on a JVM-friendly path given the request.
- **Node.js / NestJS**: Would require rebuilding the same patterns from scratch; Spring gives you everything out of the box for an enterprise internal tool like this.

---

## Proposed Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 (LTS) |
| Framework | Spring Boot (latest stable) |
| Security | Spring Security 6 |
| Persistence | Spring JDBC (`NamedParameterJdbcTemplate`) |
| Database | PostgreSQL 16 (upgrade from SQLite) |
| Templates | Thymeleaf 3 + Thymeleaf Extras (Spring Security) |
| Email | Spring Mail + Office 365 SMTP |
| File Uploads | Spring `MultipartFile` + OpenCSV |
| Scheduling | Spring `@Scheduled` |
| Build Tool | Gradle (Kotlin DSL) or Maven |
| Containerisation | Docker + Docker Compose (minimal changes) |
| Testing | JUnit 5 + Mockito + Spring Boot Test + Testcontainers |

---

## Application Overview (what's being migrated)

The app ("Gatekeeping") is an internal Headcount Planning Management System for a multi-company organisation. It manages:

1. **Positions** – job roles with salary, benefits, reporting lines, status
2. **Budget positions** – planned headcount separate from actuals
3. **Submissions** – change requests (salary change, new hire, deactivation, etc.)
4. **Submission cycles (Gatekeeping)** – admin-controlled windows for submissions
5. **Approval workflow** – On-hold → Approved / Rejected
6. **Dashboard analytics** – FTE and cost trends via Plotly.js charts
7. **User management** – ADMIN / USER roles, function-based access
8. **Org hierarchy** – Pillar → Department → Function → Company
9. **Exchange rates** – Multi-currency (EUR, GBP, COP, HRK)
10. **Audit trail** – Every action logged with IP, timestamp, user
11. **CMS** – Editable Help and FAQ pages
12. **Background tasks** – Nightly position updates + token expiry

---

## Database Migration

### SQLite → PostgreSQL

The schema is well-defined in `schema.sql` (528 lines). A Flyway migration script will be created to:

1. Recreate all tables as PostgreSQL DDL
2. Migrate seed data (companies, pillars, departments, functions, statuses)
3. Apply proper PostgreSQL types (`SERIAL`, `BOOLEAN`, `TIMESTAMP WITH TIME ZONE`, `TEXT`)
4. Add foreign key constraints (currently missing in SQLite schema)
5. Add indexes (position `number + isBudget`, page `name + title`)

### Tables to migrate

| Table | Notes |
|---|---|
| `user` | Add `created_at`; `type` → enum (`USER`, `ADMIN`); `active` → `BOOLEAN` |
| `user_function` | Composite PK stays; becomes a `@ManyToMany` join table in JPA |
| `position` | `isBudget` → `BOOLEAN`; `salary`, `fringe_benefit`, etc. → `DECIMAL` |
| `submission` | `position_id TEXT` (stores position number) → consider FK to position |
| `submission_change` | Audit detail, stays as-is |
| `gatekeeping` | Submission cycle windows |
| `audit` | Append-only log; add index on `user_id`, `date` |
| `page` | CMS content (help + FAQ) |
| All lookup tables | `company`, `pillar`, `department`, `function`, `position_status`, `recruitment_status`, `submission_reason`, `submission_status`, `exchange_rate` |

---

## Project Structure (Spring Boot)

```
gatekeeping/
├── src/
│   ├── main/
│   │   ├── java/com/gatekeeping/
│   │   │   ├── GatekeepingApplication.java
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfig.java        ← Spring Security (replaces @login_required, @admin_required)
│   │   │   │   ├── MailConfig.java             ← JavaMailSender (replaces smtplib)
│   │   │   │   └── SchedulerConfig.java        ← @EnableScheduling
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java         ← auth blueprint
│   │   │   │   ├── HomeController.java         ← home blueprint
│   │   │   │   ├── PositionController.java     ← position blueprint
│   │   │   │   ├── SubmissionController.java   ← submission blueprint
│   │   │   │   ├── DashboardController.java    ← dashboard blueprint
│   │   │   │   ├── HelpController.java
│   │   │   │   ├── FaqController.java
│   │   │   │   └── AdminController.java        ← admin blueprint (40+ routes)
│   │   │   ├── model/                          ← Plain Java records/POJOs (no ORM annotations)
│   │   │   │   ├── User.java
│   │   │   │   ├── Position.java
│   │   │   │   ├── Submission.java
│   │   │   │   ├── SubmissionChange.java
│   │   │   │   ├── Gatekeeping.java
│   │   │   │   ├── AuditLog.java
│   │   │   │   ├── Page.java
│   │   │   │   ├── Company.java
│   │   │   │   ├── Pillar.java
│   │   │   │   ├── Department.java
│   │   │   │   ├── Function.java
│   │   │   │   ├── ExchangeRate.java
│   │   │   │   └── enums/
│   │   │   │       ├── UserType.java
│   │   │   │       ├── PositionStatus.java
│   │   │   │       └── SubmissionStatus.java
│   │   │   ├── repository/                     ← JDBC repositories with NamedParameterJdbcTemplate (replaces api/*.py)
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── PositionRepository.java
│   │   │   │   ├── SubmissionRepository.java
│   │   │   │   └── ...
│   │   │   ├── mapper/                         ← RowMapper<T> implementations (replaces sqlite3 row_factory)
│   │   │   │   ├── UserRowMapper.java
│   │   │   │   ├── PositionRowMapper.java
│   │   │   │   └── ...
│   │   │   ├── service/                        ← Business logic layer
│   │   │   │   ├── UserService.java
│   │   │   │   ├── PositionService.java
│   │   │   │   ├── SubmissionService.java
│   │   │   │   ├── DashboardService.java
│   │   │   │   ├── AuditService.java
│   │   │   │   ├── MailService.java
│   │   │   │   └── CsvImportService.java
│   │   │   ├── security/
│   │   │   │   └── UserDetailsServiceImpl.java ← Loads user from DB via JDBC for Spring Security
│   │   │   └── scheduler/
│   │   │       └── ScheduledTasks.java         ← Replaces tasks.py + separate Docker service
│   │   ├── resources/
│   │   │   ├── application.yml                 ← App config (replaces config.py + gunicorn_config.py)
│   │   │   ├── db/migration/                   ← Flyway scripts
│   │   │   │   ├── V1__init_schema.sql
│   │   │   │   └── V2__seed_data.sql
│   │   │   ├── templates/                      ← Thymeleaf (replaces Jinja2 templates)
│   │   │   │   ├── layout/
│   │   │   │   ├── auth/
│   │   │   │   ├── admin/
│   │   │   │   ├── position/
│   │   │   │   ├── submission/
│   │   │   │   ├── help/
│   │   │   │   ├── faq/
│   │   │   │   └── error/
│   │   │   └── static/                         ← Static assets (Bootstrap, jQuery, Plotly — reuse as-is)
│   └── test/
│       └── java/com/gatekeeping/
│           ├── controller/                     ← Controller tests (MockMvc)
│           ├── service/                        ← Service unit tests
│           └── integration/                    ← Testcontainers integration tests
├── build.gradle.kts                            ← (or pom.xml)
├── Dockerfile
└── docker-compose.yml
```

---

## Migration Phases

### Phase 1 — Project Scaffolding & Infrastructure
- Bootstrap Spring Boot project (Spring Initializr)
- Add dependencies: Web, Thymeleaf, Spring Security, Spring JDBC, PostgreSQL driver, Flyway, Spring Mail, Spring Scheduling, OpenCSV, Lombok
- Set up Docker Compose with PostgreSQL service replacing SQLite volume
- Configure `application.yml` with datasource, mail, security, scheduling properties
- Set up Flyway with V1 (schema) and V2 (seed data) migration scripts
- Eliminate the separate `schedule` Docker service (scheduling moves into the app)

### Phase 2 — Domain Model & JDBC Repositories
- Create plain Java `record` classes (or POJOs) for all 15+ tables — no ORM annotations, no Hibernate
- Define enums for `UserType`, `PositionStatus`, `RecruitmentStatus`, `SubmissionStatus`, `SubmissionReason`
- Create a `RowMapper<T>` per domain object to map `ResultSet` columns → Java fields (replaces SQLite3's `row_factory`)
- Create a repository class per domain object using `NamedParameterJdbcTemplate`, porting queries from `api/*.py` 1:1
  - Named parameters (`:userId`) replace positional `?` placeholders from the Python code
  - Permission-filtered queries (function-based access) expressed cleanly in SQL `WHERE` clauses — no ORM workarounds needed
  - Dashboard aggregation queries (FTE, cost rollup) ported directly from `api/dashboard.py`

### Phase 3 — Security (replaces auth.py + decorators)
- Implement `UserDetailsService` loading users from DB
- Configure Spring Security: form login, logout, session management (20-min timeout)
- Define role hierarchy: `ROLE_ADMIN` inherits from `ROLE_USER`
- Protect URL patterns: `/admin/**` requires `ROLE_ADMIN`; all others require authenticated
- Implement password reset token flow (existing UUID-token pattern)
- Email domain validation on registration (restrict to `@gaucimaistre.com`)
- Add CSRF protection (Spring Security enables this by default — was absent in Flask)
- Thymeleaf Security Extras for role-based template rendering (replaces `{% if g.user.type == 'ADMIN' %}`)

### Phase 4 — Controllers & Services (replaces blueprints + api/*.py)
- Implement one controller per blueprint, delegating to service layer
- **AuthController**: register, login (handled by Spring Security), forgot-password, reset
- **HomeController**: user function listing
- **PositionController**: position list, create, view (JSON endpoint)
- **SubmissionController**: list, create, change detail
- **DashboardController**: analytics, using aggregation queries replacing pandas
- **AdminController**: all 40+ admin routes (user CRUD, org hierarchy CRUD, position/budget import, submission review, gatekeeping cycles, CMS, audit)
- **HelpController / FaqController**: CMS page serving
- Implement function-based permission checks in service layer (mirrors `check_submitter=True` pattern)

### Phase 5 — Frontend Overhaul (see full section below)
- See **Frontend Migration** section for the complete plan
- In brief: Bootstrap 4 → Bootstrap 5.3, jQuery → Alpine.js + htmx, Feather → Bootstrap Icons, DataTables 2.x, Summernote → Quill
- Thymeleaf replaces Jinja2 (server-side rendering approach is preserved)

### Phase 6 — Background Tasks & Email
- Migrate `utils.py::update_position()` → `@Scheduled(cron = "0 0 0 * * *")` in `ScheduledTasks.java`
- Migrate `utils.py::expire_password_reset_tokens()` → `@Scheduled(cron = "0 10 0 * * *")`
- Migrate email sending → `JavaMailSender` with `MimeMessageHelper`
- Remove the `schedule` Docker service; scheduling runs inside the main app

### Phase 7 — File Upload & CSV Import
- Migrate `/admin/position/upload` and `/admin/budget/upload`
- Replace werkzeug file handling with Spring `MultipartFile`
- Replace pandas CSV parsing with OpenCSV `CsvBeanReader`
- Replicate `is_upload_valid()` validation logic in a `CsvImportService`

### Phase 8 — Testing
- Port `test_auth.py` → `AuthControllerTest` (MockMvc)
- Port `test_db.py` → repository integration tests (Testcontainers + PostgreSQL)
- Port `test_factory.py` → `GatekeepingApplicationTests` (context load test)
- Add service-layer unit tests (Mockito)

### Phase 9 — Docker & Deployment
- Update `Dockerfile` (OpenJDK 21 base image, build JAR, run with `java -jar`)
- Update `docker-compose.yml`:
  - Replace Python/Conda web service with Spring Boot JAR service
  - Add `postgres` service (replaces SQLite file volume)
  - Remove `schedule` service (no longer needed)
  - Keep port `5000` or switch to `8080` (standard Spring Boot)
  - Retain HTTPS via SSL cert configuration in `application.yml`

---

## Key Differences & Risks

| Area | Risk | Mitigation |
|---|---|---|
| SQLite → PostgreSQL | Data type changes, missing FK constraints in SQLite | Write Flyway migrations from scratch; don't auto-convert |
| Raw SQL → JDBC | Near 1:1 port from Python sqlite3 to NamedParameterJdbcTemplate | Port `api/*.py` queries directly; named parameters replace positional `?` |
| Analytics data | pandas DataFrames | Port aggregation SQL directly to repository; compute FTE/cost in Java Streams |
| Session-based auth | Flask sessions vs Spring Security session | Spring Security default session management handles this well |
| Email credentials in `keys.py` | Not in repo; credentials must be provided | Move to environment variables / `application.yml` externalized config |
| SSL certificates | Currently baked into Gunicorn command | Configure via `server.ssl.*` in `application.yml` or terminate at reverse proxy |
| CSRF | Not present in Flask app | Spring Security enables CSRF by default — all forms need `_csrf` tokens (Thymeleaf auto-injects) |
| Bootstrap 4 + jQuery | Outdated (2018) — full frontend overhaul planned | See Frontend Migration section |
| No ORM currently | JDBC keeps this consistent — no new abstraction layer introduced | `NamedParameterJdbcTemplate` is a thin wrapper, no magic |
| Bootstrap 3/4 mix | FAQ/help use Bootstrap 3 panel classes | Rewrite as Bootstrap 5 accordion component |
| jQuery AJAX + location.reload() | All CRUD triggers full page reloads | Replace with htmx partial updates for a snappier UX |
| Summernote (jQuery-based RTE) | Tied to jQuery, abandoned-ish | Replace with Quill 2.x (framework-agnostic) |
| XSS in help/faq | `{{ page['body']|safe }}` bypasses escaping | Sanitise HTML server-side with OWASP Java HTML Sanitizer before storage |
| Debug console.log | Left in production templates | Remove all during migration |

---

## What Can Be Reused As-Is

- The `schema.sql` structure as the basis for Flyway V1
- The `schema.sql` seed data as the basis for Flyway V2
- The overall URL structure (can keep identical routes)
- Business logic rules (deadline checks, domain validation, permission model)
- **Plotly.js** — version-agnostic, no changes needed
- Application logo (`img/logo.png`)

---

## Frontend Migration

### Current State

| Library | Version | Age | Status |
|---|---|---|---|
| Bootstrap | 4.1.0 | 2018 (8 years old) | Outdated — Bootstrap 5.3 is current |
| jQuery | 3.3.1 | 2018 | Unnecessary — modern browser APIs cover all usage |
| DataTables | 1.10.16 | 2017 | Outdated — 2.x available; also jQuery-dependent |
| Summernote | ~2018 | 2018 | Largely abandoned, jQuery-dependent |
| Feather Icons | unknown | — | Superseded by Lucide; Bootstrap Icons is better fit |
| Plotly.js | "latest" | — | Keep — no issues |
| script.js | 0 lines | — | Empty, delete |

**Additional findings from deep analysis:**
- 34 HTML templates with all JS written inline in `<script>` tags per template
- FAQ/help pages use **Bootstrap 3** panel classes (`panel`, `panel-default`, `panel-heading`) — inconsistent with the rest of the app
- All CRUD in admin pages uses a jQuery AJAX → modal → `location.reload()` pattern (every edit/delete causes a full page refresh)
- The submission create form has **30+ jQuery field show/hide rules** that need careful porting
- `console.log()` debug statements left throughout production templates
- `{{ page['body']|safe }}` in help/faq bypasses XSS escaping — a real vulnerability to fix

---

### Recommended Frontend Stack

This app is an **internal server-rendered enterprise tool** — not a SPA. The right approach is progressive enhancement: keep Thymeleaf for all rendering, and add just enough JS for interactivity. A full React/Vue SPA would add enormous complexity for no benefit here.

| Concern | Old | New | Reason |
|---|---|---|---|
| CSS framework | Bootstrap 4.1 | **Bootstrap 5.3** | No jQuery dependency, improved utilities, better grid, native accordion, offcanvas, floating labels |
| JS interactivity | jQuery 3.3.1 | **Alpine.js 3.x** | 15KB, declarative, zero-config — directly replaces jQuery for dropdowns, field show/hide, conditional rendering |
| AJAX / partial updates | `$.ajax()` + `location.reload()` | **htmx 2.x** | Declarative HTML attributes for server-driven partial page updates; eliminates full reloads; pairs perfectly with Thymeleaf fragments |
| Icon library | Feather Icons | **Bootstrap Icons 1.11** | Native Bootstrap 5 integration, 2000+ icons, no JS required (SVG sprite or `<i>` tag) |
| Data tables | DataTables 1.10.16 | **DataTables 2.x** | jQuery-free version, same API, same export buttons (copy/CSV/Excel/PDF/print) — minimal migration effort |
| Rich text editor | Summernote | **Quill 2.x** | Actively maintained, framework-agnostic, no jQuery dependency |
| Chart library | Plotly.js | **Plotly.js** (keep) | Already version-agnostic and framework-independent |
| Template engine | Jinja2 | **Thymeleaf 3** | Server-side rendering preserved; `th:fragment` for htmx partial responses |
| Form validation | HTML5 + none | **Bootstrap 5 validation classes** | Built-in Bootstrap 5 form validation with `.was-validated`; server errors via Thymeleaf |
| Build tool | None (CDN/vendor files) | **Vite** (optional) | Bundle and tree-shake if needed; CDN links acceptable for internal tool |

---

### Why htmx + Alpine.js instead of React/Vue?

| Factor | React/Vue SPA | htmx + Alpine.js |
|---|---|---|
| Complexity | High — requires full API layer, state management, bundler | Low — works with existing Thymeleaf server rendering |
| Learning curve | High for Java backend team | Very low — it's just HTML attributes |
| Bundle size | 100–300KB+ | ~30KB total (htmx 14KB + Alpine 15KB) |
| Server changes needed | Must add REST API endpoints | None — controllers return HTML fragments |
| SEO / accessibility | Extra work | Free — server renders full HTML |
| Matches the codebase style | No | Yes — declarative, co-located with HTML |

---

### Bootstrap 4 → Bootstrap 5 Migration Details

**Breaking changes to address:**

| Bootstrap 4 Pattern | Bootstrap 5 Replacement |
|---|---|
| `data-toggle="modal"` | `data-bs-toggle="modal"` |
| `data-target="#id"` | `data-bs-target="#id"` |
| `data-dismiss="modal"` | `data-bs-dismiss="modal"` |
| `data-toggle="collapse"` | `data-bs-toggle="collapse"` |
| `data-toggle="dropdown"` | `data-bs-toggle="dropdown"` |
| `btn-default` (BS3 leftover) | `btn-secondary` |
| `panel`, `panel-default`, `panel-heading`, `panel-body` (BS3) | `accordion`, `accordion-item`, `accordion-header`, `accordion-body` |
| `label label-primary` (BS3) | `badge bg-primary` |
| `mr-*`, `ml-*`, `pr-*`, `pl-*` | `me-*`, `ms-*`, `pe-*`, `ps-*` (logical properties) |
| `float-left` / `float-right` | `float-start` / `float-end` |
| jQuery required for JS components | No jQuery required |
| `.form-group` | Removed — use spacing utilities directly |
| `.form-inline` | Removed — use grid/flex |
| `.input-group-prepend` / `.input-group-append` | Removed — put elements directly in `.input-group` |
| `.form-label-group` (custom floating label in signin.css) | Bootstrap 5 native `.form-floating` |
| Hamburger: `navbar-toggler` with `data-toggle="collapse"` | Same but `data-bs-toggle="collapse"` |

**New Bootstrap 5 features to adopt:**
- **Floating labels** — replace the custom `signin.css` floating label hack (88 lines) with `<div class="form-floating">`
- **Accordion** — replace Bootstrap 3 panel-based FAQ/help with native accordion
- **Offcanvas** — replace the admin sidebar (currently `d-none d-md-block`) with a proper mobile-friendly offcanvas sidebar
- **CSS custom properties** — theming without Sass
- **RTL support** — if ever needed

---

### jQuery Removal: Pattern-by-Pattern Replacement

**1. AJAX data fetch before modal (most common admin pattern)**
```html
<!-- OLD: jQuery -->
<button onclick="$.get('/admin/user/1', function(data) { $('#name').val(data.name); $('#editModal').modal('show'); })">Edit</button>

<!-- NEW: htmx -->
<button hx-get="/admin/user/1/form"
        hx-target="#editModal .modal-body"
        hx-on::after-request="bootstrap.Modal.getOrCreateInstance('#editModal').show()"
        data-bs-toggle="modal" data-bs-target="#editModal">Edit</button>
<!-- The server returns a Thymeleaf fragment of the pre-populated form -->
```

**2. location.reload() after CRUD**
```html
<!-- OLD: jQuery AJAX + reload -->
$.post(url, data, function() { location.reload(); });

<!-- NEW: htmx -->
<form hx-post="/admin/user/1/update" hx-target="#userTable" hx-swap="outerHTML">
<!-- Server returns updated <table> fragment — no reload -->
```

**3. Submission form field show/hide (30+ jQuery rules)**
```html
<!-- OLD: jQuery -->
$('#reason').change(function() {
  if ($(this).val() == 'Salary Change') { $('#salaryField').show(); }
});

<!-- NEW: Alpine.js -->
<div x-data="{ reason: '' }">
  <select x-model="reason" name="reason">...</select>
  <div x-show="reason === 'Salary Change'">
    <input name="salary" />
  </div>
</div>
```

**4. DataTables initialisation**
```javascript
// OLD: requires jQuery
$('#example').DataTable({ dom: 'Bfrtip', buttons: ['csv','excel','pdf','print'] });

// NEW: DataTables 2.x (no jQuery)
new DataTable('#example', { layout: { topStart: 'buttons' }, buttons: ['csv','excel','pdf','print'] });
```

**5. Summernote → Quill (Help/FAQ CMS editor)**
```javascript
// OLD
$('#summernote').summernote({ height: 500 });
$.post(url, { content: $('#summernote').summernote('code') });

// NEW
const quill = new Quill('#editor', { theme: 'snow', modules: { toolbar: [...] } });
// On save: document.querySelector('[name="body"]').value = quill.getSemanticHTML();
```

---

### Template-by-Template Migration Notes

| Template | Changes Required |
|---|---|
| `base.html` | `data-toggle` → `data-bs-toggle`; Feather → Bootstrap Icons; remove jQuery/Popper script tags; add Alpine.js + htmx |
| `layout/dashboard.html` | Admin sidebar: add `offcanvas` variant for mobile; `d-none d-md-block` → offcanvas toggle on small screens; Feather icons → Bootstrap Icons |
| `layout/auth.html` | `form-label-group` (custom) → Bootstrap 5 `form-floating`; remove signin.css dependency |
| `auth/login.html` | `data-toggle="modal"` → `data-bs-toggle`; update alert dismissal |
| `auth/register.html` | Update validation helper text to use BS5 pattern |
| `submission/create.html` | Replace all jQuery show/hide with Alpine.js `x-show`/`x-model`; remove all inline `<script>` AJAX |
| `submission/index.html` | DataTables 2.x init; htmx for position view modal |
| `faq/index.html` + `admin/faq.html` | Replace Bootstrap 3 `panel` → Bootstrap 5 `accordion`; replace `label label-primary` → `badge bg-primary` |
| `help/index.html` + `admin/help.html` | Replace Summernote → Quill; fix XSS: sanitise stored HTML server-side |
| All `admin/*.html` (13 files) | `data-toggle`/`data-target` → `data-bs-*`; jQuery AJAX → htmx; `location.reload()` → `hx-target` swap; `btn-default` → `btn-secondary`; DataTables 2.x; remove `console.log` |
| `error/404.html` | `btn-default` → `btn-secondary` |

---

### Frontend Build & Asset Management

The current app vendored all assets directly into `static/` (no build step). For the migrated app:

- **CDN links acceptable** for an internal tool — avoids build complexity
- Or use **Webjars** (Spring Boot Maven/Gradle integration) to manage Bootstrap 5, Alpine.js, htmx as dependencies with version pinning
- Plotly.js can stay as a CDN link (it's very large; no benefit to bundling)
- Custom CSS remains minimal (< 200 lines total) — no Sass needed
- If a build step is desired, **Vite** is the simplest choice

---

## Todos (Updated)

1. `scaffold-project` — Bootstrap Spring Boot project with all required dependencies
2. `flyway-migrations` — Write V1 schema + V2 seed data Flyway scripts from schema.sql
3. `jdbc-repositories` — Create plain Java records + `RowMapper<T>` classes + `NamedParameterJdbcTemplate` repositories (replaces JPA; ports `api/*.py` queries 1:1)
4. `spring-security` — Configure Spring Security (auth, RBAC, session timeout, CSRF)
5. `auth-controller` — Implement AuthController (register, forgot-password, reset)
6. `home-position-controllers` — HomeController, PositionController
7. `submission-dashboard-controllers` — SubmissionController, DashboardController
8. `admin-controller` — AdminController (40+ routes)
9. `help-faq-controllers` — HelpController, FaqController
10. `services` — All service classes with business logic
11. `frontend-assets` — Set up Bootstrap 5.3, Alpine.js, htmx, Bootstrap Icons, DataTables 2.x, Quill (via CDN or Webjars)
12. `thymeleaf-templates` — Recreate all 34 Jinja2 templates in Thymeleaf with Bootstrap 5 markup
13. `htmx-partials` — Add Thymeleaf fragment endpoints for all htmx-driven CRUD operations (replaces jQuery AJAX + reload)
14. `alpine-interactivity` — Port submission form field show/hide and other jQuery interactivity to Alpine.js
15. `scheduled-tasks` — Migrate background tasks and email
16. `csv-import` — File upload and CSV import service
17. `xss-sanitizer` — Add OWASP Java HTML Sanitizer for help/faq content before storage
18. `testing` — Port test suite; add Testcontainers integration tests
19. `docker-update` — Update Dockerfile and docker-compose.yml
