from flask import Blueprint, render_template
from gatekeeping.db import get_db
from gatekeeping.api.faq import get_faqs

bp = Blueprint('faq', __name__, url_prefix='/faq')


@bp.route('/')
def index():
    return render_template('faq/index.html', page=get_faqs())