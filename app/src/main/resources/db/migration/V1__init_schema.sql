-- V1: Initial schema
-- Converted from SQLite schema.sql to PostgreSQL
-- Key changes from SQLite:
--   - AUTOINCREMENT -> GENERATED ALWAYS AS IDENTITY
--   - INTEGER -> BOOLEAN for isBudget
--   - TEXT -> VARCHAR / TEXT (TEXT is fine in PostgreSQL)
--   - REAL -> NUMERIC for exchange rates
--   - "user" is a reserved word in PostgreSQL - quoted throughout
--   - "function" is a reserved word in PostgreSQL - quoted throughout
--   - Proper FK constraints enforced (SQLite did not enforce them)
--   - TIMESTAMP DEFAULT CURRENT_TIMESTAMP -> TIMESTAMPTZ DEFAULT now()

CREATE TABLE exchange_rate (
    id    INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name  TEXT    NOT NULL,
    code  TEXT    NOT NULL,
    rate  NUMERIC NOT NULL
);

CREATE TABLE company (
    id               INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    exchange_rate_id INTEGER NOT NULL REFERENCES exchange_rate (id),
    name             TEXT    NOT NULL
);

CREATE TABLE pillar (
    id   INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name TEXT NOT NULL
);

CREATE TABLE department (
    id   INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name TEXT NOT NULL
);

CREATE TABLE "function" (
    id   INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name TEXT NOT NULL
);

CREATE TABLE position_status (
    id   INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name TEXT NOT NULL
);

CREATE TABLE recruitment_status (
    id   INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name TEXT NOT NULL
);

CREATE TABLE submission_status (
    id   INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name TEXT NOT NULL
);

CREATE TABLE submission_reason (
    id   INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name TEXT NOT NULL
);

CREATE TABLE "user" (
    id                   INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email                TEXT    NOT NULL UNIQUE,
    password             TEXT    NOT NULL,
    password_reset_token TEXT,
    type                 TEXT    NOT NULL DEFAULT 'USER',
    active               BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE user_function (
    user_id     INTEGER NOT NULL REFERENCES "user" (id) ON DELETE CASCADE,
    function_id INTEGER NOT NULL REFERENCES "function" (id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, function_id)
);

CREATE TABLE gatekeeping (
    id                  INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    date                DATE NOT NULL,
    submission_deadline DATE NOT NULL,
    notes               TEXT
);

CREATE TABLE position (
    id                          INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    status_id                   INTEGER NOT NULL REFERENCES position_status (id),
    recruitment_status_id       INTEGER NOT NULL REFERENCES recruitment_status (id),
    number                      TEXT    NOT NULL,
    pillar_id                   INTEGER NOT NULL REFERENCES pillar (id),
    company_id                  INTEGER NOT NULL REFERENCES company (id),
    department_id               INTEGER NOT NULL REFERENCES department (id),
    function_id                 INTEGER NOT NULL REFERENCES "function" (id),
    is_budget                   BOOLEAN NOT NULL DEFAULT FALSE,
    title                       TEXT,
    functional_reporting_line   TEXT,
    disciplinary_reporting_line TEXT,
    holder                      TEXT,
    hours                       INTEGER,
    start_date                  DATE,
    end_date                    DATE,
    salary                      NUMERIC,
    fringe_benefit              NUMERIC,
    social_security_contribution NUMERIC,
    performance_bonus           NUMERIC,
    super_bonus                 NUMERIC,
    management_bonus            NUMERIC
);

CREATE UNIQUE INDEX idx_position_number ON position (number, is_budget);

CREATE TABLE submission (
    id              INTEGER    GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    submitter_id    INTEGER    NOT NULL REFERENCES "user" (id),
    gatekeeping_id  INTEGER    NOT NULL REFERENCES gatekeeping (id),
    position_id     TEXT       NOT NULL,
    status_id       INTEGER    NOT NULL DEFAULT 1 REFERENCES submission_status (id),
    reason_id       INTEGER    NOT NULL REFERENCES submission_reason (id),
    rationale       TEXT       NOT NULL,
    effective_date  DATE       NOT NULL,
    submitted       TIMESTAMPTZ NOT NULL DEFAULT now(),
    comment         TEXT
);

CREATE TABLE submission_change (
    id            INTEGER    GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    submission_id INTEGER    NOT NULL REFERENCES submission (id) ON DELETE CASCADE,
    submitted     TIMESTAMPTZ NOT NULL DEFAULT now(),
    field         TEXT       NOT NULL,
    change        TEXT       NOT NULL
);

CREATE TABLE page (
    id        INTEGER    GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    submitted TIMESTAMPTZ NOT NULL DEFAULT now(),
    edited    TIMESTAMPTZ NOT NULL DEFAULT now(),
    name      TEXT       NOT NULL,
    title     TEXT       NOT NULL,
    body      TEXT       NOT NULL
);

CREATE UNIQUE INDEX idx_page_name ON page (name, title);

CREATE TABLE audit (
    id         INTEGER    GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id    INTEGER    REFERENCES "user" (id) ON DELETE SET NULL,
    date       TIMESTAMPTZ NOT NULL DEFAULT now(),
    user_agent TEXT       NOT NULL,
    ip_address TEXT       NOT NULL,
    domain     TEXT       NOT NULL,
    tbl        TEXT       NOT NULL,
    row_id     INTEGER,
    function   TEXT       NOT NULL,
    action     TEXT       NOT NULL
);

CREATE INDEX idx_audit_user_date ON audit (user_id, date DESC);
