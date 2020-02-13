import os
from contextlib import closing
import sqlite3
import time
from datetime import datetime

import schedule
from os import listdir, getcwd
from os.path import isfile, join

DATABASE_PATH = os.path.join(os.getcwd(), 'instance', 'gatekeeping.sqlite')


def get_db():
    db = sqlite3.connect(DATABASE_PATH)
    db.row_factory = sqlite3.Row

    return db


def execute_statement(statement):
    with closing(get_db()) as conn:  # auto-closes
        with conn:  # auto-commits
            with closing(conn.cursor()) as cursor:  # auto-closes
                cursor.execute(statement)


def update_position():
    """ update positions for approved submissions """
    submissions = get_submissions_effective_date_today()

    if submissions:
        for submission in submissions:
            submission_changes = get_submission_changes(submission['id'])

            for change in submission_changes:
                execute_statement(
                    f"UPDATE position SET {change['field']} = '{change['change']}' WHERE number = '{submission['position_id']}' and isBudget = 0"
                )

            # create audit trail in db
            create_audit_log('127.0.0.1', 'Server', '127.0.0.1', action='Successfully updated position {} on submission effective date'.format(
                submission['position_id']), table='position', function='UPDATE')
    else:
        # create audit trail in db
        create_audit_log('127.0.0.1', 'Server', '127.0.0.1',
                         action='No positions to update today', table='position', function='')


def expire_password_reset_tokens():
    """ expire all password tokens """
    users = get_users_with_password_reset_tokens()

    with closing(get_db()) as db:
        if users:
            for user in users:
                if user['password_reset_token']:
                    execute_statement(
                        f"UPDATE user SET password_reset_token = '' WHERE id = {user['id']}"
                    )

                    # create audit trail in db
                    create_audit_log('127.0.0.1', 'Server', '127.0.0.1', action='Successfully expired password token for user {}'.format(
                        user['email']), table='user', function='UPDATE')
        else:
            # create audit trail in db
            create_audit_log('127.0.0.1', 'Server', '127.0.0.1',
                             action='No password tokens to expire today', table='user', function='')


def get_users_with_password_reset_tokens():
    with closing(get_db()) as db:
        return db.execute(
            'SELECT *'
            ' FROM user'
            ' WHERE password_reset_token NOT NULL'
        ).fetchall()


def get_submission_changes(submission_id):
    with closing(get_db()) as db:
        return db.execute(
            'SELECT *'
            ' FROM submission_change'
            ' WHERE submission_id = ?',
            (submission_id,)
        ).fetchall()


def get_submissions_effective_date_today():
    with closing(get_db()) as db:
        return db.execute(
            'SELECT * FROM submission'
            ' WHERE effective_date = (?)',
            (str(datetime.now().date()),)
        ).fetchall()


def create_audit_log(user_agent, user_ip, domain, action, table, function, row_id='NULL', user_id='NULL'):
    execute_statement(
        f"INSERT INTO audit (user_id, user_agent, ip_address, domain, action, tbl, row_id, function)\
         VALUES ({user_id}, '{user_agent}', '{user_ip}', '{domain}', '{action}', '{table}', {row_id}, '{function}')"
    )


schedule.every().day.at("00:00").do(update_position)
schedule.every().day.at("00:10").do(expire_password_reset_tokens)
# schedule.every(5).seconds.do(update_position)
# schedule.every(5).seconds.do(expire_password_reset_tokens)


while True:
    schedule.run_pending()
    time.sleep(1)
