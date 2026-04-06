package com.gaucimaistre.gatekeeping.mapper;

import com.gaucimaistre.gatekeeping.model.Gatekeeping;
import org.springframework.jdbc.core.RowMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

@Slf4j
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
