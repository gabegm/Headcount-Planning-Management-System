from flask import Blueprint, g, redirect, render_template, url_for, request, current_app
from gatekeeping.api.user import (get_user_function)
from gatekeeping.api.function import (get_functions, get_function)

bp = Blueprint('home', __name__)


@bp.route('/')
def index():
    functions = get_user_function(
        g.user['id']) if g.user['type'] != 'ADMIN' else get_functions()

    data = {
        'functions': dict(
            [get_function(function['function_id'])
             for function in functions] if g.user['type'] != 'ADMIN' else functions
        )
    }

    return render_template('index.html', data=data)
