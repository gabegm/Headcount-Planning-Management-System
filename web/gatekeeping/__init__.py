import os

from flask import Flask, session, request, redirect, url_for, g
import schedule
import time
from threading import Thread
from datetime import (datetime, timedelta)
import sys


def create_app(test_config=None):
    """Create and configure an instance of the Flask application."""
    app = Flask(__name__, instance_relative_config=True)
    app.config.from_mapping(
        # a default secret that should be overridden by instance config
        SECRET_KEY='dev',
        # store the database in the instance folder
        DATABASE=os.path.join(app.instance_path, 'gatekeeping.sqlite'),
    )

    if test_config is None:
        # load the instance config, if it exists, when not testing
        app.config.from_pyfile('config.py', silent=True)
    else:
        # load the test config if passed in
        app.config.update(test_config)

    # ensure the instance folder exists
    try:
        os.makedirs(app.instance_path)
    except OSError:
        pass

    # register the database commands
    from gatekeeping import db
    db.init_app(app)

    # apply the blueprints to the app
    from gatekeeping import auth, position, submission, home, help, faq, dashboard, admin

    app.register_blueprint(auth.bp)
    app.register_blueprint(position.bp)
    app.register_blueprint(submission.bp)
    app.register_blueprint(home.bp)
    app.register_blueprint(help.bp)
    app.register_blueprint(faq.bp)
    app.register_blueprint(dashboard.bp)
    app.register_blueprint(admin.bp)

    from gatekeeping.utils import (update_position, expire_password_reset_tokens, run_scheduler, LOGIN_REQUIRED_VIEWS, ADMIN_REQUIRED_VIEWS)

    @app.before_request
    def before_request():
        """ Expire sessions after 20 minutes """
        session.permanent = True
        app.permanent_session_lifetime = timedelta(minutes=20)
        session.modified = True

        """View decorator that redirects anonymous users to the login page."""
        if request.blueprint in LOGIN_REQUIRED_VIEWS:
            if g.user is None:
                return redirect(url_for('auth.login'))

            if g.user['active'] != 1:
                return redirect(url_for('auth.login', show_modal=True))

        if request.blueprint in ADMIN_REQUIRED_VIEWS:
            if g.user is None or g.user['type'] != 'ADMIN':
                return redirect(url_for('home.index'))

    # http://flask.pocoo.org/docs/1.0/appcontext/
    #with app.app_context():
    #    schedule.every().day.at("00:00").do(update_position)
    #    schedule.every().day.at("00:10").do(expire_password_tokens)
    #    t = Thread(target=run_scheduler)
    #    t.deamon = True # set this thread as a Daemon Thread
    #    t.start()

    @app.context_processor
    def inject_now():
        """ make the datetime object accessible in all views """
        return {'now': datetime.utcnow()}

    # apply custom errors to the app
    from gatekeeping.error import page_not_found
    #app.register_error_handler(404, page_not_found)

    # make url_for('index') == url_for('blog.index')
    # in another app, you might define a separate main index here with
    # app.route, while giving the blog blueprint a url_prefix, but for
    # the tutorial the blog will be the main index
    app.add_url_rule('/', endpoint='index', view_func=home.index)

    return app
