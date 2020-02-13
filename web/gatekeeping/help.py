from flask import Blueprint, render_template
from gatekeeping.db import get_db

bp = Blueprint('help', __name__, url_prefix='/help')


@bp.route('/')
def index():
    """Show help page content."""
    name = 'help'
    help = get_db().execute(
        'SELECT body FROM page WHERE name = ?',
        (name,)
    ).fetchone()

    return render_template('help/index.html', page=help)