from gatekeeping.db import get_db
from flask import abort

def get_submission_reasons():
    submission_reasons = get_db().execute(
        'SELECT *'
        ' FROM submission_reason'
    ).fetchall()

    return submission_reasons

def get_submission_reason(id):
    submission_status = get_db().execute(
        'SELECT *'
        ' FROM submission_reason'
        ' WHERE id = ?',
        (id,)
    ).fetchone()

    if submission_status is None:
        abort(404, "Submission reason id {0} doesn't exist.".format(id))

    return submission_status

def get_submission_reason_by_name(name):
    submission_reason = get_db().execute(
        'SELECT *'
        ' FROM submission_reason'
        ' WHERE name = ?',
        (name,)
    ).fetchone()

    if submission_reason is None:
        abort(404, "Submission reason {0} doesn't exist.".format(id))

    return submission_reason