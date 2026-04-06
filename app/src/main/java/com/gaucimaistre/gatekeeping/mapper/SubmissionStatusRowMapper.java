package com.gaucimaistre.gatekeeping.mapper;

import com.gaucimaistre.gatekeeping.model.SubmissionStatus;
import org.springframework.jdbc.core.RowMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
@Component
public class SubmissionStatusRowMapper implements RowMapper<SubmissionStatus> {

    @Override
    public SubmissionStatus mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new SubmissionStatus(
                rs.getInt("id"),
                rs.getString("name")
        );
    }
}
