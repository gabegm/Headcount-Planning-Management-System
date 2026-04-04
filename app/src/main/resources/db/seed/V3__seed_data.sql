-- V1: Seed data — development and demo environments only
-- Loaded via spring.flyway.locations: classpath:db/seed (dev profile + tests).
-- Never runs in production (spring.flyway.locations omits classpath:db/seed there).
--
-- Default password for all demo accounts is bcrypt('changeme').
-- Reset all credentials before any real use.

-- ── Clear existing data ───────────────────────────────────────────────────────
TRUNCATE TABLE audit, page, submission_change, submission, user_function, "user",
               position, gatekeeping,
               submission_reason, submission_status, recruitment_status, position_status,
               "function", department, pillar, company, exchange_rate
               RESTART IDENTITY CASCADE;

-- ── Reference tables ─────────────────────────────────────────────────────────

INSERT INTO exchange_rate (name, code, rate) VALUES
    ('Euro',          'EUR', 1.00),
    ('US Dollar',     'USD', 1.08),
    ('British Pound', 'GBP', 0.86),
    ('Swiss Franc',   'CHF', 0.97);

-- id: 1=Acme Group HQ, 2=Acme Technology Ltd, 3=Acme Digital Services,
--     4=Acme Data & Analytics, 5=Acme Product Solutions, 6=Acme Cloud Platforms,
--     7=Acme Americas Inc, 8=Acme UK Ltd, 9=Acme Switzerland AG, 10=Acme Shared Services
INSERT INTO company (name, exchange_rate_id) VALUES
    ('Acme Group HQ',           1),
    ('Acme Technology Ltd',     1),
    ('Acme Digital Services',   1),
    ('Acme Data & Analytics',   1),
    ('Acme Product Solutions',  1),
    ('Acme Cloud Platforms',    1),
    ('Acme Americas Inc',       2),
    ('Acme UK Ltd',             3),
    ('Acme Switzerland AG',     4),
    ('Acme Shared Services',    1);

-- id: 1=Engineering, 2=Product, 3=Commercial, 4=Corporate
INSERT INTO pillar (name) VALUES
    ('Engineering'),
    ('Product'),
    ('Commercial'),
    ('Corporate');

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

-- id: 1=Eng-Backend, 2=Eng-Frontend, 3=Eng-Mobile, 4=Eng-Platform, 5=Eng-Data,
--     6=Eng-Infrastructure, 7=Eng-Security, 8=Product, 9=Design & UX, 10=Data & Analytics,
--     11=Sales, 12=Marketing, 13=Customer Success, 14=Finance, 15=Legal, 16=Compliance,
--     17=HR, 18=Recruiting, 19=IT Operations, 20=Project Management, 21=Internal Audit,
--     22=Executive, 23=Corporate Communications, 24=Office Services
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

-- id: 1=Occupied, 2=Vacant, 3=External, 4=Leaver, 5=Pending Approval
INSERT INTO position_status (name) VALUES
    ('Occupied'),
    ('Vacant'),
    ('External'),
    ('Leaver'),
    ('Pending Approval');

-- id: 1=Contracted, 2=Deactivated, 3=External, 4=Approved, 5=On-Board,
--     6=Proposed, 7=Extended
INSERT INTO recruitment_status (name) VALUES
    ('Contracted'),
    ('Deactivated'),
    ('External'),
    ('Approved'),
    ('On-Board'),
    ('Proposed'),
    ('Extended');

-- id: 1=On-hold, 2=Approved, 3=Rejected
INSERT INTO submission_status (name) VALUES
    ('On-hold'),
    ('Approved'),
    ('Rejected');

-- id: 1=Backfill, 2=New Approved Position, 3=Salary Change, 4=Job Title Change,
--     5=Reporting Line Change, 6=Working Hours Change, 7=New Unapproved Position,
--     8=Deactivate Position
INSERT INTO submission_reason (name) VALUES
    ('Backfill'),
    ('New Approved Position'),
    ('Salary Change'),
    ('Job Title Change'),
    ('Reporting Line Change'),
    ('Working Hours Change'),
    ('New Unapproved Position'),
    ('Deactivate Position');

-- ── Users ─────────────────────────────────────────────────────────────────────
-- All demo accounts use password 'changeme' (bcrypt hash below).
-- id: 1=admin, 2=manager, 3=employee
INSERT INTO "user" (email, password, type, active) VALUES
    ('admin@example.com',
     '$2a$10$zsS9Kw/l0CvDG6yMWA2EC.tzqqhn0prcD85Uruh.exJuPg9fRlUt6',
     'ADMIN', TRUE),
    ('manager@example.com',
     '$2a$10$zsS9Kw/l0CvDG6yMWA2EC.tzqqhn0prcD85Uruh.exJuPg9fRlUt6',
     'USER', TRUE),
    ('employee@example.com',
     '$2a$10$zsS9Kw/l0CvDG6yMWA2EC.tzqqhn0prcD85Uruh.exJuPg9fRlUt6',
     'USER', TRUE);

-- Function access
-- Manager: all Engineering functions (1–7)
-- Employee: Backend (1), Frontend (2), Data (5)
INSERT INTO user_function (user_id, function_id) VALUES
    (2, 1), (2, 2), (2, 3), (2, 4), (2, 5), (2, 6), (2, 7),
    (3, 1), (3, 2), (3, 5);

-- ── Gatekeeping cycles ────────────────────────────────────────────────────────
INSERT INTO gatekeeping (date, submission_deadline, notes) VALUES
    ('2025-10-31', '2025-10-17', 'Q4 2025 Gatekeeping'),
    ('2026-01-31', '2026-01-17', 'Q1 2026 Gatekeeping'),
    ('2026-04-30', '2026-04-16', 'Q2 2026 Gatekeeping'),
    ('2026-07-31', '2026-07-17', 'Q3 2026 Gatekeeping'),
    ('2026-10-31', '2026-10-16', 'Q4 2026 Gatekeeping');

-- ── Positions ─────────────────────────────────────────────────────────────────
-- is_budget=FALSE  →  actual headcount (shown in submissions, position lists)
-- is_budget=TRUE   →  budget plan lines (compared against actual on dashboard)
--
-- The unique index is (number, is_budget), so the same number can exist as both
-- an actual position and a budget plan line.
--
-- Columns: status_id, recruitment_status_id, number, pillar_id, company_id,
--          department_id, function_id, is_budget,
--          title, functional_reporting_line, disciplinary_reporting_line,
--          holder, hours, start_date,
--          salary, fringe_benefit, social_security_contribution,
--          performance_bonus, super_bonus, management_bonus

-- ── Engineering – Backend (function_id=1) ────────────────────────────────────

-- Actual positions
INSERT INTO position (
    status_id, recruitment_status_id, number, pillar_id, company_id,
    department_id, function_id, is_budget,
    title, functional_reporting_line, disciplinary_reporting_line,
    holder, hours, start_date,
    salary, fringe_benefit, social_security_contribution,
    performance_bonus, super_bonus, management_bonus
) VALUES
    (1, 5, 'BE001', 1, 2, 1, 1, FALSE,
     'Backend Engineer', 'Lead Backend Engineer', 'Head of Engineering',
     'Bob Smith', 40, '2022-06-01',
     70000.00, 1200.00, 10500.00, 5.00, 0.00, 0.00),

    (1, 5, 'BE002', 1, 2, 1, 1, FALSE,
     'Senior Backend Engineer', 'Lead Backend Engineer', 'Head of Engineering',
     'Sarah Connor', 40, '2023-02-15',
     85000.00, 1200.00, 12750.00, 8.00, 0.00, 0.00),

    (1, 5, 'BE003', 1, 2, 1, 1, FALSE,
     'Backend Engineer', 'Lead Backend Engineer', 'Head of Engineering',
     'James Wilson', 40, '2024-01-08',
     72000.00, 1200.00, 10800.00, 5.00, 0.00, 0.00),

    (2, 4, 'BE004', 1, 2, 1, 1, FALSE,
     'Backend Engineer', 'Lead Backend Engineer', 'Head of Engineering',
     NULL, 40, '2024-09-01',
     72000.00, 1200.00, 10800.00, 5.00, 0.00, 0.00),

    (1, 5, 'BE005', 1, 2, 1, 1, FALSE,
     'Lead Backend Engineer', 'Head of Engineering', 'Head of Engineering',
     'Maria Garcia', 40, '2025-03-10',
     95000.00, 1200.00, 14250.00, 10.00, 5.00, 0.00);

-- Budget plan lines
INSERT INTO position (
    status_id, recruitment_status_id, number, pillar_id, company_id,
    department_id, function_id, is_budget,
    title, functional_reporting_line, disciplinary_reporting_line,
    holder, hours, start_date,
    salary, fringe_benefit, social_security_contribution,
    performance_bonus, super_bonus, management_bonus
) VALUES
    (2, 6, 'BE001', 1, 2, 1, 1, TRUE,
     'Backend Engineer', 'Lead Backend Engineer', 'Head of Engineering',
     NULL, 40, '2022-06-01',
     70000.00, 1200.00, 10500.00, 5.00, 0.00, 0.00),

    (2, 6, 'BE002', 1, 2, 1, 1, TRUE,
     'Senior Backend Engineer', 'Lead Backend Engineer', 'Head of Engineering',
     NULL, 40, '2023-01-01',
     85000.00, 1200.00, 12750.00, 8.00, 0.00, 0.00),

    (2, 6, 'BE003', 1, 2, 1, 1, TRUE,
     'Backend Engineer', 'Lead Backend Engineer', 'Head of Engineering',
     NULL, 40, '2023-07-01',
     72000.00, 1200.00, 10800.00, 5.00, 0.00, 0.00),

    (2, 6, 'BE004', 1, 2, 1, 1, TRUE,
     'Backend Engineer', 'Lead Backend Engineer', 'Head of Engineering',
     NULL, 40, '2024-01-01',
     72000.00, 1200.00, 10800.00, 5.00, 0.00, 0.00),

    (2, 6, 'BE005', 1, 2, 1, 1, TRUE,
     'Backend Engineer', 'Lead Backend Engineer', 'Head of Engineering',
     NULL, 40, '2024-01-01',
     72000.00, 1200.00, 10800.00, 5.00, 0.00, 0.00),

    (2, 6, 'BE006', 1, 2, 1, 1, TRUE,
     'Lead Backend Engineer', 'Head of Engineering', 'Head of Engineering',
     NULL, 40, '2025-01-01',
     95000.00, 1200.00, 14250.00, 10.00, 5.00, 0.00);

-- ── Engineering – Frontend (function_id=2) ───────────────────────────────────

INSERT INTO position (
    status_id, recruitment_status_id, number, pillar_id, company_id,
    department_id, function_id, is_budget,
    title, functional_reporting_line, disciplinary_reporting_line,
    holder, hours, start_date,
    salary, fringe_benefit, social_security_contribution,
    performance_bonus, super_bonus, management_bonus
) VALUES
    (1, 5, 'FE001', 1, 3, 1, 2, FALSE,
     'Frontend Engineer', 'Lead Frontend Engineer', 'Head of Engineering',
     'Carol White', 40, '2023-03-20',
     68000.00, 1200.00, 10200.00, 5.00, 0.00, 0.00),

    (1, 5, 'FE002', 1, 3, 1, 2, FALSE,
     'Senior Frontend Engineer', 'Lead Frontend Engineer', 'Head of Engineering',
     'Tom Brown', 40, '2023-09-01',
     82000.00, 1200.00, 12300.00, 8.00, 0.00, 0.00),

    (2, 4, 'FE003', 1, 3, 1, 2, FALSE,
     'Frontend Engineer', 'Lead Frontend Engineer', 'Head of Engineering',
     NULL, 32, '2024-06-15',
     56000.00, 1200.00, 8400.00, 5.00, 0.00, 0.00),

    (2, 6, 'FE001', 1, 3, 1, 2, TRUE,
     'Frontend Engineer', 'Lead Frontend Engineer', 'Head of Engineering',
     NULL, 40, '2023-01-01',
     68000.00, 1200.00, 10200.00, 5.00, 0.00, 0.00),

    (2, 6, 'FE002', 1, 3, 1, 2, TRUE,
     'Senior Frontend Engineer', 'Lead Frontend Engineer', 'Head of Engineering',
     NULL, 40, '2023-07-01',
     82000.00, 1200.00, 12300.00, 8.00, 0.00, 0.00),

    (2, 6, 'FE003', 1, 3, 1, 2, TRUE,
     'Frontend Engineer', 'Lead Frontend Engineer', 'Head of Engineering',
     NULL, 40, '2024-01-01',
     68000.00, 1200.00, 10200.00, 5.00, 0.00, 0.00);

-- ── Engineering – Mobile (function_id=3) ─────────────────────────────────────

INSERT INTO position (
    status_id, recruitment_status_id, number, pillar_id, company_id,
    department_id, function_id, is_budget,
    title, functional_reporting_line, disciplinary_reporting_line,
    holder, hours, start_date,
    salary, fringe_benefit, social_security_contribution,
    performance_bonus, super_bonus, management_bonus
) VALUES
    (1, 5, 'MOB001', 1, 2, 1, 3, FALSE,
     'Mobile Engineer', 'Lead Mobile Engineer', 'Head of Engineering',
     'Yang Li', 40, '2024-01-15',
     72000.00, 1200.00, 10800.00, 5.00, 0.00, 0.00),

    (2, 4, 'MOB002', 1, 2, 1, 3, FALSE,
     'Senior Mobile Engineer', 'Lead Mobile Engineer', 'Head of Engineering',
     NULL, 40, '2025-06-01',
     87000.00, 1200.00, 13050.00, 8.00, 0.00, 0.00),

    (2, 6, 'MOB001', 1, 2, 1, 3, TRUE,
     'Mobile Engineer', 'Lead Mobile Engineer', 'Head of Engineering',
     NULL, 40, '2024-01-01',
     72000.00, 1200.00, 10800.00, 5.00, 0.00, 0.00),

    (2, 6, 'MOB002', 1, 2, 1, 3, TRUE,
     'Senior Mobile Engineer', 'Lead Mobile Engineer', 'Head of Engineering',
     NULL, 40, '2025-01-01',
     87000.00, 1200.00, 13050.00, 8.00, 0.00, 0.00);

-- ── Engineering – Data (function_id=5) ───────────────────────────────────────

INSERT INTO position (
    status_id, recruitment_status_id, number, pillar_id, company_id,
    department_id, function_id, is_budget,
    title, functional_reporting_line, disciplinary_reporting_line,
    holder, hours, start_date,
    salary, fringe_benefit, social_security_contribution,
    performance_bonus, super_bonus, management_bonus
) VALUES
    (1, 5, 'DATA001', 1, 4, 3, 5, FALSE,
     'Data Engineer', 'Head of Data', 'Head of Engineering',
     'Alice Johnson', 40, '2023-01-15',
     65000.00, 1200.00, 9750.00, 5.00, 0.00, 0.00),

    (1, 5, 'DATA002', 1, 4, 3, 5, FALSE,
     'Senior Data Engineer', 'Head of Data', 'Head of Engineering',
     'Priya Sharma', 40, '2023-07-01',
     80000.00, 1200.00, 12000.00, 8.00, 0.00, 0.00),

    (2, 4, 'DATA003', 1, 4, 3, 5, FALSE,
     'Data Engineer', 'Head of Data', 'Head of Engineering',
     NULL, 40, '2024-03-10',
     65000.00, 1200.00, 9750.00, 5.00, 0.00, 0.00),

    (2, 6, 'DATA001', 1, 4, 3, 5, TRUE,
     'Data Engineer', 'Head of Data', 'Head of Engineering',
     NULL, 40, '2023-01-01',
     65000.00, 1200.00, 9750.00, 5.00, 0.00, 0.00),

    (2, 6, 'DATA002', 1, 4, 3, 5, TRUE,
     'Senior Data Engineer', 'Head of Data', 'Head of Engineering',
     NULL, 40, '2023-07-01',
     80000.00, 1200.00, 12000.00, 8.00, 0.00, 0.00),

    (2, 6, 'DATA003', 1, 4, 3, 5, TRUE,
     'Data Engineer', 'Head of Data', 'Head of Engineering',
     NULL, 40, '2024-01-01',
     65000.00, 1200.00, 9750.00, 5.00, 0.00, 0.00);

-- ── Submissions ───────────────────────────────────────────────────────────────
-- submitter_id: 1=admin, 2=manager, 3=employee
-- gatekeeping_id: 1=Q4 2025, 2=Q1 2026, 3=Q2 2026
-- status_id: 1=On-hold, 2=Approved, 3=Rejected
-- reason_id: 1=Backfill, 2=New Approved Position, 3=Salary Change, 4=Job Title Change

INSERT INTO submission (submitter_id, gatekeeping_id, position_id, status_id, reason_id,
                        rationale, effective_date, submitted) VALUES
    -- Approved backfill for vacant BE004
    (1, 2, 'BE004', 2, 1,
     'BE004 has been vacant since Q3 2024. The team is under capacity and delivery timelines are at risk.',
     '2026-02-01', '2026-01-10 09:15:00+00'),

    -- Approved new position for mobile team
    (2, 2, 'MOB002', 2, 2,
     'The mobile roadmap for 2026 requires a senior engineer to lead the new features stream.',
     '2026-03-01', '2026-01-12 11:30:00+00'),

    -- Rejected salary change for DATA001
    (1, 2, 'DATA001', 3, 3,
     'Alice Johnson has been promoted to a lead role informally. Salary adjustment to reflect responsibilities.',
     '2026-02-15', '2026-01-14 14:00:00+00'),

    -- On-hold backfill for FE003
    (2, 3, 'FE003', 1, 1,
     'FE003 remains unfilled. Request to reopen for Q2 2026 gatekeeping.',
     '2026-05-01', '2026-04-02 10:00:00+00');

-- ── Submission changes ────────────────────────────────────────────────────────
-- field and change are free-form text capturing what was proposed.

INSERT INTO submission_change (submission_id, submitted, field, change) VALUES
    -- Submission 1: Backfill for BE004
    (1, '2026-01-10 09:15:00+00', 'holder',    'David Kim'),
    (1, '2026-01-10 09:15:00+00', 'status_id', '1'),

    -- Submission 2: New position MOB002
    (2, '2026-01-12 11:30:00+00', 'title',     'Senior Mobile Engineer'),
    (2, '2026-01-12 11:30:00+00', 'salary',    '87000.00'),
    (2, '2026-01-12 11:30:00+00', 'hours',     '40'),

    -- Submission 3: Salary change DATA001
    (3, '2026-01-14 14:00:00+00', 'salary',    '78000.00'),
    (3, '2026-01-14 14:00:00+00', 'title',     'Lead Data Engineer'),

    -- Submission 4: Backfill FE003
    (4, '2026-04-02 10:00:00+00', 'holder',    'Lisa Park'),
    (4, '2026-04-02 10:00:00+00', 'status_id', '1');
