-- Seed initial gatekeeping cycles so submissions can be created.
-- Cycles are quarterly; adjust dates as needed per your planning calendar.
INSERT INTO gatekeeping (date, submission_deadline, notes) VALUES
    ('2026-01-31', '2026-01-17', 'Q1 2026 Gatekeeping'),
    ('2026-04-30', '2026-04-16', 'Q2 2026 Gatekeeping'),
    ('2026-07-31', '2026-07-17', 'Q3 2026 Gatekeeping'),
    ('2026-10-31', '2026-10-16', 'Q4 2026 Gatekeeping');
