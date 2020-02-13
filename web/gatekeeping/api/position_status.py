from gatekeeping.db import get_db
from flask import abort

def get_position_statuses():
    position_statuses = get_db().execute(
        'SELECT *'
        ' FROM position_status '
    ).fetchall()

    return position_statuses


def get_position_status(id):
    position_status = get_db().execute(
        'SELECT *'
        ' FROM position_status '
        ' WHERE id = ?',
        (id,)
    ).fetchone()

    if position_status is None:
        abort(404, "Position holder status id {0} doesn't exist.".format(id))

    return position_status

def get_position_status_by_name(name):
    position_status = get_db().execute(
        'SELECT *'
        ' FROM position_status '
        ' WHERE name = ?',
        (name,)
    ).fetchone()

    if position_status is None:
        abort(404, "Position holder status name {0} doesn't exist.".format(name))

    return position_status