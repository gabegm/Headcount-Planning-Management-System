from gatekeeping.db import get_db
from flask import abort
from datetime import datetime

def get_gatekeeping(id):
    gatekeeping = get_db().execute(
        'SELECT *'
        ' FROM gatekeeping '
        ' WHERE id = ?',
        (id,)
    ).fetchone()

    if gatekeeping is None:
        abort(404, "Gatekeeping id {0} doesn't exist.".format(id))

    return gatekeeping


def get_next_gatekeeping():
    gatekeeping = get_db().execute(
        'SELECT *'
        ' FROM gatekeeping'
        ' WHERE date >= ?'
        ' ORDER BY date ASC LIMIT 1',
        (str(datetime.now().date()),)
    ).fetchone()

    return gatekeeping

def isGatekeepingNext():
    get_next_gatekeeping()