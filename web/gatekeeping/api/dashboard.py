from calendar import monthrange
from datetime import datetime

import pandas as pd
from flask import Blueprint, jsonify, abort, g
from gatekeeping.api.budget import get_budget
from gatekeeping.api.position import get_positions
from gatekeeping.api.function import get_functions, get_function
from gatekeeping.api.user import get_user_function

def get_line_chart(function=None):
    positions = get_positions(check_submitter=False)
    budget = get_budget()

    columns = [row.keys() for row in positions]

    positions = pd.DataFrame(positions, columns=columns[0])
    budget = pd.DataFrame(budget, columns=columns[0])

    if function:
        if function != 'All':
            positions = positions.loc[positions['function'] == function]
            budget = budget.loc[budget['function'] == function]

        if  g.user['type'] != 'ADMIN' and function == 'All':
            functions = get_user_function(g.user['id'])
            function_names = [get_function(function['function_id'])['name'] for function in functions]

            positions = positions.loc[positions['function'].isin(function_names)]
            budget = budget.loc[budget['function'].isin(function_names)]


    positions['FTE'] = pd.to_numeric(positions['hours'], errors='coerce') / 40
    budget['FTE'] = pd.to_numeric(budget['hours'], errors='coerce') / 40

    positions['salary'] = pd.to_numeric(positions['salary'], errors='coerce')
    positions['fringe_benefit'] = pd.to_numeric(positions['fringe_benefit'], errors='coerce')
    positions['social_security_contribution'] = pd.to_numeric(positions['social_security_contribution'], errors='coerce')

    budget['salary'] = pd.to_numeric(budget['salary'], errors='coerce')
    budget['fringe_benefit'] = pd.to_numeric(budget['fringe_benefit'], errors='coerce')
    budget['social_security_contribution'] = pd.to_numeric(budget['social_security_contribution'], errors='coerce')

    positions['total_cost'] = positions['salary'].add(positions['fringe_benefit'], fill_value=0).add(positions['social_security_contribution'], fill_value=0)
    budget['total_cost'] = budget['salary'].add(budget['fringe_benefit'], fill_value=0).add(budget['social_security_contribution'], fill_value=0)

    positions['start_date'] = pd.to_datetime(positions['start_date'], errors='coerce')
    positions['end_date'] = pd.to_datetime(positions['end_date'], errors='coerce')

    budget['start_date'] = pd.to_datetime(budget['start_date'], errors='coerce')
    budget['end_date'] = pd.to_datetime(budget['end_date'], errors='coerce')

    year = datetime.now().year

    months = {1:'January', 2:'February', 3:'March', 4:'April', 5:'May', 6:'June', 7:'July', 8:'August', 9:'September', 10:'October', 11:'November', 12:'December'}
    index=['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December']

    headcount_2018 = pd.Series(index=index)
    budgeted_headcount_2018 = pd.Series(index=index)
    headcount_cost_2018 = pd.Series(index=index)
    budgeted_headcount_cost_2018 = pd.Series(index=index)

    total_proposed_increase = 0
    proposed_monthly_increase = budget.loc[budget['recruitment_status'].isin(['Proposed', 'Approved'])]['FTE'].sum()/12

    for month in range(1,13):
        total_proposed_increase += proposed_monthly_increase
        hc = budget.loc[budget['recruitment_status'].isin(['On-Board', 'Contracted'])]['FTE'].sum()
        budgeted_headcount_2018[months[month]] = pd.Series(hc + total_proposed_increase)

    for month in range(1,13):
        total_proposed_increase += proposed_monthly_increase
        hc = budget.loc[budget['recruitment_status'].isin(['On-Board', 'Contracted'])]['total_cost'].sum()
        budgeted_headcount_cost_2018[months[month]] = pd.Series(hc + total_proposed_increase)

    for month in range(1,13):
        hc = positions.loc[(positions['end_date'] >= datetime(year, month, 1)) & (positions['start_date'] <= datetime(year, month, monthrange(year, month)[1])) | (positions['end_date'].isnull()) & (positions['start_date'] <= datetime(year, month, monthrange(year,month)[1]))]['FTE'].sum()
        headcount_2018[months[month]] = pd.Series(hc)

    for month in range(1,13):
        hc = positions.loc[(positions['end_date'] >= datetime(year, month, 1)) & (positions['start_date'] <= datetime(year, month, monthrange(year, month)[1])) | (positions['end_date'].isnull()) & (positions['start_date'] <= datetime(year, month, monthrange(year,month)[1]))]['total_cost'].sum()
        headcount_cost_2018[months[month]] = pd.Series(hc)

    return {
        'positions': headcount_2018.to_json(),
        'budget': budgeted_headcount_2018.to_json(),
        'positions_cost': headcount_cost_2018.to_json(),
        'budget_cost': budgeted_headcount_cost_2018.to_json()
    }