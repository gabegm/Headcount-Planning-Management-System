from gatekeeping.db import get_db

def create_audit_log(user_agent, user_ip, domain, action, table, function, row_id='NULL', user_id='NULL'):
    db = get_db()

    db.execute(
        'INSERT INTO audit ('
        'user_id, user_agent, ip_address, domain, action, tbl, row_id, function'
        ') VALUES (?, ?, ?, ?, ?, ?, ?, ?)',
        (user_id, user_agent, user_ip, domain, action, table, row_id, function)
    )

    db.commit()