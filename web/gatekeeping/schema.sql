-- Initialize the database.
-- Drop any existing data and create empty tables.

DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS user_function;
DROP TABLE IF EXISTS submission;
DROP TABLE IF EXISTS submission_change;
DROP TABLE IF EXISTS gatekeeping;
DROP TABLE IF EXISTS position;
DROP TABLE IF EXISTS budget;
DROP TABLE IF EXISTS company;
DROP TABLE IF EXISTS pillar;
DROP TABLE IF EXISTS department;
DROP TABLE IF EXISTS function;
DROP TABLE IF EXISTS submission_reason;
DROP TABLE IF EXISTS submission_status;
DROP TABLE IF EXISTS position_status;
DROP TABLE IF EXISTS recruitment_status;
DROP TABLE IF EXISTS exchange_rate;
DROP TABLE IF EXISTS page;
DROP TABLE IF EXISTS audit;

-- Create tables

CREATE TABLE user (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    email TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    password_reset_token TEXT,
    type text NOT NULL DEFAULT 'USER',
    active INTEGER DEFAULT 0
);

CREATE TABLE user_function (
    user_id INTEGER,
    function_id INTEGER,
    PRIMARY KEY(user_id, function_id)
);

CREATE TABLE gatekeeping (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    date DATE NOT NULL,
    submission_deadline DATE NOT NULL,
    notes TEXT
);

CREATE TABLE submission (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    submitter_id INTEGER NOT NULL,
    gatekeeping_id INTEGER NOT NULL,
    position_id TEXT NOT NULL,
    status_id INTEGER NOT NULL DEFAULT 1,
    reason_id INTEGER NOT NULL,
    rationale TEXT NOT NULL,
    effective_date DATE NOT NULL,
    submitted TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    comment TEXT,
    FOREIGN KEY (submitter_id) REFERENCES user(id),
    FOREIGN KEY (gatekeeping_id) REFERENCES gatekeeping(id),
    FOREIGN KEY (status_id) REFERENCES submission_status(id),
    FOREIGN KEY (reason_id) REFERENCES submission_reason(id)
);

CREATE TABLE submission_change (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    submission_id INTEGER NOT NULL,
    submitted TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    field TEXT NOT NULL,
    change TEXT NOT NULL,
    FOREIGN KEY (submission_id) REFERENCES submission(id)
);

CREATE TABLE position (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    status_id INTEGER NOT NULL,
    recruitment_status_id INTEGER NOT NULL,
    number TEXT NOT NULL,
    pillar_id INTEGER NOT NULL,
    company_id INTEGER NOT NULL,
    department_id INTEGER NOT NULL,
    function_id INTEGER NOT NULL,
    isBudget INTEGER NOT NULL,
    title TEXT,
    functional_reporting_line TEXT,
    disciplinary_reporting_line TEXT,
    holder TEXT,
    hours INT,
    start_date DATE,
    end_date DATE,
    salary INTEGER,
    fringe_benefit INTEGER,
    social_security_contribution INTEGER,
    performance_bonus INTEGER,
    super_bonus INTEGER,
    management_bonus INTEGER,
    FOREIGN KEY (company_id) REFERENCES company(id),
    FOREIGN KEY (pillar_id) REFERENCES pillar(id),
    FOREIGN KEY (department_id) REFERENCES department(id),
    FOREIGN KEY (function_id) REFERENCES function(id),
    FOREIGN KEY (status_id) REFERENCES position_status(id),
    FOREIGN KEY (recruitment_status_id) REFERENCES recruitment_status(id)
);

CREATE TABLE company (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    exchange_rate_id INTEGER NOT NULL,
    name TEXT NOT NULL,
    FOREIGN KEY (exchange_rate_id) REFERENCES exchange_rate(id)
);

CREATE TABLE pillar (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL
);

CREATE TABLE department (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL
);

CREATE TABLE function (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL
);

CREATE TABLE submission_reason (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL
);

CREATE TABLE submission_status (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL
);

CREATE TABLE position_status (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL
);

CREATE TABLE recruitment_status (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL
);

CREATE TABLE exchange_rate (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    code TEXT NOT NULL,
    rate REAL NOT NULL
);

CREATE TABLE page (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    submitted TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    edited TIMESTAMP NOT NULL,
    name TEXT NOT NULL,
    title TEXT NOT NULL,
    body TEXT NOT NULL
);

CREATE TABLE audit (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER,
    date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_agent TEXT NOT NULL,
    ip_address TEXT NOT NULL,
    domain TEXT NOT NULL,
    tbl TEXT NOT NULL,
    row_id INTEGER,
    function TEXT NOT NULL,
    action TEXT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(id)
);

-- Create unique indexes
CREATE UNIQUE INDEX idx_page_name ON page (name, title);
CREATE UNIQUE INDEX idx_position_number on position(number, isBudget);

-- Insert values

INSERT INTO user (email, password, type, active)
VALUES ('gabriel.gaucimaistre@gaucimaistre.com', 'pbkdf2:sha256:50000$AbksDRJY$a14735ab7def1d6a36591f478e67d5352459f2e7d489c6ef86c6053314f7951d', 'ADMIN', 1);

INSERT INTO position_status (name)
VALUES ('Occupied');

INSERT INTO position_status (name)
VALUES ('Vacant');

INSERT INTO position_status (name)
VALUES ('External');

INSERT INTO position_status (name)
VALUES ('Leaver');

INSERT INTO position_status (name)
VALUES ('Pending Approval');

INSERT INTO recruitment_status (name)
VALUES ('Contracted');

INSERT INTO recruitment_status (name)
VALUES ('Deactivated');

INSERT INTO recruitment_status (name)
VALUES ('External');

INSERT INTO recruitment_status (name)
VALUES ('Approved');

INSERT INTO recruitment_status (name)
VALUES ('On-Board');

INSERT INTO recruitment_status (name)
VALUES ('Proposed');

INSERT INTO recruitment_status (name)
VALUES ('Extended');

INSERT INTO company (name, exchange_rate_id)
VALUES ('GM Malta', 1);

INSERT INTO company (name, exchange_rate_id)
VALUES ('GM Casino', 1);

INSERT INTO company (name, exchange_rate_id)
VALUES ('GM Technology Services', 1);

INSERT INTO company (name, exchange_rate_id)
VALUES ('GM Retail Services', 1);

INSERT INTO company (name, exchange_rate_id)
VALUES ('GM Retail Systems', 1);

INSERT INTO company (name, exchange_rate_id)
VALUES ('GM Shop Agency North', 1);

INSERT INTO company (name, exchange_rate_id)
VALUES ('GM Shop Agency East', 1);

INSERT INTO company (name, exchange_rate_id)
VALUES ('GM Group', 1);

INSERT INTO company (name, exchange_rate_id)
VALUES ('Sports Services South America', 2);

INSERT INTO company (name, exchange_rate_id)
VALUES ('Sport Bet', 3);

INSERT INTO company (name, exchange_rate_id)
VALUES ('GM Services', 4);

INSERT INTO company (name, exchange_rate_id)
VALUES ('Maritimo D', 1);

INSERT INTO department (name)
VALUES ('Tech');

INSERT INTO department (name)
VALUES ('Retention Marketing - Online');

INSERT INTO department (name)
VALUES ('Retail Services & Legal Affairs');

INSERT INTO department (name)
VALUES ('Bookmaking');

INSERT INTO department (name)
VALUES ('Payments & Fraud Services');

INSERT INTO department (name)
VALUES ('TRServ');

INSERT INTO department (name)
VALUES ('Retail');

INSERT INTO department (name)
VALUES ('Finance');

INSERT INTO department (name)
VALUES ('Gaming - Online');

INSERT INTO department (name)
VALUES ('C-Level');

INSERT INTO department (name)
VALUES ('Legal');

INSERT INTO department (name)
VALUES ('Marketing Intelligence');

INSERT INTO department (name)
VALUES ('Business Development');

INSERT INTO department (name)
VALUES ('Compliance & Regulations');

INSERT INTO department (name)
VALUES ('HR');

INSERT INTO department (name)
VALUES ('Admin');

INSERT INTO department (name)
VALUES ('PMO');

INSERT INTO department (name)
VALUES ('Graduates - Trainees');

INSERT INTO department (name)
VALUES ('Acquisition Marketing - Online');

INSERT INTO department (name)
VALUES ('Customer Services');

INSERT INTO department (name)
VALUES ('TSA North - Retail');

INSERT INTO department (name)
VALUES ('GM Gibraltar');

INSERT INTO department (name)
VALUES ('Tech - Data');

INSERT INTO department (name)
VALUES ('TSA EAST - Retail');

INSERT INTO department (name)
VALUES ('TRSys');

INSERT INTO department (name)
VALUES ('Customer & Transactions Platform');

INSERT INTO department (name)
VALUES ('Data & Business Intelligence');

INSERT INTO department (name)
VALUES ('Digital');

INSERT INTO department (name)
VALUES ('Digital Tech');

INSERT INTO department (name)
VALUES ('Infrastructure');

INSERT INTO department (name)
VALUES ('Graduate Trainees');

INSERT INTO department (name)
VALUES ('Corp. Communications');

INSERT INTO department (name)
VALUES ('Internal Audit');

INSERT INTO department (name)
VALUES ('Office Services');

INSERT INTO department (name)
VALUES ('Executives');

INSERT INTO department (name)
VALUES ('Sponsorships');

INSERT INTO department (name)
VALUES ('Sponsoring');

INSERT INTO pillar (name)
VALUES ('Business Framework');

INSERT INTO pillar (name)
VALUES ('Tech');

INSERT INTO pillar (name)
VALUES ('Online');

INSERT INTO pillar (name)
VALUES ('Retail');

INSERT INTO submission_status (name)
VALUES ('On-hold');

INSERT INTO submission_status (name)
VALUES ('Approved');

INSERT INTO submission_status (name)
VALUES ('Rejected');

INSERT INTO submission_reason (name)
VALUES ('Backfill');

INSERT INTO submission_reason (name)
VALUES ('New Approved Position');

INSERT INTO submission_reason (name)
VALUES ('Salary Change');

INSERT INTO submission_reason (name)
VALUES ('Job Title Change');

INSERT INTO submission_reason (name)
VALUES ('Reporting Line Change');

INSERT INTO submission_reason (name)
VALUES ('Working Hours Change');

INSERT INTO submission_reason (name)
VALUES ('New Unapproved Position');

INSERT INTO submission_reason (name)
VALUES ('Deactivate Position');

INSERT INTO function (name)
VALUES ('Tech - Infrastructure');

INSERT INTO function (name)
VALUES ('Retention Marketing');

INSERT INTO function (name)
VALUES ('Retail Services & Legal Affairs');

INSERT INTO function (name)
VALUES ('Bookmaking Croatia');

INSERT INTO function (name)
VALUES ('Payments & Fraud Services');

INSERT INTO function (name)
VALUES ('Retail Services');

INSERT INTO function (name)
VALUES ('Tech - Platform');

INSERT INTO function (name)
VALUES ('Tech - Digital Tech');

INSERT INTO function (name)
VALUES ('Tech - Tech Digital');

INSERT INTO function (name)
VALUES ('TSA East');

INSERT INTO function (name)
VALUES ('Tech');

INSERT INTO function (name)
VALUES ('Tech - Bookmaking');

INSERT INTO function (name)
VALUES ('Finance');

INSERT INTO function (name)
VALUES ('HR');

INSERT INTO function (name)
VALUES ('Office Services');

INSERT INTO function (name)
VALUES ('Bookmaking Colombia');

INSERT INTO function (name)
VALUES ('Gaming');

INSERT INTO function (name)
VALUES ('Marketing Intelligence');

INSERT INTO function (name)
VALUES ('Executives');

INSERT INTO function (name)
VALUES ('Bookmaking Malta');

INSERT INTO function (name)
VALUES ('Business Development');

INSERT INTO function (name)
VALUES ('Compliance & Regulations');

INSERT INTO function (name)
VALUES ('Legal');

INSERT INTO function (name)
VALUES ('PMO');

INSERT INTO function (name)
VALUES ('Acquisition Marketing');

INSERT INTO function (name)
VALUES ('Customer Services');

INSERT INTO function (name)
VALUES ('Corp. Communications');

INSERT INTO function (name)
VALUES ('TSA North');

INSERT INTO function (name)
VALUES ('GM Gibraltar');

INSERT INTO function (name)
VALUES ('Tech - Data');

INSERT INTO function (name)
VALUES ('Tech - Retail Tech');

INSERT INTO function (name)
VALUES ('Retail Systems');

INSERT INTO function (name)
VALUES ('Sponsoring');

INSERT INTO function (name)
VALUES ('GM Shop Agency South');

INSERT INTO function (name)
VALUES ('Internal Audit');

INSERT INTO exchange_rate (name, code, rate)
VALUES ('Euro', 'EUR', '1.0');

INSERT INTO exchange_rate (name, code, rate)
VALUES ('Peso', 'COP', '0.0003');

INSERT INTO exchange_rate (name, code, rate)
VALUES ('Kuna', 'HRK', '0.14');

INSERT INTO exchange_rate (name, code, rate)
VALUES ('British Pound', 'GBP', '0.88');