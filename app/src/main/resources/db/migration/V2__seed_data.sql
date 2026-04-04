-- V2: Seed data — generic tech-company demo data
-- The default admin password is a bcrypt hash of 'changeme' — reset on first deploy.

-- Clear any pre-existing reference data and reset identity sequences
TRUNCATE TABLE user_function, "user", company, exchange_rate, pillar, department, "function",
               position_status, recruitment_status, submission_status, submission_reason,
               gatekeeping, position
               RESTART IDENTITY CASCADE;

-- Exchange rates (inserted first — referenced by company)
INSERT INTO exchange_rate (name, code, rate) VALUES
    ('Euro',           'EUR', 1.0),
    ('US Dollar',      'USD', 1.08),
    ('British Pound',  'GBP', 0.86),
    ('Swiss Franc',    'CHF', 0.97);

-- Companies (subsidiaries of a fictional tech group)
INSERT INTO company (name, exchange_rate_id) VALUES
    ('Acme Group HQ',              1),
    ('Acme Technology Ltd',        1),
    ('Acme Digital Services',      1),
    ('Acme Data & Analytics',      1),
    ('Acme Product Solutions',     1),
    ('Acme Cloud Platforms',       1),
    ('Acme Americas Inc',          2),
    ('Acme UK Ltd',                3),
    ('Acme Switzerland AG',        4),
    ('Acme Shared Services',       1);

-- Pillars (strategic groupings)
INSERT INTO pillar (name) VALUES
    ('Engineering'),
    ('Product'),
    ('Commercial'),
    ('Corporate');

-- Departments
INSERT INTO department (name) VALUES
    ('Engineering'),
    ('Platform Engineering'),
    ('Data Engineering'),
    ('Infrastructure & DevOps'),
    ('Security'),
    ('Product Management'),
    ('Product Design & UX'),
    ('Data & Analytics'),
    ('Sales'),
    ('Marketing'),
    ('Customer Success'),
    ('Finance'),
    ('Legal & Compliance'),
    ('Human Resources'),
    ('People Operations'),
    ('Recruiting'),
    ('IT Operations'),
    ('Project Management Office'),
    ('Internal Audit'),
    ('Executive'),
    ('Corporate Communications'),
    ('Office Services'),
    ('Graduate Programme');

-- Functions (cross-cutting capability areas)
INSERT INTO "function" (name) VALUES
    ('Engineering - Backend'),
    ('Engineering - Frontend'),
    ('Engineering - Mobile'),
    ('Engineering - Platform'),
    ('Engineering - Data'),
    ('Engineering - Infrastructure'),
    ('Engineering - Security'),
    ('Product'),
    ('Design & UX'),
    ('Data & Analytics'),
    ('Sales'),
    ('Marketing'),
    ('Customer Success'),
    ('Finance'),
    ('Legal'),
    ('Compliance'),
    ('Human Resources'),
    ('Recruiting'),
    ('IT Operations'),
    ('Project Management'),
    ('Internal Audit'),
    ('Executive'),
    ('Corporate Communications'),
    ('Office Services');

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

-- Default admin user (password = bcrypt('changeme') — reset on first deploy)
INSERT INTO "user" (email, password, type, active) VALUES
    ('admin@example.com',
     '$2a$10$zsS9Kw/l0CvDG6yMWA2EC.tzqqhn0prcD85Uruh.exJuPg9fRlUt6',
     'ADMIN',
     TRUE);

-- Gatekeeping cycles (quarterly — adjust per your planning calendar)
INSERT INTO gatekeeping (date, submission_deadline, notes) VALUES
    ('2026-01-31', '2026-01-17', 'Q1 2026 Gatekeeping'),
    ('2026-04-30', '2026-04-16', 'Q2 2026 Gatekeeping'),
    ('2026-07-31', '2026-07-17', 'Q3 2026 Gatekeeping'),
    ('2026-10-31', '2026-10-16', 'Q4 2026 Gatekeeping');

-- Sample positions for development and demo purposes.
-- is_budget=FALSE: these are real job positions (submissions reference them).
-- is_budget=TRUE positions represent headcount budget plan lines (separate concept).
INSERT INTO position (
    status_id, recruitment_status_id, number, pillar_id, company_id,
    department_id, function_id, is_budget,
    title, functional_reporting_line, disciplinary_reporting_line,
    holder, hours, start_date,
    salary, fringe_benefit, social_security_contribution,
    performance_bonus, super_bonus, management_bonus
) VALUES
    (1, 5, 'DATA001', 1, 2, 3, 5, FALSE,
     'Data Engineer', 'Head of Data', 'Head of Engineering',
     'Alice Johnson', 40, '2023-01-15',
     65000.00, 1200.00, 9750.00, 5.00, 0.00, 0.00),

    (2, 4, 'DATA002', 1, 2, 3, 5, FALSE,
     'Senior Data Engineer', 'Head of Data', 'Head of Engineering',
     NULL, 40, NULL,
     80000.00, 1200.00, 12000.00, 8.00, 0.00, 0.00),

    (1, 5, 'BE001', 1, 2, 1, 1, FALSE,
     'Backend Engineer', 'Lead Backend Engineer', 'Head of Engineering',
     'Bob Smith', 40, '2022-06-01',
     70000.00, 1200.00, 10500.00, 5.00, 0.00, 0.00),

    (1, 5, 'FE001', 1, 3, 1, 2, FALSE,
     'Frontend Engineer', 'Lead Frontend Engineer', 'Head of Engineering',
     'Carol White', 40, '2023-03-20',
     68000.00, 1200.00, 10200.00, 5.00, 0.00, 0.00),

    (2, 4, 'MOB001', 1, 2, 1, 3, FALSE,
     'Mobile Engineer', 'Lead Mobile Engineer', 'Head of Engineering',
     NULL, 40, NULL,
     72000.00, 1200.00, 10800.00, 5.00, 0.00, 0.00);
