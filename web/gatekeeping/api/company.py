from gatekeeping.db import get_db
from flask import abort

def get_company_names():
    companies = get_db().execute(
        'SELECT id, name'
        ' FROM company '
    ).fetchall()

    return companies

def get_companies():
    companies = get_db().execute(
        'SELECT c.id, c.name, e.name as currency'
        ' FROM company c'
        ' JOIN exchange_rate e on c.exchange_rate_id = e.id'
    ).fetchall()

    return companies

def get_company(id):
    company = get_db().execute(
        'SELECT c.id, c.name, e.name as currency'
        ' FROM company c'
        ' JOIN exchange_rate e on c.exchange_rate_id = e.id'
        ' WHERE c.id = ?',
        (id,)
    ).fetchone()

    if company is None:
        abort(404, "Company id {0} doesn't exist.".format(id))

    return company

def get_company_by_name(name):
    company = get_db().execute(
        'SELECT * FROM company WHERE name = ?',
        (name,)
    ).fetchone()

    if company is None:
        abort(404, "Company name {0} doesn't exist.".format(name))

    return company