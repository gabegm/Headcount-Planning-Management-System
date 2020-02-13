from flask import render_template

def page_not_found(e):
    # note that we set the 404 status explicitly
    return render_template('error/404.html'), 404