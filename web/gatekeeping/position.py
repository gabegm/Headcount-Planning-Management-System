import os

import pandas as pd

from flask import (Blueprint, abort, flash, g, session, jsonify, redirect,
                   render_template, request, url_for)
from gatekeeping.api.company import get_companies, get_company, get_company_by_name
from gatekeeping.db import get_db
from gatekeeping.api.department import (get_department, get_department_by_name,
                                    get_departments)
from gatekeeping.api.function import (get_function, get_function_by_name,
                                  get_functions)
from gatekeeping.api.pillar import get_pillar, get_pillar_by_name, get_pillars
from gatekeeping.api.position_status import (get_position_status,
                                         get_position_status_by_name,
                                         get_position_statuses)
from gatekeeping.api.recruitment_status import (get_recruitment_status,
                                            get_recruitment_status_by_name,
                                            get_recruitment_statuses)
from gatekeeping.api.submission_status import (get_submission_status,
                                           get_submission_status_by_name)
from gatekeeping.api.user import (get_user_function)
from gatekeeping.api.audit import create_audit_log
from gatekeeping.api.position import (get_positions, get_position)


bp = Blueprint('position', __name__, url_prefix='/position')


@bp.route('/')
def index():
    """Show all the positions, most recent first."""
    positions = get_positions()

    return render_template('position/index.html', positions=positions)


@bp.route('/position/create', methods=('GET', 'POST'))
def create():
    pillars = get_pillars()
    companies = [(company['id'], company['name']) for company in get_companies()]
    departments = get_departments()
    functions = get_functions()
    position_statuses = get_position_statuses()
    recruitment_statuses = get_recruitment_statuses()

    data = {
        "pillars": dict(pillars),
        "companies": dict(companies),
        "departments": dict(departments),
        "functions": dict(functions),
        "positionStatuses": dict(position_statuses),
        "recruitmentStatuses": dict(recruitment_statuses)
    }

    if request.method == 'POST':
        # grabbing default position status
        status_id = get_position_status_by_name(request.form['positionStatuses'])['id']
        recruitment_status_id = get_recruitment_status_by_name(request.form['recruitmentStatuses'])['id']
        number = request.form['number']
        pillar_id = get_pillar_by_name(request.form['pillars'])['id']
        company_id = get_company_by_name(request.form['companies'])['id']
        department_id = get_department_by_name(request.form['departments'])['id']
        function_id = get_function_by_name(request.form['functions'])['id']
        title = request.form['title']
        functional_reporting_line = request.form['functinalReportingLine']
        disciplinary_reporting_line = request.form['disciplinaryReportingLine']
        holder = request.form['holder']
        hours = request.form['hours']
        start_date = request.form['startDate']
        end_date = request.form['endDate']
        salary = request.form['salary']
        social_security_contribution = request.form['socialSecurityContribution']
        fringe_benefit = request.form['fringeBenefit']
        performance_bonus = request.form['performanceBonus']
        super_bonus = request.form['superBonus']
        management_bonus = request.form['managementBonus']
        user_ip = request.environ.get('HTTP_X_REAL_IP', request.remote_addr)
        user_agent = request.headers.get('User-Agent')
        domain = request.headers['Host']
        errors = []

        if not status_id:
            errors.append('Status is required.')

        if not recruitment_status_id:
            errors.append('Recruitment Status required.')

        if not number:
            errors.append('Number is required.')

        if not pillar_id:
            errors.append('Pillar is required.')

        if not department_id:
            errors.append('Department Status required.')

        if not function_id:
            errors.append('Function is required.')

        if not title:
            errors.append('Title is required.')

        if not functional_reporting_line:
            errors.append('Functional  is required.')

        if not disciplinary_reporting_line:
            errors.append('Disciplinary Reporting Line is required.')

        if errors:
            [flash(error) for error in errors]
            return jsonify(status='error')
        else:
            db = get_db()
            db.execute(
                'INSERT INTO position'
                '('
                'status_id, recruitment_status_id, number, pillar_id, company_id, department_id,'
                'function_id, isBudget, title, functional_reporting_line, disciplinary_reporting_line, holder,'
                'hours, start_date, end_date, salary, social_security_contribution,'
                'fringe_benefit, performance_bonus, super_bonus, management_bonus'
                ')'
                ' VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)',
                (
                    status_id, recruitment_status_id, number, pillar_id, company_id, department_id,
                    function_id, 0, title, functional_reporting_line, disciplinary_reporting_line, holder,
                    hours, start_date, end_date, salary, social_security_contribution, fringe_benefit, performance_bonus,
                    super_bonus, management_bonus
                )
            )

            db.commit()

            # create audit trail in db
            create_audit_log(user_agent, user_ip, domain, action='Successful position creation', table='POSITION', function='INSERT', user_id=g.user['id'])

            return jsonify(status='ok')

    return jsonify(data)


@bp.route('/view/<string:id>', methods=('GET',))
def view(id):
    position = get_position(id, check_submitter=True)
    position = dict(position)
    position['start_date'] = position['start_date'].strftime('%d/%m/%Y')
    position['end_date'] = position['end_date'].strftime('%d/%m/%Y')

    return jsonify(position)
