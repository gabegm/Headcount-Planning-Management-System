from gatekeeping.db import get_db
from flask import abort

def get_pillars():
    pillars = get_db().execute(
        'SELECT *'
        ' FROM pillar '
    ).fetchall()

    return pillars

def get_pillar(id):
    pillar = get_db().execute(
        'SELECT *'
        ' FROM pillar '
        ' WHERE id = ?',
        (id,)
    ).fetchone()

    if pillar is None:
        abort(404, "Pillar id {0} doesn't exist.".format(id))

    return pillar


def get_pillar_by_name(name):
    pillar = get_db().execute(
        'SELECT *'
        ' FROM pillar '
        ' WHERE name = ?',
        (name,)
    ).fetchone()

    if pillar is None:
        abort(404, "Pillar id {0} doesn't exist.".format(name))

    return pillar