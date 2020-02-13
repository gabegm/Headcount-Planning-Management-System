from gatekeeping.db import get_db
from flask import abort

def get_departments():
    departments = get_db().execute(
        'SELECT *'
        ' FROM department '
    ).fetchall()

    return departments

def get_department(id):
    department = get_db().execute(
        'SELECT *'
        ' FROM department '
        ' WHERE id = ?',
        (id,)
    ).fetchone()

    if department is None:
        abort(404, "Department id {0} doesn't exist.".format(id))

    return department


def get_department_by_name(name):
    department = get_db().execute(
        'SELECT *'
        ' FROM department '
        ' WHERE name = ?',
        (name,)
    ).fetchone()

    if department is None:
        abort(404, "Department name {0} doesn't exist.".format(name))

    return department