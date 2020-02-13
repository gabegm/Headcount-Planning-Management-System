import os
from datetime import datetime
import pandas as pd
from flask import (Blueprint, render_template, request, redirect, flash, url_for, g, jsonify)
from werkzeug.exceptions import abort
from gatekeeping.db import (get_db,
                            get_last_insert_rowid)
from gatekeeping.position import get_position
from gatekeeping.api.submission_reason import (get_submission_reasons, get_submission_reason_by_name)
from gatekeeping.api.gatekeeping import get_next_gatekeeping
from gatekeeping.utils import create_audit_log

bp = Blueprint('submission', __name__, url_prefix='/submission')


@bp.route('/')
def index():
    submissions = get_user_submissions()

    submissions = [dict(submission) for submission in submissions]

    for submission in submissions:
        submission['count'] = len(get_submission_changes(submission['id']))

    return render_template('submission/index.html', submissions=submissions)


@bp.route('/create', methods=('GET', 'POST'))
def create():
    submission_reasons = get_submission_reasons()

    if request.method == 'POST':
        position_id = request.form['positionId']
        reason_id = get_submission_reason_by_name(request.form['reason'])['id']

        changes = {
            'title': request.form['positionTitleField'],
            'functional_reporting_line': request.form['functionalReportingLineField'],
            'disciplinary_reporting_line': request.form['disciplinaryReportingLineField'],
            'salary': request.form['annualSalaryField'],
            'hours': request.form['positionHoursField'],
            'start_date': request.form['startDateField'],
            'performance_bonus': request.form['performanceBonusField'],
            'super_bonus': request.form['superBonusField'],
            'management_bonus': request.form['managementBonusField']
        }

        rationale = request.form['rationale']
        effective_date = request.form['effectiveDate']
        user_ip = request.environ.get('HTTP_X_REAL_IP', request.remote_addr)
        user_agent = request.headers.get('User-Agent')
        domain = request.headers['Host']
        errors = []

        db = get_db()
        next_gatekeeping = get_next_gatekeeping()

        if not position_id:
            errors.append('Position Id is required.')

        if not reason_id:
            errors.append('Reason is required.')

        if db.execute(
            'SELECT id'
            ' FROM position'
            ' WHERE number = ?',
            (position_id,)
        ).fetchone() is None:
            errors.append('Position {} does not exist.'.format(position_id))

        if datetime.now().date() > next_gatekeeping['submission_deadline']:
            errors.append('Submission deadline exceeded.')

        if errors:
            [flash(error) for error in errors]
            return render_template('submission/create.html', submission_reasons=submission_reasons)
        else:
            db.execute(
                'INSERT INTO submission (position_id, reason_id, rationale, effective_date, submitter_id, gatekeeping_id)'
                ' VALUES (?, ?, ?, ?, ?, ?)',
                (position_id, reason_id, rationale, effective_date,
                 g.user['id'], next_gatekeeping['id'])
            )

            db.commit()

            for key, value in changes.items():
                if value:
                    db.execute(
                        'INSERT INTO submission_change (submission_id, field, change)'
                        ' VALUES (?, ?, ?)',
                        (get_last_insert_rowid(
                            'submission')['seq'], key, value)
                    )

            db.commit()

            # create audit trail in db
            create_audit_log(user_agent, user_ip, domain,
                             action='Successful submission', table='SUBMISSION', function='INSERT')

            return redirect(url_for('submission.index'))

    return render_template('submission/create.html', submission_reasons=submission_reasons)


@bp.route('/<int:id>/change', methods=('GET',))
def change(id):
    changes = get_submission_changes(id)

    return render_template('submission/change.html', changes=changes)


def get_submission(id, check_submitter=True):
    submission = get_db().execute(
        'SELECT s.id, s.submitted, s.position_id, s.effective_date, u.email, gk.date as gatekeeping_date, sr.name as reason, s.rationale, st.name as status'
        ' FROM submission s'
        ' JOIN user u ON s.submitter_id = u.id'
        ' JOIN gatekeeping gk ON s.gatekeeping_id = gk.id'
        ' JOIN submission_reason sr ON s.reason_id = sr.id'
        ' JOIN submission_status st ON s.status_id = st.id'
        ' WHERE s.id = ?',
        (id,)
    ).fetchone()

    if submission is None:
        abort(404, "Submission id {0} doesn't exist.".format(id))

    if check_submitter and submission['submitter_id'] != g.user['id']:
        abort(403)

    return submission


def get_user_submissions():
    submissions = get_db().execute(
        'SELECT s.id, s.submitted, s.position_id, sr.name as reason, s.rationale, s.effective_date, s.submitter_id, u.id, gk.date as gatekeeping_date, st.name as status'
        ' FROM submission s'
        ' JOIN user u ON s.submitter_id = u.id'
        ' JOIN gatekeeping gk ON s.gatekeeping_id = gk.id'
        ' JOIN submission_status st ON s.status_id = st.id'
        ' JOIN submission_reason sr ON s.reason_id = sr.id'
        ' WHERE s.submitter_id = ?',
        (g.user['id'],)
    ).fetchall()

    return submissions


def get_submissions():
    return get_db().execute(
        'SELECT s.id, s.submitted, s.position_id, s.effective_date, u.email, s.submitter_id, u.id, gk.date as gatekeeping_date, st.name as status, sr.name as reason, s.rationale'
        ' FROM submission s'
        ' JOIN user u ON s.submitter_id = u.id'
        ' JOIN gatekeeping gk ON s.gatekeeping_id = gk.id'
        ' JOIN submission_status st ON s.status_id = st.id'
        ' JOIN submission_reason sr ON s.reason_id = sr.id'
    ).fetchall()


def get_submission_changes(submission_id):
    return get_db().execute(
        'SELECT *'
        ' FROM submission_change'
        ' WHERE submission_id = ?',
        (submission_id,)
    ).fetchall()


def get_submission_change(id, check_submitter=True):
    submission_change = get_db().execute(
        'SELECT sc.id, sc.submitted, sc.field, sc.change, s.submitter_id'
        ' FROM submission_change sc'
        ' JOIN submission s on sc.submission_id = s.id'
        ' JOIN user u ON s.submitter_id = u.id'
        ' WHERE sc.id = ?',
        (id,)
    ).fetchone()

    if submission_change is None:
        abort(404, "Submission id {0} doesn't exist.".format(id))

    if check_submitter and submission_change['submitter_id'] != g.user['id']:
        abort(403)

    return submission_change

def get_submissions_effective_date_today():
    return get_db().execute(
        'SELECT s.id, s.submitted, s.position_id, s.effective_date, u.email, s.submitter_id, u.id, gk.date as gatekeeping_date, st.name as status, sr.name as reason, s.rationale'
        ' FROM submission s'
        ' JOIN user u ON s.submitter_id = u.id'
        ' JOIN gatekeeping gk ON s.gatekeeping_id = gk.id'
        ' JOIN submission_status st ON s.status_id = st.id'
        ' JOIN submission_reason sr ON s.reason_id = sr.id'
        ' WHERE gatekeeping_date = (?)',
        (str(datetime.now().date()),)
    ).fetchall()