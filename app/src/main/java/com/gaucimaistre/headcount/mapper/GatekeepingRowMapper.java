package com.gaucimaistre.headcount.mapper;

import com.gaucimaistre.headcount.model.Gatekeeping;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

@Component
public class GatekeepingRowMapper implements RowMapper<Gatekeeping> {

    @Override
    public Gatekeeping mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Gatekeeping(
                rs.getInt("id"),
                rs.getObject("date", LocalDate.class),
                rs.getObject("submission_deadline", LocalDate.class),
                rs.getString("notes")
        );
    }
}
