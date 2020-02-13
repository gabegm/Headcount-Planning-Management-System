from flask import (g, abort, session)
from gatekeeping.db import get_db
from gatekeeping.api.user import get_user_function

def get_position(id, check_submitter=True):
    position = get_db().execute(
        'SELECT p.id, ps.name as status, rs.name as recruitment_status, number, pl.name as pillar, c.name as company, d.name as department,'
        'f.id as function_id, f.name as function, title, functional_reporting_line, disciplinary_reporting_line, holder,'
        'hours, start_date, end_date, salary, social_security_contribution, fringe_benefit, performance_bonus, super_bonus, management_bonus'
        ' FROM position p'
        ' JOIN company c ON p.company_id = c.id'
        ' JOIN pillar pl ON p.pillar_id = pl.id'
        ' JOIN department d ON p.department_id = d.id'
        ' JOIN function f ON p.function_id = f.id'
        ' JOIN position_status ps ON p.status_id = ps.id'
        ' JOIN recruitment_status rs ON p.recruitment_status_id = rs.id'
        ' WHERE p.number = ? AND p.isBudget = 0',
        (id,)
    ).fetchone()

    if position is None:
        abort(404, "Position id {0} doesn't exist.".format(id))

    user_functions = get_user_function(session['user_id'])
    function_ids = [function['function_id'] for function in user_functions]

    if check_submitter and g.user['type'] != 'ADMIN' and position['function_id'] not in function_ids:
       abort(403)

    return position


def get_position_by_id(id, check_submitter=True):
    position = get_db().execute(
        'SELECT p.id, ps.name as status, rs.name as recruitment_status, number, pl.name as pillar, c.name as company, d.name as department,'
        'f.id as function_id, f.name as function, title, functional_reporting_line, disciplinary_reporting_line, holder,'
        'hours, start_date, end_date, salary, social_security_contribution, fringe_benefit, performance_bonus, super_bonus, management_bonus'
        ' FROM position p'
        ' JOIN company c ON p.company_id = c.id'
        ' JOIN pillar pl ON p.pillar_id = pl.id'
        ' JOIN department d ON p.department_id = d.id'
        ' JOIN function f ON p.function_id = f.id'
        ' JOIN position_status ps ON p.status_id = ps.id'
        ' JOIN recruitment_status rs ON p.recruitment_status_id = rs.id'
        ' WHERE p.id = ? AND p.isBudget = 0',
        (id,)
    ).fetchone()

    if position is None:
        abort(404, "Position id {0} doesn't exist.".format(id))

    user_functions = get_user_function(session['user_id'])
    function_ids = [function['function_id'] for function in user_functions]

    if check_submitter and g.user['type'] != 'ADMIN' and position['function_id'] not in function_ids:
       abort(403)

    return position


def get_positions(check_submitter=True):
    query = (
        'SELECT p.id, ps.name as status, rs.name as recruitment_status, number, pl.name as pillar, c.name as company, er.rate as rate, d.name as department,'
        'f.name as function, title, functional_reporting_line, disciplinary_reporting_line, holder,'
        'hours, start_date, end_date, salary, fringe_benefit, social_security_contribution, performance_bonus, super_bonus, management_bonus'
        ' FROM position p'
        ' JOIN company c ON p.company_id = c.id'
        ' JOIN exchange_rate er on c.exchange_rate_id = er.id'
        ' JOIN pillar pl ON p.pillar_id = pl.id'
        ' JOIN department d ON p.department_id = d.id'
        ' JOIN function f ON p.function_id = f.id'
        ' JOIN position_status ps ON p.status_id = ps.id'
        ' JOIN recruitment_status rs ON p.recruitment_status_id = rs.id'
        ' WHERE isBudget = 0'
    )

    if check_submitter and g.user['type'] != 'ADMIN':
        user_functions = get_user_function(session['user_id'])

        if user_functions:
            function_ids = [function['function_id'] for function in user_functions]

            query += ' AND f.id IN (?' + ', ?' * (len(function_ids)-1) + ')'

            return get_db().execute(query, tuple(function_ids)).fetchall()

        abort(403, 'You are not assigned to any functions')

    return get_db().execute(query).fetchall()