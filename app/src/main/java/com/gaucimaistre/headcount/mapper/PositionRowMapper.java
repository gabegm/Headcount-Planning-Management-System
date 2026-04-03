package com.gaucimaistre.headcount.mapper;

import com.gaucimaistre.headcount.model.Position;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

@Component
public class PositionRowMapper implements RowMapper<Position> {

    @Override
    public Position mapRow(ResultSet rs, int rowNum) throws SQLException {
        Integer hours = rs.getObject("hours", Integer.class);

        return new Position(
                rs.getInt("id"),
                rs.getInt("status_id"),
                rs.getInt("recruitment_status_id"),
                rs.getString("number"),
                rs.getInt("pillar_id"),
                rs.getInt("company_id"),
                rs.getInt("department_id"),
                rs.getInt("function_id"),
                rs.getBoolean("is_budget"),
                rs.getString("title"),
                rs.getString("functional_reporting_line"),
                rs.getString("disciplinary_reporting_line"),
                rs.getString("holder"),
                hours,
                rs.getObject("start_date", LocalDate.class),
                rs.getObject("end_date", LocalDate.class),
                rs.getBigDecimal("salary"),
                rs.getBigDecimal("fringe_benefit"),
                rs.getBigDecimal("social_security_contribution"),
                rs.getBigDecimal("performance_bonus"),
                rs.getBigDecimal("super_bonus"),
                rs.getBigDecimal("management_bonus")
        );
    }
}
