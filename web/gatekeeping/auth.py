import functools
import uuid
import traceback
from datetime import timedelta
from flask import Blueprint, render_template, session, url_for, redirect, g, request, flash, app, jsonify
from werkzeug.security import check_password_hash, generate_password_hash
from gatekeeping.db import get_db
from gatekeeping.api.user import (get_user_from_email, get_admin_users, get_user_from_password_reset_token)
from gatekeeping.api.audit import create_audit_log
from gatekeeping.utils import send_mail

bp = Blueprint('auth', __name__, url_prefix='/auth')


def login_required(view):
    """View decorator that redirects anonymous users to the login page."""
    @functools.wraps(view)
    def wrapped_view(**kwargs):
        if g.user is None:
            return redirect(url_for('auth.login'))

        if g.user['active'] != 1:
            return redirect(url_for('auth.login', show_modal=True))

        return view(**kwargs)

    return wrapped_view


def admin_required(view):
    """View decorator that redirects anonymous users to the login page."""
    @functools.wraps(view)
    def wrapped_view(**kwargs):
        if g.user is None or g.user['type'] != 'ADMIN':
            return redirect(url_for('home.index'))

        return view(**kwargs)

    return wrapped_view

@bp.before_app_request
def load_logged_in_user():
    """If a user id is stored in the session, load the user object from
    the database into ``g.user``."""
    user_id = session.get('user_id')

    if user_id is None:
        g.user = None
    else:
        g.user = get_db().execute(
            'SELECT * FROM user WHERE id = ?', (user_id,)
        ).fetchone()


@bp.route('/register', methods=('GET', 'POST'))
def register():
    """Register a new user.
    Validates that the email is not already taken. Hashes the
    password for security.
    """
    if g.user:
        return redirect(url_for('home.index'))

    if request.method == 'POST':
        email = request.form['email']
        password = request.form['password']
        user_ip = request.environ.get('HTTP_X_REAL_IP', request.remote_addr)
        user_agent = request.headers.get('User-Agent')
        domain = request.headers['Host']
        errors = []

        db = get_db()

        if not email:
            errors.append('Email is required.')

        if email.split('@')[1] != 'gaucimaistre.com':
            errors.append('Not a valid gaucimaistre email address')

        if db.execute(
            'SELECT id'
            ' FROM user'
            ' WHERE email = ?',
            (email,)
        ).fetchone() is not None:
            errors.append('User {} is already registered.'.format(email))

        if not password:
            errors.append('Password is required.')

        if errors:
            [flash(error) for error in errors]
            # return flashed messages
            return render_template('auth/register.html')
        else:
            # the name is available, store it in the database and go to
            # the login page
            db.execute(
                'INSERT INTO user (email, password) VALUES (?, ?)',
                (email, generate_password_hash(password))
            )

            db.commit()

            # create audit trail in db
            create_audit_log(user_agent, user_ip, domain,
                             action='Successful registration', table='USER', function='INSERT')

            send_mail(
                send_from='hrgatekeeping@gaucimaistre.com',
                send_to=list([user['email'] for user in get_admin_users()]),
                subject='New user requesting account activation',
                text='Hi,\n\n'
                'The following user has requested to access the gatkeeping application.\n\n'
                +email,
                server='smtp.office365.com'
            )

            return redirect(url_for('auth.login', show_modal=True))

        # create audit trail in db
        create_audit_log(user_ip, user_agent, domain,
                         action='Unsuccessful registration', table='USER', function='READ')

    return render_template('auth/register.html')


@bp.route('/login', methods=('GET', 'POST'))
def login():
    """Log in a registered user by adding the user id to the session."""
    if g.user:
        return redirect(url_for('home.index'))

    if request.method == 'POST':
        email = request.form['email']
        password = request.form['password']
        user_ip = request.environ.get('HTTP_X_REAL_IP', request.remote_addr)
        user_agent = request.headers.get('User-Agent')
        domain = request.headers['Host']
        errors = []

        user = get_user_from_email(email)

        if user is None:
            errors.append('Incorrect email.')

        if not check_password_hash(user['password'], password):
            errors.append('Incorrect password.')

        if errors:
            # create audit trail in db
            create_audit_log(user_agent, user_ip, domain, action='Unsuccessful login for email {}'.format(
                email), table='USER', function='READ')

            [flash(error) for error in errors]
            # return flashed messages
            return render_template('auth/login.html')
        else:
            # store the user id in a new session and return to the index
            session.clear()
            session['user_id'] = user['id']

            # create audit trail in db
            create_audit_log(user_agent, user_ip, domain, action='Successful login',
                             table='USER', function='READ', user_id=session['user_id'])

            return redirect(url_for('index'))

    return render_template('auth/login.html')


@bp.route('/forgot-password', methods=('GET', 'POST'))
def forgot_password():
    """ Ggenerate unique token and store it in db """
    if request.method == 'POST':
        email = request.form['email']
        user = get_user_from_email(email)
        user_ip = request.environ.get('HTTP_X_REAL_IP', request.remote_addr)
        user_agent = request.headers.get('User-Agent')
        domain = request.headers['Host']
        token = str(uuid.uuid4())
        errors = []

        if email is None:
            errors.append('Email is required.')

        if user is None:
            errors.append('Email does not exist.')

        if errors:
            [flash(error) for error in errors]
            return jsonify(status='error')
        else:
            db = get_db()

            db.execute(
                'UPDATE user SET password_reset_token = ? WHERE email = ?',
                (token, email)
            )

            db.commit()

            send_mail(
                send_from='hrgatekeeping@gaucimaistre.com',
                send_to=[email],
                subject='Gatekeeping Forgotten Password',
                text='Hi,\n\n'
                'You recently notified us that you forgot your password.\n\n'
                'Kindly access the link below to reset your password.\n\n'
                + str(url_for('auth.reset', token=token, _external=True)),
                server='smtp.office365.com'
            )

            # create audit trail in db
            create_audit_log(user_agent, user_ip, domain, action='User requested password reset',
                             table='USER', function='INSERT', user_id=user['id'])

            return render_template('auth/forgot_password.html', show_modal=True)

    return render_template('auth/forgot_password.html')


@bp.route('/reset/<string:token>', methods=('GET', 'POST'))
def reset(token):
    if request.method == 'POST':
        password = request.form['password']
        confirm_password = request.form['confirmPassword']
        user_ip = request.environ.get('HTTP_X_REAL_IP', request.remote_addr)
        user_agent = request.headers.get('User-Agent')
        domain = request.headers['Host']
        user = get_user_from_password_reset_token(token)
        errors = []

        if password is None:
            errors.append('Email is required.')

        if confirm_password is None:
            errors.append('Email does not exist.')

        if password != confirm_password:
            errors.append('Passwords do not match.')

        if user['password'] == password:
            errors.append('New password cannot be the same as old password.')

        if errors:
            [flash(error) for error in errors]
            return jsonify(status='error')
        else:
            db = get_db()

            db.execute(
                'UPDATE user SET password = ?, password_reset_token = NULL WHERE password_reset_token = ?',
                (generate_password_hash(password), token)
            )

            db.commit()

            # create audit trail in db
            create_audit_log(user_agent, user_ip, domain, action='User reset password',
                            table='USER', function='', user_id=user['id'])

            return redirect(url_for('index'))

    return render_template('auth/reset.html')


@bp.route('/logout')
def logout():
    """Clear the current session, including the stored user id."""
    if g.user is None:
        return redirect(url_for('index'))

    user_ip = request.environ.get('HTTP_X_REAL_IP', request.remote_addr)
    user_agent = request.headers.get('User-Agent')
    domain = request.headers['Host']
    user_id = session.get('user_id')

    # create audit trail in db
    create_audit_log(user_agent, user_ip, domain, action='User logged out',
                     table='USER', function='READ', user_id=user_id)

    session.clear()

    return redirect(url_for('index'))
