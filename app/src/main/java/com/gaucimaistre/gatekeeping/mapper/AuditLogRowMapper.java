package com.gaucimaistre.gatekeeping.mapper;

import com.gaucimaistre.gatekeeping.model.AuditLog;
import org.springframework.jdbc.core.RowMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;

@Slf4j
@Component
public class AuditLogRowMapper implements RowMapper<AuditLog> {

    @Override
    public AuditLog mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new AuditLog(
                rs.getInt("id"),
                rs.getObject("user_id", Integer.class),
                rs.getObject("date", OffsetDateTime.class),
                rs.getString("user_agent"),
                rs.getString("ip_address"),
                rs.getString("domain"),
                rs.getString("tbl"),
                rs.getObject("row_id", Integer.class),
                rs.getString("function"),
                rs.getString("action")
        );
    }
}
