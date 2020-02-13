from flask import abort
from gatekeeping.db import get_db


def get_user(id):
    user = get_db().execute(
        'SELECT * FROM user  WHERE id = ?',
        (id,)
    ).fetchone()

    if user is None:
        abort(404, "User id {0} doesn't exist.".format(id))

    return user

def get_user_from_email(email):
    user = get_db().execute(
        'SELECT * FROM user WHERE email = ?',
        (email,)
    ).fetchone()

    if user is None:
        abort(404, "User email {0} doesn't exist.".format(email))

    return user

def get_user_from_password_reset_token(token):
    user = get_db().execute(
        'SELECT * FROM user WHERE password_reset_token = ?',
        (token,)
    ).fetchone()

    if user is None:
        abort(404, "User token {0} doesn't exist.".format(token))

    return user

def get_user_function(user_id):
    return get_db().execute(
        'SELECT * FROM user_function WHERE user_id = ?',
        (user_id,)
    ).fetchall()

def has_user_function(id):
    user_function = get_db().execute(
        'SELECT count(*) FROM user_function where user_id = ?',
        (id,)
    ).fetchall()

    if user_function:
        return True

    return False

def get_users():
    return get_db().execute(
        'SELECT * FROM user'
    ).fetchall()


def get_users_with_password_reset_tokens():
    return get_db().execute(
        'SELECT email, password_reset_token FROM user WHERE password_reset_token NOT NULL'
    ).fetchall()


def get_active_users():
    return get_db().execute(
        'SELECT * FROM user WHERE active = 1'
    ).fetchall()


def get_admin_users():
    return get_db().execute(
        'SELECT * FROM user WHERE type=\'ADMIN\''
    ).fetchall()