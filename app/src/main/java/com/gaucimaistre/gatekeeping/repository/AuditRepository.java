package com.gaucimaistre.gatekeeping.repository;

import com.gaucimaistre.gatekeeping.mapper.AuditLogRowMapper;
import com.gaucimaistre.gatekeeping.model.AuditLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AuditRepository {

    private final NamedParameterJdbcTemplate jdbc;
    private final AuditLogRowMapper rowMapper;

    private static final String SELECT_ALL_COLUMNS = """
            SELECT id, user_id, date, user_agent, ip_address, domain,
                   tbl, row_id, function, action
            FROM audit
            """;

    public List<AuditLog> findAll() {
        return jdbc.query(SELECT_ALL_COLUMNS + "ORDER BY date DESC", rowMapper);
    }

    public List<AuditLog> findByUserId(int userId) {
        String sql = SELECT_ALL_COLUMNS + "WHERE user_id = :userId ORDER BY date DESC";
        return jdbc.query(sql, new MapSqlParameterSource("userId", userId), rowMapper);
    }

    public void save(AuditLog auditLog) {
        log.debug("Saving {}: {}", "audit-log", auditLog);
        String sql = """
                INSERT INTO audit (user_id, user_agent, ip_address, domain, tbl, row_id, function, action)
                VALUES (:userId, :userAgent, :ipAddress, :domain, :tbl, :rowId, :function, :action)
                """;
        jdbc.update(sql, new MapSqlParameterSource()
                .addValue("userId", auditLog.userId())
                .addValue("userAgent", auditLog.userAgent())
                .addValue("ipAddress", auditLog.ipAddress())
                .addValue("domain", auditLog.domain())
                .addValue("tbl", auditLog.tbl())
                .addValue("rowId", auditLog.rowId())
                .addValue("function", auditLog.function())
                .addValue("action", auditLog.action()));
    }
}
