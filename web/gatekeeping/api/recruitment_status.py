from gatekeeping.db import get_db
from flask import abort

def get_recruitment_statuses():
    recruitment_statuses = get_db().execute(
        'SELECT *'
        ' FROM recruitment_status'
    ).fetchall()

    return recruitment_statuses


def get_recruitment_status(id):
    recruitment_status = get_db().execute(
        'SELECT *'
        ' FROM recruitment_status '
        ' WHERE id = ?',
        (id,)
    ).fetchone()

    if recruitment_status is None:
        abort(404, "Recruitment status id {0} doesn't exist.".format(id))

    return recruitment_status

def get_recruitment_status_by_name(name):
    recruitment_status = get_db().execute(
        'SELECT *'
        ' FROM recruitment_status '
        ' WHERE name = ?',
        (name,)
    ).fetchone()

    if recruitment_status is None:
        abort(404, "Recruitment status name {0} doesn't exist.".format(name))

    return recruitment_status