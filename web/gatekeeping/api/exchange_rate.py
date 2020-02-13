from gatekeeping.db import get_db
from flask import abort


def get_exchange_rates():
    return get_db().execute(
        'SELECT * FROM exchange_rate'
    ).fetchall()


def get_exchange_rate(id):
    exchange_rate = get_db().execute(
        'SELECT *'
        ' FROM exchange_rate '
        ' WHERE id = ?',
        (id,)
    ).fetchone()

    if exchange_rate is None:
        abort(404, "Exchange rate id {0} doesn't exist.".format(id))

    return exchange_rate


def get_exchange_rate_by_name(name):
    exchange_rate = get_db().execute(
        'SELECT *'
        ' FROM exchange_rate '
        ' WHERE name = ?',
        (name,)
    ).fetchone()

    if exchange_rate is None:
        abort(404, "Exchange rate name {0} doesn't exist.".format(name))

    return exchange_rate
