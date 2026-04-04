-- Seed sample positions for development and demo purposes.
-- References use IDs from V2 seed data:
--   pillar:             1=Engineering, 2=Product, 3=Commercial, 4=Corporate
--   company:            1=Acme Group HQ, 2=Acme Technology Ltd, 3=Acme Digital Services
--   department:         1=Engineering, 2=Platform Engineering, 3=Data Engineering
--   function:           1=Eng-Backend, 2=Eng-Frontend, 3=Eng-Mobile, 5=Eng-Data
--   position_status:    1=Occupied, 2=Vacant, 3=External
--   recruitment_status: 1=Contracted, 4=Approved, 5=On-Board

INSERT INTO position (
    status_id, recruitment_status_id, number, pillar_id, company_id,
    department_id, function_id, is_budget,
    title, functional_reporting_line, disciplinary_reporting_line,
    holder, hours, start_date,
    salary, fringe_benefit, social_security_contribution,
    performance_bonus, super_bonus, management_bonus
) VALUES
    (1, 5, 'DATA001', 1, 2, 3, 5, TRUE,
     'Data Engineer', 'Head of Data', 'Head of Engineering',
     'Alice Johnson', 40, '2023-01-15',
     65000.00, 1200.00, 9750.00, 5.00, 0.00, 0.00),

    (2, 4, 'DATA002', 1, 2, 3, 5, TRUE,
     'Senior Data Engineer', 'Head of Data', 'Head of Engineering',
     NULL, 40, NULL,
     80000.00, 1200.00, 12000.00, 8.00, 0.00, 0.00),

    (1, 5, 'BE001', 1, 2, 1, 1, TRUE,
     'Backend Engineer', 'Lead Backend Engineer', 'Head of Engineering',
     'Bob Smith', 40, '2022-06-01',
     70000.00, 1200.00, 10500.00, 5.00, 0.00, 0.00),

    (1, 5, 'FE001', 1, 3, 1, 2, TRUE,
     'Frontend Engineer', 'Lead Frontend Engineer', 'Head of Engineering',
     'Carol White', 40, '2023-03-20',
     68000.00, 1200.00, 10200.00, 5.00, 0.00, 0.00),

    (2, 4, 'MOB001', 1, 2, 1, 3, FALSE,
     'Mobile Engineer', 'Lead Mobile Engineer', 'Head of Engineering',
     NULL, 40, NULL,
     72000.00, 1200.00, 10800.00, 5.00, 0.00, 0.00);
