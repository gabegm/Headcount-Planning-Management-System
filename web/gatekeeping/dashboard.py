from calendar import monthrange
from datetime import datetime

import pandas as pd
from flask import Blueprint, abort, g, jsonify
from gatekeeping.api.dashboard import get_line_chart
from gatekeeping.api.function import get_function, get_functions
from gatekeeping.api.user import get_user_function

bp = Blueprint('dashboard', __name__, url_prefix='/dashboard')


@bp.route('/<int:id>', endpoint='render', methods=('GET',))
def dashboard(id):
    user_functions = get_user_function(g.user['id'])
    function_ids = [function['function_id'] for function in user_functions]

    if g.user['type'] != 'ADMIN' and id != 0 and id not in function_ids:
        abort(403, 'You are assigned to the selected function')

    functions = get_functions()
    line_chart = get_line_chart(function=get_function(id)[
                                'name'] if id != 0 else 'All')
    positions = line_chart['positions']
    budget = line_chart['budget']
    positions_cost = line_chart['positions_cost']
    budget_cost = line_chart['budget_cost']

    data = {
        'positions': positions,
        'budget': budget,
        'positions_cost': positions_cost,
        'budget_cost': budget_cost,
        'functions': dict(functions)
    }

    return jsonify(dict(data))
