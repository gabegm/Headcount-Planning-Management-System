from datetime import datetime
from flask import g, has_request_context, request, abort
from gatekeeping.db import get_db
from gatekeeping.api.audit import create_audit_log
from gatekeeping.api.user import get_users_with_password_reset_tokens
from gatekeeping.keys import mail
from gatekeeping.api.submission import (get_submissions_effective_date_today, get_submission_changes)
from os.path import basename
from email.mime.application import MIMEApplication
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText
from email.utils import COMMASPACE, formatdate
import smtplib
import schedule
import time
from numpy import nan as NaN

ALLOWED_EXTENSIONS = set(['txt', 'csv', 'tsv'])
LOGIN_REQUIRED_VIEWS = ['home', 'submission', 'position', 'help', 'faq']
ADMIN_REQUIRED_VIEWS = ['admin']

def allowed_file(filename):
    """Checks file type and name"""
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

def update_position():
    """ update positions for approved submissions """
    db = get_db()
    submissions = get_submissions_effective_date_today()

    if submissions:
        for submission in submissions:
            submission_changes = get_submission_changes(submission['id'])

            for change in submission_changes:
                db.execute(
                    'UPDATE position SET ? = ? WHERE position_id = ?',
                    (change['field'], change['value'], submission['position_id'])
                )

            db.commit()

            # create audit trail in db
            create_audit_log('127.0.0.1', 'Server', '127.0.0.1', action='Successfully updated position {} on submission effective date'.format(submission['position_id']), table='position', function='UPDATE')
    else:
        # create audit trail in db
        create_audit_log('127.0.0.1', 'Server', '127.0.0.1', action='No positions to update today', table='', function='')

def expire_password_reset_tokens():
    """ expire all password tokens """
    db = get_db()
    users = get_users_with_password_reset_tokens()

    if users:
        for user in users:
            if user['password_reset_token']:
                db.execute(
                    'UPDATE user SET password_reset_token = ? WHERE position_id = ?',
                    ('', user['id'])
                )

                # create audit trail in db
                create_audit_log('127.0.0.1', 'Server', '127.0.0.1', action='Successfully expired password token for user {}'.format(user['email']), table='user', function='UPDATE')

            db.commit()
    else:
        # create audit trail in db
        create_audit_log('127.0.0.1', 'Server', '127.0.0.1', action='No password tokens to expire today', table='', function='')

def run_scheduler():
    while True:
        schedule.run_pending()
        time.sleep(1)

def send_mail(send_from, send_to, subject, text, files=None,
              server="127.0.0.1"):
    assert isinstance(send_to, list)
    """ Sends an email to stakeholders using microsoft mail """
    msg = MIMEMultipart()
    msg['From'] = send_from
    msg['To'] = COMMASPACE.join(send_to)
    msg['Date'] = formatdate(localtime=True)
    msg['Subject'] = subject

    msg.attach(MIMEText(text))

    for f in files or []:
        with open(f, "rb") as fil:
            part = MIMEApplication(
                fil.read(),
                Name=basename(f)
            )
        # After the file is closed
        part['Content-Disposition'] = 'attachment; filename="%s"' % basename(f)
        msg.attach(part)

    smtp = smtplib.SMTP(server, 587)
    smtp.starttls()
    smtp.login(mail[0], mail[1])
    smtp.sendmail(send_from, send_to, msg.as_string())
    smtp.close()

def is_upload_valid(df):
    """Checks the contents of the upload"""

    for index, row in df.iterrows():
        if row['Status'] is NaN:
            print('Status is null at index', index)
            return False
        if row['Recruitment Status'] is NaN:
            print('Recruitment Status is null at index', index)
            return False
        if row['Number'] is NaN:
            print('Number is null at index', index)
            return False
        if row['Pillar'] is NaN:
            print('Pillar is null at index', index)
            return False
        if row['Company'] is NaN:
            print('Company is null at index', index)
            return False
        if row['Department'] is NaN:
            print('Department Name is null at index', index)
            return False
        if row['Function'] is NaN:
            print('Function is null at index', index)
            return False
        if row['Title'] is NaN:
            print('Title is null at index', index)
            return False
        if row['FTE'] is NaN:
            print('FTE is null at index', index)
            return False

    return True