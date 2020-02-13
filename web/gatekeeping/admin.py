import os
from datetime import datetime
from time import time

import pandas as pd
from flask import (Blueprint, abort, flash, g, jsonify, redirect,
                   render_template, request, url_for)
from gatekeeping.api.audit import create_audit_log
from gatekeeping.api.budget import get_budget
from gatekeeping.api.company import (get_companies, get_company,
                                     get_company_by_name)
from gatekeeping.api.department import (get_department, get_department_by_name,
                                        get_departments)
from gatekeeping.api.exchange_rate import (get_exchange_rate,
                                           get_exchange_rate_by_name,
                                           get_exchange_rates)
from gatekeeping.api.faq import get_faq, get_faqs
from gatekeeping.api.function import (get_function, get_function_by_name,
                                      get_functions)
from gatekeeping.api.gatekeeping import get_gatekeeping
from gatekeeping.api.pillar import get_pillar, get_pillar_by_name, get_pillars
from gatekeeping.api.position import (get_position, get_position_by_id,
                                      get_positions)
from gatekeeping.api.position_status import (get_position_status,
                                             get_position_status_by_name,
                                             get_position_statuses)
from gatekeeping.api.recruitment_status import (get_recruitment_status,
                                                get_recruitment_status_by_name,
                                                get_recruitment_statuses)
from gatekeeping.api.submission import (get_submission, get_submission_change,
                                        get_submission_changes,
                                        get_submissions)
from gatekeeping.api.submission_reason import (get_submission_reasons, get_submission_reason_by_name)
from gatekeeping.api.submission_status import (get_submission_status,
                                               get_submission_status_by_name,
                                               get_submission_statuses)
from gatekeeping.api.user import (get_user, get_user_function, get_users,
                                  has_user_function, get_active_users)
from gatekeeping.dashboard import get_line_chart
from gatekeeping.db import get_db, get_table
from gatekeeping.utils import allowed_file, is_upload_valid, send_mail
from werkzeug.utils import secure_filename

bp = Blueprint('admin', __name__, url_prefix='/admin')


@bp.route('/')
def index():
    functions = get_functions()

    data = {
        'functions': dict(functions)
    }

    return render_template('admin/index.html', data=data)


@bp.route('/user')
def user():
    """Show all users."""
    users = get_users()

    return render_template('admin/user.html', users=users)


@bp.route('/user/update/<int:id>', endpoint='user-update', methods=('GET', 'POST'))
def update_user(id):
    user = get_user(id)
    functions = get_functions()
    user_function_rows = get_user_function(id)
    user_functions = []

    for row in user_function_rows:
        user_functions.append(get_function(row['function_id'])['name'])

    data = {
        "user": dict(user),
        "functions": dict(functions),
        "userFunctions": user_functions
    }

    if request.method == 'POST':
        user_type = request.form['userType']
        active = request.form['userActive']
        user_functions = request.form.getlist('functions')
        errors = []

        if not user_type:
            errors.append('Type is required.')

        if not active:
            errors.append('Active is required.')

        if user_type != 'ADMIN' and not user_functions:
            errors.append('User functions required.')

        if errors:
            [flash([error, id], category='update') for error in errors]
            # return flashed messages
            return jsonify(status='error')
        else:
            db = get_db()
            db.execute(
                'UPDATE user SET type = ?, active = ? WHERE id = ?',
                (user_type, active, id)
            )

            if user_functions:
                if has_user_function(id):
                    db.execute(
                        'DELETE from user_function WHERE user_id = ?',
                        (id,)
                    )
                for function in user_functions:
                    db.execute(
                        'INSERT INTO user_function (user_id, function_id)'
                        ' VALUES (?,?)',
                        (id, get_function_by_name(function)['id'])
                    )

            db.commit()

            return jsonify(status='ok')

    return jsonify(dict(data))


@bp.route('/user/delete/<int:id>', endpoint='user-delete', methods=('POST',))
def delete_user(id):
    """Delete a post.
    Ensures that the post exists and that the logged in user is the
    author of the post.
    """
    db = get_db()
    db.execute('DELETE FROM user WHERE id = ?', (id,))
    db.commit()

    return jsonify(status='ok')


@bp.route('/company')
def company():
    """Show all companies."""

    return render_template('admin/company.html', companies=get_companies())


@bp.route('/company/create', endpoint='company-create', methods=('GET', 'POST'))
def create_company():
    currencies = get_exchange_rates()

    # extract columns we need and convert back to tuple
    currencies = [(currency['id'], currency['name'])
                  for currency in currencies]

    if request.method == 'POST':
        name = request.form['companyName']
        exchange_rate_id = request.form['currency']
        errors = []

        if not name:
            errors.append('Name is required.')

        if not exchange_rate_id:
            errors.append('Exchange rate required.')

        if errors:
            [flash(error, category='create') for error in errors]
            # return flashed messages
            return jsonify(status='error')
        else:
            db = get_db()
            db.execute(
                'INSERT INTO company (exchange_rate_id, name) VALUES (?, ?)',
                (exchange_rate_id, name)
            )

            db.commit()

            return jsonify(status='ok')

    return jsonify(dict(currencies))


@bp.route('/company/update/<int:id>', endpoint='company-update', methods=('GET', 'POST'))
def update_company(id):
    company = get_company(id)
    currencies = get_exchange_rates()

    # extract columns we need and convert back to tuple
    currencies = [(currency['id'], currency['name'])
                  for currency in currencies]

    data = {
        "company": dict(company),
        "currencies": dict(currencies)
    }

    if request.method == 'POST':
        name = request.form['companyName']
        exchange_rate_id = request.form['currency']
        errors = []

        if not name:
            errors.append('Name is required.')

        if not exchange_rate_id:
            errors.append('Exchange rate required.')

        if errors:
            [flash([error, id], category='update') for error in errors]
            # return flashed messages
            return jsonify(status='error')
        else:
            db = get_db()
            db.execute(
                'UPDATE company SET name = ?, exchange_rate_id = ? WHERE id = ?',
                (name, exchange_rate_id, id)
            )
            db.commit()

            return jsonify(status='ok')

    return jsonify(dict(data))


@bp.route('/company/delete/<int:id>', endpoint='company-delete', methods=('POST',))
def delete_company(id):
    """Delete a post.
    Ensures that the post exists and that the logged in user is the
    author of the post.
    """
    db = get_db()
    db.execute('DELETE FROM company WHERE id = ?', (id,))
    db.commit()

    return jsonify(status='ok')


@bp.route('/pillar')
def pillar():
    """Show all pillars."""
    db = get_db()

    pillars = db.execute(
        'SELECT *'
        ' FROM pillar'
    ).fetchall()

    return render_template('admin/pillar.html', pillars=pillars)


@bp.route('/pillar/create', endpoint='pillar-create', methods=('POST',))
def create_pillar():
    name = request.form['pillarName']
    errors = []

    if not name:
        errors.append('Name is required.')

    if errors:
        [flash(error, category='create') for error in errors]
        # return flashed messages
        return jsonify(status='error')
    else:
        db = get_db()
        db.execute(
            'INSERT INTO pillar (name) VALUES (?)',
            (name,)
        )
        db.commit()

        return jsonify(status='ok')


@bp.route('/pillar/update/<int:id>', endpoint='pillar-update', methods=('GET', 'POST'))
def update_pillar(id):
    pillar = get_pillar(id)

    if request.method == 'POST':
        name = request.form['pillarName']
        errors = []

        if not name:
            errors.append('Name is required.')

        if errors:
            [flash([error, id], category='update') for error in errors]
            # return flashed messages
            return jsonify(status='error')
        else:
            db = get_db()
            db.execute(
                'UPDATE pillar SET name = ? WHERE id = ?',
                (name, id)
            )
            db.commit()

            return jsonify(status='ok')

    return jsonify(dict(pillar))


@bp.route('/pillar/delete/<int:id>', endpoint='pillar-delete', methods=('POST',))
def delete_pillar(id):
    """Delete a post.
    Ensures that the post exists and that the logged in user is the
    author of the post.
    """
    db = get_db()
    db.execute('DELETE FROM pillar WHERE id = ?', (id,))
    db.commit()

    return jsonify(status='ok')


@bp.route('/department')
def department():
    """Show all departments."""
    db = get_db()

    departments = db.execute(
        'SELECT *'
        ' FROM department'
    ).fetchall()

    return render_template('admin/department.html', departments=departments)


@bp.route('/department/create', endpoint='department-create', methods=('POST',))
def create_department():
    name = request.form['departmentName']
    errors = []

    if not name:
        errors.append('Name is required.')

    if errors:
        [flash(error, category='create') for error in errors]
        return jsonify(status='error')
    else:
        db = get_db()
        db.execute(
            'INSERT INTO department (name) VALUES (?)',
            (name,)
        )
        db.commit()

        return jsonify(status='ok')


@bp.route('/department/update/<int:id>', endpoint='department-update', methods=('GET', 'POST'))
def update_department(id):
    department = get_department(id)

    if request.method == 'POST':
        name = request.form['departmentName']
        errors = []

        if not name:
            errors.append('Name is required.')

        if errors:
            [flash([error, id], category='update') for error in errors]
            return jsonify(status='error')
        else:
            db = get_db()
            db.execute(
                'UPDATE department SET name = ? WHERE id = ?',
                (name, id)
            )
            db.commit()

            return jsonify(status='ok')

    return jsonify(dict(department))


@bp.route('/department/delete/<int:id>', endpoint='department-delete', methods=('POST',))
def delete_department(id):
    """Delete a post.
    Ensures that the post exists and that the logged in user is the
    author of the post.
    """
    db = get_db()
    db.execute('DELETE FROM department WHERE id = ?', (id,))
    db.commit()

    return jsonify(status='ok')


@bp.route('/exchange-rate')
def exchange_rate():
    """Show all departments."""
    db = get_db()

    exchange_rates = db.execute(
        'SELECT *'
        ' FROM exchange_rate'
    ).fetchall()

    return render_template('admin/exchange_rate.html', exchange_rates=exchange_rates)


@bp.route('/exchange-rate/create', endpoint='exchange-rate-create', methods=('POST',))
def create_exchange_rate():
    name = request.form['exchangeRateName']
    code = request.form['exchangeRateCode']
    rate = request.form['exchangeRateRate']
    errors = []

    if not name:
        errors.append('Name is required.')

    if not code:
        errors.append('Code is required.')

    if not rate:
        errors.append('Rate is required.')

    if errors:
        [flash(error, category='create') for error in errors]
        return jsonify(status='error')
    else:
        db = get_db()
        db.execute(
            'INSERT INTO exchange_rate (name, code, rate) VALUES (?, ?, ?)',
            (name, code, rate)
        )
        db.commit()

        return jsonify(status='ok')


@bp.route('/exchange-rate/update/<int:id>', endpoint='exchange-rate-update', methods=('GET', 'POST'))
def update_exchange_rate(id):
    exchange_rate = get_exchange_rate(id)

    if request.method == 'POST':
        name = request.form['exchangeRateName']
        code = request.form['exchangeRateCode']
        rate = request.form['exchangeRateRate']
        errors = []

        if not name:
            errors.append('Name is required.')

        if not code:
            errors.append('Code is required.')

        if not rate:
            errors.append('Rate is required.')

        if errors:
            [flash([error, id], category='update') for error in errors]
            return jsonify(status='error')
        else:
            db = get_db()
            db.execute(
                'UPDATE exchange_rate SET name = ?, code = ?, rate = ? WHERE id = ?',
                (name, code, rate, id)
            )
            db.commit()

            return jsonify(status='ok')

    return jsonify(dict(exchange_rate))


@bp.route('/exchange-rate/delete/<int:id>', endpoint='exchange-rate-delete', methods=('POST',))
def delete_exchange_rate(id):
    """Delete a post.
    Ensures that the post exists and that the logged in user is the
    author of the post.
    """
    db = get_db()
    db.execute('DELETE FROM exchange_rate WHERE id = ?', (id,))
    db.commit()

    return jsonify(status='ok')


@bp.route('/budget', methods=('GET', 'POST'))
def budget():
    """Show all positions."""
    budget = get_budget()

    return render_template('admin/budget.html', positions=budget)


@bp.route('/budget/upload', endpoint='budget-upload', methods=('POST',))
def upload_budget():
    import_type = request.form['importType']
    delimeter = request.form['delimeter']
    date_format = request.form['dateFormat']
    day_first = True if date_format == 'dd/mm/yyy' else False
    file = request.files['file']
    df = pd.read_csv(
        file,
        sep='{}'.format(delimeter),
        keep_default_na=False,
        parse_dates=True,
        dayfirst=day_first,
        encoding='utf-8'
    ) if file and allowed_file(file.filename) else None
    df['Start Date'] = pd.to_datetime(
        df['Start Date'], errors='coerce').dt.date.astype(object)
    df['End Date'] = pd.to_datetime(
        df['End Date'], errors='coerce').dt.date.astype(object)
    errors = []

    if not import_type:
        errors.append('Name is required.')
    if not file:
        errors.append('File is required.')

    if errors:
        [flash(error, category='upload') for error in errors]
        # return flashed messages
        return jsonify(status='error')
    else:
        if is_upload_valid:
            for index, row in df.iterrows():
                db = get_db()

                # testing exception rollback
                with db:
                    db.execute(
                        'REPLACE INTO position'
                        '('
                        'status_id, recruitment_status_id, number, pillar_id, company_id, department_id,'
                        'function_id, isBudget, title, functional_reporting_line, disciplinary_reporting_line, holder,'
                        'hours, start_date, end_date, salary, social_security_contribution,'
                        'fringe_benefit, performance_bonus, super_bonus, management_bonus'
                        ')'
                        ' VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)',
                        (
                            get_position_status_by_name(row['Status'])['id'],
                            get_recruitment_status_by_name(
                                row['Recruitment Status'])['id'],
                            row['Number'], get_pillar_by_name(
                                row['Pillar'])['id'],
                            get_company_by_name(row['Company'])['id'],
                            get_department_by_name(row['Department'])['id'],
                            get_function_by_name(row['Function'])['id'],
                            1, row['Title'], row['Functional Reporting Line'],
                            row['Disciplinary Reporting Line'], row['Holder'], row['Hours'],
                            row['Start Date'], row['End Date'],
                            row['Salary'], row['Social Security Contribution'], row['Fringe Benefit'],
                            row['Performance Bonus'], row['Super Bonus'], row['Management Bonus']
                        )
                    )

            return jsonify(status='ok')


@bp.route('/position', methods=('GET', 'POST'))
def position():
    """Show all positions."""
    positions = get_positions(check_submitter=False)

    return render_template('admin/position.html', positions=positions)


@bp.route('/position/upload', endpoint='position-upload', methods=('POST',))
def upload_position():
    import_type = request.form['importType']
    delimeter = request.form['delimeter']
    date_format = request.form['dateFormat']
    day_first = True if date_format == 'dd/mm/yyy' else False
    file = request.files['file']
    df = pd.read_csv(
        file,
        sep='{}'.format(delimeter),
        keep_default_na=False,
        parse_dates=True,
        dayfirst=day_first,
        encoding='utf-8'
    ) if file and allowed_file(file.filename) else None
    df['Start Date'] = pd.to_datetime(
        df['Start Date'], errors='coerce').dt.date.astype(object)
    df['End Date'] = pd.to_datetime(
        df['End Date'], errors='coerce').dt.date.astype(object)
    errors = []

    if not import_type:
        errors.append('Name is required.')
    if not file:
        errors.append('File is required.')

    if errors:
        [flash(error, category='upload') for error in errors]
        # return flashed messages
        return jsonify(status='error')
    else:
        if is_upload_valid:
            for index, row in df.iterrows():
                db = get_db()

                # testing exception rollback
                with db:
                    db.execute(
                        'REPLACE INTO position'
                        '('
                        'status_id, recruitment_status_id, number, pillar_id, company_id, department_id,'
                        'function_id, isBudget, title, functional_reporting_line, disciplinary_reporting_line, holder,'
                        'hours, start_date, end_date, salary, social_security_contribution,'
                        'fringe_benefit, performance_bonus, super_bonus, management_bonus'
                        ')'
                        ' VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)',
                        (
                            get_position_status_by_name(row['Status'])['id'],
                            get_recruitment_status_by_name(
                                row['Recruitment Status'])['id'],
                            row['Number'], get_pillar_by_name(
                                row['Pillar'])['id'],
                            get_company_by_name(row['Company'])['id'],
                            get_department_by_name(row['Department'])['id'],
                            get_function_by_name(row['Function'])['id'],
                            0, row['Title'], row['Functional Reporting Line'],
                            row['Disciplinary Reporting Line'], row['Holder'], row['Hours'],
                            row['Start Date'], row['End Date'],
                            row['Salary'], row['Social Security Contribution'], row['Fringe Benefit'],
                            row['Performance Bonus'], row['Super Bonus'], row['Management Bonus']
                        )
                    )
            return jsonify(status='ok')


@bp.route('/position/create', endpoint='position-create', methods=('GET', 'POST'))
def create_position():
    pillars = get_pillars()
    companies = [(company['id'], company['name'])
                 for company in get_companies()]
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
        status_id = get_position_status_by_name(
            request.form['positionStatuses'])['id']
        recruitment_status_id = get_recruitment_status_by_name(
            request.form['recruitmentStatuses'])['id']
        number = request.form['number']
        pillar_id = get_pillar_by_name(request.form['pillars'])['id']
        company_id = get_company_by_name(request.form['companies'])['id']
        department_id = get_department_by_name(
            request.form['departments'])['id']
        function_id = get_function_by_name(request.form['functions'])['id']
        title = request.form['title']
        functional_reporting_line = request.form['functionalReportingLine']
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
            [flash(error, category='create') for error in errors]
            # return flashed messages
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
            create_audit_log(user_agent, user_ip, domain, action='Successful position creation',
                             table='POSITION', function='INSERT', user_id=g.user['id'])

            return jsonify(status='ok')

    return jsonify(data)


@bp.route('/position/update/<int:id>', endpoint='position-update', methods=('GET', 'POST'))
def update_position(id):
    position = get_position_by_id(id)
    pillars = get_pillars()
    companies = [(company['id'], company['name'])
                 for company in get_companies()]
    departments = get_departments()
    functions = get_functions()
    position_statuses = get_position_statuses()
    recruitment_statuses = get_recruitment_statuses()

    data = {
        "position": dict(position),
        "pillars": dict(pillars),
        "companies": dict(companies),
        "departments": dict(departments),
        "functions": dict(functions),
        "positionStatuses": dict(position_statuses),
        "recruitmentStatuses": dict(recruitment_statuses)
    }

    if request.method == 'POST':
        status_id = get_position_status_by_name(
            request.form['positionStatuses'])['id']
        recruitment_status_id = get_recruitment_status_by_name(
            request.form['recruitmentStatuses'])['id']
        number = request.form['number']
        pillar_id = get_pillar_by_name(request.form['pillars'])['id']
        company_id = get_company_by_name(request.form['companies'])['id']
        department_id = get_department_by_name(
            request.form['departments'])['id']
        function_id = get_function_by_name(request.form['functions'])['id']
        title = request.form['title']
        functional_reporting_line = request.form['functionalReportingLine']
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
            [flash([error, id], category='update') for error in errors]
            return jsonify(status='error')
        else:
            db = get_db()
            db.execute(
                'UPDATE position set status_id = ?, recruitment_status_id = ?, number = ?, pillar_id = ?, company_id = ?, department_id = ?,'
                'function_id = ?, title = ?, functional_reporting_line = ?, disciplinary_reporting_line = ?, holder = ?,'
                'hours = ?, start_date = ?, end_date = ?, salary = ?, social_security_contribution = ?,'
                'fringe_benefit = ?, performance_bonus = ?, super_bonus = ?, management_bonus = ?'
                'WHERE number = ?',
                (
                    status_id, recruitment_status_id, number, pillar_id, company_id, department_id,
                    function_id, title, functional_reporting_line, disciplinary_reporting_line, holder,
                    hours, start_date, end_date, salary, social_security_contribution, fringe_benefit, performance_bonus,
                    super_bonus, management_bonus
                )
            )

            db.commit()

            # create audit trail in db
            create_audit_log(user_agent, user_ip, domain, action='Successful position update',
                             table='POSITION', function='UPDATE', user_id=g.user['id'])

            return jsonify(status='ok')

    return jsonify(data)


@bp.route('/position/delete/<int:id>', endpoint='position-delete', methods=('POST',))
def delete_position(id):
    """Delete a post.
    Ensures that the post exists and that the logged in user is the
    author of the post.
    """
    db = get_db()
    db.execute('DELETE FROM gatekeeping WHERE id = ?', (id,))
    db.commit()

    return jsonify(status='ok')


@bp.route('/gatekeeping')
def gatekeeping():
    """Show all gatekeeping sessions."""
    gatekeepings = get_db().execute(
        'SELECT * FROM gatekeeping'
    ).fetchall()

    return render_template('admin/gatekeeping.html', gatekeepings=gatekeepings)


@bp.route('/gatekeeping/create', endpoint='gatekeeping-create', methods=('POST',))
def create_gatekeeping():
    date = request.form['gatekeepingDate']
    submission_deadline = request.form['gatekeepingSubmissionDeadline']
    notes = request.form['gatekeepingNotes']
    errors = []

    if not date:
        errors.append('Date is required.')

    if not submission_deadline:
        errors.append('Submission deadline is required.')

    if date < submission_deadline:
        errors.append('Deadline cannot be before gatekeeping session.')

    if errors:
        [flash(error, category='create') for error in errors]
        # return flashed messages
        return jsonify(status='error')
    else:
        db = get_db()
        db.execute(
            'INSERT INTO gatekeeping (date, submission_deadline, notes)'
            ' VALUES (?, ?, ?)',
            (date, submission_deadline, notes)
        )
        db.commit()

        active_users = get_active_users() 

        send_mail(
            send_from='hrgatekeeping@gaucimaistre.com',
            send_to=[user['email'] for user in active_users],
            subject='Next gatekeeping meeting',
            text='Hi,\n\n'
            f'A new gatekeeping meeting has been scheduled.\n'
            f'Date: {date}\n'
            f'Submission deadline: {submission_deadline}\n'
            f'Notes: {notes}\n\n',
            server='smtp.office365.com'
        )

        return jsonify(status='ok')


@bp.route('/gatekeeping/update/<int:id>', endpoint='gatekeeping-update', methods=('GET', 'POST'))
def update_gatekeeping(id):
    # convert to dict to assign values
    gatekeeping = dict(get_gatekeeping(id))

    gatekeeping['date'] = str(gatekeeping['date'])
    gatekeeping['submission_deadline'] = str(
        gatekeeping['submission_deadline'])

    if request.method == 'POST':
        date = request.form['gatekeepingDate']
        submission_deadline = request.form['gatekeepingSubmissionDeadline']
        notes = request.form['gatekeepingNotes']
        errors = []

        if not date:
            errors.append('Date is required.')

        if not submission_deadline:
            errors.append('Submission deadline is required.')

        if date < submission_deadline:
            errors.append('Deadline cannot be before gatekeeping session.')

        if errors:
            [flash([error, id], category='update') for error in errors]
            # return flashed messages
            return jsonify(status='error')
        else:
            db = get_db()
            db.execute(
                'UPDATE gatekeeping SET date = ?, submission_deadline = ?, notes = ? WHERE id = ?',
                (date, submission_deadline, notes, id)
            )
            db.commit()

            return jsonify(status='ok')

    return jsonify(dict(gatekeeping))


@bp.route('/gatekeeping/delete/<int:id>', endpoint='gatekeeping-delete', methods=('POST',))
def delete_gatekeeping(id):
    """Delete a post.
    Ensures that the post exists and that the logged in user is the
    author of the post.
    """
    db = get_db()
    db.execute('DELETE FROM gatekeeping WHERE id = ?', (id,))
    db.commit()

    return jsonify(status='ok')


@bp.route('/function')
def function():
    """Show all functions."""
    db = get_db()

    functions = db.execute(
        'SELECT *'
        ' FROM function'
    ).fetchall()

    return render_template('admin/function.html', functions=functions)


@bp.route('/function/create', endpoint='function-create', methods=('POST',))
def create_function():
    name = request.form['functionName']
    errors = []

    if not name:
        errors.append('Name is required.')

    if errors:
        [flash(error, category='create') for error in errors]
        return jsonify(status='error')
    else:
        db = get_db()
        db.execute(
            'INSERT INTO function (name) VALUES (?)',
            (name,)
        )
        db.commit()

        return jsonify(status='ok')


@bp.route('/function/update/<int:id>', endpoint='function-update', methods=('GET', 'POST'))
def update_function(id):
    function = get_function(id)

    if request.method == 'POST':
        name = request.form['functionName']
        errors = []

        if not name:
            errors.append('Name is required.')

        if errors:
            [flash([error, id], category='update') for error in errors]
            return jsonify(status='error')
        else:
            db = get_db()
            db.execute(
                'UPDATE function SET name = ? WHERE id = ?',
                (name, id)
            )
            db.commit()

            return jsonify(status='ok')

    return jsonify(dict(function))


@bp.route('/function/delete/<int:id>', endpoint='function-delete', methods=('POST',))
def delete_function(id):
    """Delete a post.
    Ensures that the post exists and that the logged in user is the
    author of the post.
    """
    db = get_db()
    db.execute('DELETE FROM function WHERE id = ?', (id,))
    db.commit()

    return jsonify(status='ok')


@bp.route('/submission/')
def submission():
    """Show all functions."""
    submissions = get_submissions()

    submissions = [dict(submission) for submission in submissions]

    for submission in submissions:
        submission['count'] = len(get_submission_changes(submission['id']))

    return render_template('admin/submission.html', submissions=submissions)


@bp.route('/submission/position/<string:id>', endpoint='submission-position-view', methods=('GET',))
def view_submission_position(id):
    """Show all functions."""
    position = get_position(id)

    return jsonify(dict(position))


@bp.route('/submission/update/<int:id>', endpoint='submission-update', methods=('GET', 'POST'))
def update_submission(id):
    submission = get_submission(id, check_submitter=False)
    submission_statuses = get_submission_statuses()
    reasons = get_submission_reasons()

    data = {
        "submission": dict(submission),
        "submissionStatuses": dict(submission_statuses),
        "submissionReasons": dict(reasons)
    }

    # convert date to string for displaying
    data['submission']['effective_date'] = str(submission['effective_date'])
    data['submission']['gatekeeping_date'] = str(
        submission['gatekeeping_date'])

    if request.method == 'POST':
        position_id = request.form['positionId']
        effective_date = request.form['effectiveDate']
        gatekeeping_date = request.form['gatekeepingDate']
        reason_id = get_submission_reason_by_name(request.form['reason'])['id']
        rationale = request.form['rationale']
        status = request.form['status']
        comment = request.form['comment']
        errors = []

        if not position_id:
            errors.append('Position Id is required.')

        if not effective_date:
            errors.append('Effective date is required.')

        if errors:
            [flash(error, category='update') for error in errors]

            return jsonify(status='error')
        else:
            db = get_db()
            db.execute(
                'UPDATE submission SET position_id = ?, effective_date, submission_status_id = ? WHERE id = ?',
                (position_id, effective_date, status, id)
            )
            db.commit()

            user = get_user(submission['submittor_id'])

            send_mail(
                send_from='hrgatekeeping@gaucimaistre.com',
                send_to=user['email'],
                subject='Submission reviewed',
                text='Hi,\n\n'
                f'Your submission has been reviewed.\n'
                f'Position number: {position_id}\n'
                f'Status: {status}\n'
                f'Comment: {comment}\n\n',
                server='smtp.office365.com'
            )

            return jsonify(status='ok')

    return jsonify(dict(data))


@bp.route('/submission/<int:id>', endpoint='submission-delete', methods=('POST',))
def delete_submission(id):
    """Delete a post.
    Ensures that the post exists and that the logged in user is the
    author of the post.
    """
    db = get_db()
    db.execute('DELETE FROM submission WHERE id = ?', (id,))
    db.commit()

    return jsonify(status='ok')


@bp.route('submission/<int:id>/change', endpoint='submission-change', methods=('GET',))
def change(id):
    changes = get_submission_changes(id)

    return render_template('admin/submission_change.html', changes=changes)


@bp.route('/submission/<int:submission_id>/change/update/<int:submission_change_id>', endpoint='submission-change-update', methods=('GET', 'POST'))
def update_submission_change(submission_id, submission_change_id):
    submission_change = get_submission_change(
        submission_change_id, check_submitter=False)
    submission_reasons = get_submission_reasons()
    data = {
        "submissionChange": dict(submission_change),
        "submissionReasons": dict(submission_reasons)
    }

    if request.method == 'POST':
        reason_id = request.form['reason']
        change = request.form['change']
        rationale = request.form['rationale']
        error = None

        if not reason_id:
            error = 'Reason is required.'

        if error is not None:
            flash(error)
        else:
            db = get_db()
            db.execute(
                'UPDATE submission_change SET reason_id = ?, effective_date, rationale = ? WHERE id = ?',
                (reason_id, change, rationale, submission_id)
            )
            db.commit()

            return jsonify(status='ok')

    return jsonify(dict(data))


@bp.route('/submission/<int:submission_id>/change/<int:submission_change_id>', endpoint='submission-change-delete', methods=('POST',))
def delete_submission_change(id):
    """Delete a post.
    Ensures that the post exists and that the logged in user is the
    author of the post.
    """
    db = get_db()
    db.execute('DELETE FROM submission_change WHERE id = ?', (id,))
    db.commit()

    return jsonify(status='ok')


@bp.route('/submission/status')
def submission_status():
    """Show all functions."""
    db = get_db()

    submission_statuses = db.execute(
        'SELECT *'
        ' FROM submission_status'
    ).fetchall()

    return render_template('admin/submission_status.html', submission_statuses=submission_statuses)


@bp.route('/submission/status/create', endpoint='submission-status-create', methods=('POST',))
def create_submission_status():
    name = request.form['submissionStatusName']
    errors = []

    if not name:
        errors.append('Name is required.')

    if errors:
        [flash(error, category='create') for error in errors]

        return jsonify(status='error')
    else:
        db = get_db()
        db.execute(
            'INSERT INTO submission_status (name) VALUES (?)',
            (name,)
        )
        db.commit()

        return jsonify(status='ok')


@bp.route('/submission/status/update/<int:id>', endpoint='submission-status-update', methods=('GET', 'POST'))
def update_submission_status(id):
    submission_status = get_submission_status(id)

    if request.method == 'POST':
        name = request.form['submissionStatusName']
        errors = []

        if not name:
            errors.append('Name is required.')

        if errors:
            [flash(error, category='create') for error in errors]

            return jsonify(status='error')
        else:
            db = get_db()
            db.execute(
                'UPDATE function SET name = ? WHERE id = ?',
                (name, id)
            )
            db.commit()

            return jsonify(status='ok')

    return jsonify(dict(submission_status))


@bp.route('/submission/status/<int:id>', endpoint='submission-status-delete', methods=('POST',))
def delete_submission_status(id):
    """Delete a post.
    Ensures that the post exists and that the logged in user is the
    author of the post.
    """
    db = get_db()
    db.execute('DELETE FROM submission_status WHERE id = ?', (id,))
    db.commit()

    return jsonify(status='ok')


@bp.route('/position/status')
def position_status():
    """Show all functions."""
    db = get_db()

    position_statuses = db.execute(
        'SELECT *'
        ' FROM position_status'
    ).fetchall()

    return render_template('admin/position_status.html', position_statuses=position_statuses)


@bp.route('/position/status/create', endpoint='position-status-create', methods=('POST',))
def create_position_status():
    name = request.form['name']
    errors = []

    if not name:
        errors.append('Name is required.')

    if errors:
        [flash(error, category='create') for error in errors]
        return jsonify(status='error')
    else:
        db = get_db()
        db.execute(
            'INSERT INTO position_status (name) VALUES (?)',
            (name,)
        )
        db.commit()

        return jsonify(status='ok')


@bp.route('/position/status/update/<int:id>', endpoint='position-status-update', methods=('GET', 'POST'))
def update_position_status(id):
    position_status = get_position_status(id)

    if request.method == 'POST':
        name = request.form['name']
        errors = []

        if not name:
            errors.append('Name is required.')

        if errors:
            [flash([error, id], category='update') for error in errors]
            return jsonify(status='error')
        else:
            db = get_db()
            db.execute(
                'UPDATE function SET name = ? WHERE id = ?',
                (name, id)
            )
            db.commit()

            return jsonify(status='ok')

    return jsonify(dict(position_status))


@bp.route('/position/status/<int:id>', endpoint='position-status-delete', methods=('POST',))
def delete_position_status(id):
    """Delete a post.
    Ensures that the post exists and that the logged in user is the
    author of the post.
    """
    db = get_db()
    db.execute('DELETE FROM position_status WHERE id = ?', (id,))
    db.commit()

    return jsonify(status='ok')


@bp.route('/recruitment/status')
def recruitment_status():
    """Show all functions."""
    recruitment_statuses = get_db().execute(
        'SELECT * FROM recruitment_status'
    ).fetchall()

    return render_template('admin/recruitment_status.html', recruitment_statuses=recruitment_statuses)


@bp.route('/recruitment/status/create', endpoint='recruitment-status-create', methods=('POST',))
def create_recruitment_status():
    name = request.form['recruitmentStatusName']
    error = None

    if not name:
        error = 'Name is required.'

    if error is not None:
        flash(error)
    else:
        db = get_db()
        db.execute(
            'INSERT INTO recruitment_status (name) VALUES (?)',
            (name,)
        )
        db.commit()

        return jsonify(status='ok')


@bp.route('/recruitment/status/update/<int:id>', endpoint='recruitment-status-update', methods=('GET', 'POST'))
def update_recruitment_status(id):
    recruitment_status = get_recruitment_status(id)

    if request.method == 'POST':
        name = request.form['recruitmentStatusName']
        error = None

        if not name:
            error = 'Name is required.'

        if error is not None:
            flash(error)
        else:
            db = get_db()
            db.execute(
                'UPDATE function SET name = ? WHERE id = ?',
                (name, id)
            )
            db.commit()

            return jsonify(status='ok')

    return jsonify(dict(recruitment_status))


@bp.route('/recruitment/status/<int:id>', endpoint='recruitment-status-delete', methods=('POST',))
def delete_recruitment_status(id):
    """Delete a post.
    Ensures that the post exists and that the logged in user is the
    author of the post.
    """
    db = get_db()
    db.execute('DELETE FROM recruitment_status WHERE id = ?', (id,))
    db.commit()

    return jsonify(status='ok')


@bp.route('/audit')
def audit():
    """Show all functions."""
    audits = get_db().execute(
        'SELECT * FROM audit'
    ).fetchall()

    return render_template('admin/audit.html', audits=audits)


@bp.route('/help')
def help():
    """Show help page content."""
    name = 'help'
    help = get_db().execute(
        'SELECT body FROM page WHERE name = ?',
        (name,)
    ).fetchone()

    return render_template('admin/help.html', page=help)


@bp.route('/help/update', endpoint='help-update', methods=('POST',))
def update_help():
    name = title = 'help'
    body = request.form['editordata']

    if body:
        db = get_db()

        db.execute(
            'REPLACE INTO page (name, title, body, edited)'
            ' VALUES (?, ?, ?, ?)',
            (name, title, body, datetime.fromtimestamp(
                time()).strftime('%Y-%m-%d %H:%M:%S'))
        )

        db.commit()

        return jsonify(status='ok')


@bp.route('/faq')
def faq():
    """Show help page content."""
    name = 'faq'
    faq = get_db().execute(
        'SELECT * FROM page WHERE name = ?',
        (name,)
    ).fetchall()

    return render_template('admin/faq.html', page=faq)


@bp.route('/faq/create', endpoint='faq-create', methods=('POST',))
def create_faq():
    name = 'faq'
    title = request.form['question']
    body = request.form['editordata']
    errors = []

    if not title:
        errors.append('Question is required.')

    if not body:
        errors.append('Answer is required.')

    if errors:
        [flash([error, id], category='update') for error in errors]
        return jsonify(status='error')
    else:
        db = get_db()
        db.execute(
            'INSERT INTO page (name, title, body, edited)'
            ' VALUES (?, ?, ?, ?)',
            (name, title, body, datetime.fromtimestamp(
                time()).strftime('%Y-%m-%d %H:%M:%S'))
        )
        db.commit()

        return jsonify(status='ok')


@bp.route('/faq/update/<int:id>', endpoint='faq-update', methods=('GET', 'POST'))
def update_faq(id):
    faq = get_faq(id)

    if request.method == 'POST':
        title = request.form['question']
        body = request.form['editordata']

        if body:
            db = get_db()
            db.execute(
                'UPDATE page SET title = ?, body = ?, edited = ?'
                ' WHERE id = ?',
                (title, body, datetime.fromtimestamp(
                    time()).strftime('%Y-%m-%d %H:%M:%S'), id)
            )

            db.commit()

            return jsonify(status='ok')

    return jsonify(dict(faq))


@bp.route('/faq/delete/<int:id>', endpoint='faq-delete', methods=('POST',))
def delete_faq(id):
    """Delete a post.
    Ensures that the post exists and that the logged in user is the
    author of the post.
    """
    db = get_db()
    db.execute('DELETE FROM page WHERE id = ?', (id,))
    db.commit()

    return jsonify(status='ok')
