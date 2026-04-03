-- V2: Seed data
-- Ported from schema.sql INSERT statements.
-- Note: the default admin password hash is bcrypt-based and must be re-hashed
-- on first deploy using Spring Security's BCryptPasswordEncoder.
-- The placeholder below is a bcrypt hash of 'changeme' — force a password
-- reset on first login.

-- Exchange rates (inserted first — referenced by company)
INSERT INTO exchange_rate (name, code, rate) VALUES
    ('Euro',          'EUR', 1.0),
    ('Peso',          'COP', 0.0003),
    ('Kuna',          'HRK', 0.14),
    ('British Pound', 'GBP', 0.88);

-- Companies
INSERT INTO company (name, exchange_rate_id) VALUES
    ('GM Malta',                   1),
    ('GM Casino',                  1),
    ('GM Technology Services',     1),
    ('GM Retail Services',         1),
    ('GM Retail Systems',          1),
    ('GM Shop Agency North',       1),
    ('GM Shop Agency East',        1),
    ('GM Group',                   1),
    ('Sports Services South America', 2),
    ('Sport Bet',                  3),
    ('GM Services',                4),
    ('Maritimo D',                 1);

-- Pillars
INSERT INTO pillar (name) VALUES
    ('Business Framework'),
    ('Tech'),
    ('Online'),
    ('Retail');

-- Departments
INSERT INTO department (name) VALUES
    ('Tech'),
    ('Retention Marketing - Online'),
    ('Retail Services & Legal Affairs'),
    ('Bookmaking'),
    ('Payments & Fraud Services'),
    ('TRServ'),
    ('Retail'),
    ('Finance'),
    ('Gaming - Online'),
    ('C-Level'),
    ('Legal'),
    ('Marketing Intelligence'),
    ('Business Development'),
    ('Compliance & Regulations'),
    ('HR'),
    ('Admin'),
    ('PMO'),
    ('Graduates - Trainees'),
    ('Acquisition Marketing - Online'),
    ('Customer Services'),
    ('TSA North - Retail'),
    ('GM Gibraltar'),
    ('Tech - Data'),
    ('TSA EAST - Retail'),
    ('TRSys'),
    ('Customer & Transactions Platform'),
    ('Data & Business Intelligence'),
    ('Digital'),
    ('Digital Tech'),
    ('Infrastructure'),
    ('Graduate Trainees'),
    ('Corp. Communications'),
    ('Internal Audit'),
    ('Office Services'),
    ('Executives'),
    ('Sponsorships'),
    ('Sponsoring');

-- Functions
INSERT INTO "function" (name) VALUES
    ('Tech - Infrastructure'),
    ('Retention Marketing'),
    ('Retail Services & Legal Affairs'),
    ('Bookmaking Croatia'),
    ('Payments & Fraud Services'),
    ('Retail Services'),
    ('Tech - Platform'),
    ('Tech - Digital Tech'),
    ('Tech - Tech Digital'),
    ('TSA East'),
    ('Tech'),
    ('Tech - Bookmaking'),
    ('Finance'),
    ('HR'),
    ('Office Services'),
    ('Bookmaking Colombia'),
    ('Gaming'),
    ('Marketing Intelligence'),
    ('Executives'),
    ('Bookmaking Malta'),
    ('Business Development'),
    ('Compliance & Regulations'),
    ('Legal'),
    ('PMO'),
    ('Acquisition Marketing'),
    ('Customer Services'),
    ('Corp. Communications'),
    ('TSA North'),
    ('GM Gibraltar'),
    ('Tech - Data'),
    ('Tech - Retail Tech'),
    ('Retail Systems'),
    ('Sponsoring'),
    ('GM Shop Agency South'),
    ('Internal Audit');

-- Position statuses
INSERT INTO position_status (name) VALUES
    ('Occupied'),
    ('Vacant'),
    ('External'),
    ('Leaver'),
    ('Pending Approval');

-- Recruitment statuses
INSERT INTO recruitment_status (name) VALUES
    ('Contracted'),
    ('Deactivated'),
    ('External'),
    ('Approved'),
    ('On-Board'),
    ('Proposed'),
    ('Extended');

-- Submission statuses
INSERT INTO submission_status (name) VALUES
    ('On-hold'),
    ('Approved'),
    ('Rejected');

-- Submission reasons
INSERT INTO submission_reason (name) VALUES
    ('Backfill'),
    ('New Approved Position'),
    ('Salary Change'),
    ('Job Title Change'),
    ('Reporting Line Change'),
    ('Working Hours Change'),
    ('New Unapproved Position'),
    ('Deactivate Position');

-- Default admin user
-- Password is a bcrypt hash of 'changeme' — must be reset on first deploy.
INSERT INTO "user" (email, password, type, active) VALUES
    ('gabriel.gaucimaistre@gaucimaistre.com',
     '$2a$10$zsS9Kw/l0CvDG6yMWA2EC.tzqqhn0prcD85Uruh.exJuPg9fRlUt6',
     'ADMIN',
     TRUE);
