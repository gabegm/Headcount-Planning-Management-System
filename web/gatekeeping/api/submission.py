from datetime import datetime
from flask import (abort, g)
from gatekeeping.db import get_db

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