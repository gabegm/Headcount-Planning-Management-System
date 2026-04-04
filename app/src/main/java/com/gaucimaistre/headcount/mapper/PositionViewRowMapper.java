package com.gaucimaistre.headcount.mapper;

import com.gaucimaistre.headcount.model.PositionView;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class PositionViewRowMapper implements RowMapper<PositionView> {
    @Override
    public PositionView mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new PositionView(
            rs.getInt("id"),
            rs.getString("status_name"),
            rs.getString("recruitment_status_name"),
            rs.getString("number"),
            rs.getString("pillar_name"),
            rs.getString("company_name"),
            rs.getString("department_name"),
            rs.getString("function_name"),
            rs.getBoolean("is_budget"),
            rs.getString("title"),
            rs.getString("functional_reporting_line"),
            rs.getString("disciplinary_reporting_line"),
            rs.getString("holder"),
            rs.getObject("hours", Integer.class),
            rs.getObject("start_date", java.time.LocalDate.class),
            rs.getObject("end_date", java.time.LocalDate.class),
            rs.getBigDecimal("salary"),
            rs.getBigDecimal("fringe_benefit"),
            rs.getBigDecimal("social_security_contribution"),
            rs.getBigDecimal("performance_bonus"),
            rs.getBigDecimal("super_bonus"),
            rs.getBigDecimal("management_bonus")
        );
    }
}
