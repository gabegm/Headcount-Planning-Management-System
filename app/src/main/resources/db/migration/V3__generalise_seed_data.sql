-- V3: Replace company-specific seed data with generic tech-company demo data.
-- Clears all lookup/reference tables and re-seeds with neutral values.
-- Safe to apply to an existing V2 database.

-- Clear tables and reset identity sequences; CASCADE handles FK-dependent tables
TRUNCATE TABLE user_function, "user", company, exchange_rate, pillar, department, "function" RESTART IDENTITY CASCADE;
-- Leave status/reason tables — their values are already generic

-- Exchange rates
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

-- Default admin user (password = bcrypt('changeme') — reset on first deploy)
INSERT INTO "user" (email, password, type, active) VALUES
    ('admin@example.com',
     '$2a$10$zsS9Kw/l0CvDG6yMWA2EC.tzqqhn0prcD85Uruh.exJuPg9fRlUt6',
     'ADMIN',
     TRUE);
