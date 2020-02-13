from gatekeeping.db import get_db

def get_budget():
    return get_db().execute(
        'SELECT p.id, ps.name as status, rs.name as recruitment_status, number, pl.name as pillar, c.name as company, er.rate as rate, d.name as department,'
        'f.name as function, title, functional_reporting_line, disciplinary_reporting_line, holder,'
        'hours, start_date, end_date, salary, fringe_benefit, social_security_contribution, performance_bonus, super_bonus, management_bonus'
        ' FROM position p'
        ' JOIN company c ON p.company_id = c.id'
        ' JOIN exchange_rate er on c.exchange_rate_id = er.id'
        ' JOIN pillar pl ON p.pillar_id = pl.id'
        ' JOIN department d ON p.pillar_id = d.id'
        ' JOIN function f ON p.function_id = f.id'
        ' JOIN position_status ps ON p.status_id = ps.id'
        ' JOIN recruitment_status rs ON p.recruitment_status_id = rs.id'
        ' WHERE isBudget = 1'
    ).fetchall()