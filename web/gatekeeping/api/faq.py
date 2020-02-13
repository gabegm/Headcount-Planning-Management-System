from flask import Blueprint, render_template
from gatekeeping.db import get_db

def get_faqs():
    return get_db().execute(
        'SELECT id, title, body FROM page WHERE name = \'faq\''
    ).fetchall()

def get_faq(id):
    return get_db().execute(
        'SELECT id, title, body FROM page WHERE id = ?',
        (id,)
    ).fetchone()