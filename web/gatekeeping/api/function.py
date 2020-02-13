from gatekeeping.db import get_db
from flask import abort


def get_functions():
    return get_db().execute('SELECT * FROM function').fetchall()


def get_function(id):
    function = get_db().execute(
        'SELECT *'
        ' FROM function '
        ' WHERE id = ?',
        (id,)
    ).fetchone()

    if function is None:
        abort(404, "Function id {0} doesn't exist.".format(id))

    return function


def get_function_by_name(name):
    function = get_db().execute(
        'SELECT *'
        ' FROM function '
        ' WHERE name = ?',
        (name,)
    ).fetchone()

    if function is None:
        abort(404, "Function name {0} doesn't exist.".format(name))

    return function
