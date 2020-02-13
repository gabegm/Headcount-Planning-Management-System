from gatekeeping.db import get_db
from flask import abort

def get_submission_statuses():
    submission_statuses = get_db().execute(
        'SELECT *'
        ' FROM submission_status'
    ).fetchall()

    return submission_statuses

def get_submission_status(id):
    submission_status = get_db().execute(
        'SELECT *'
        ' FROM submission_status '
        ' WHERE id = ?',
        (id,)
    ).fetchone()

    if submission_status is None:
        abort(404, "Submission status id {0} doesn't exist.".format(id))

    return submission_status

def get_submission_status_by_name(name):
    submission_status = get_db().execute(
        'SELECT *'
        ' FROM submission_status '
        ' WHERE name = ?',
        (name,)
    ).fetchone()

    if submission_status is None:
        abort(404, "Submission status name {0} doesn't exist.".format(id))

    return submission_status