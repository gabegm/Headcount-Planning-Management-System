package com.gaucimaistre.headcount.mapper;

import com.gaucimaistre.headcount.model.SubmissionChange;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;

@Component
public class SubmissionChangeRowMapper implements RowMapper<SubmissionChange> {

    @Override
    public SubmissionChange mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new SubmissionChange(
                rs.getInt("id"),
                rs.getInt("submission_id"),
                rs.getObject("submitted", OffsetDateTime.class),
                rs.getString("field"),
                rs.getString("change")
        );
    }
}
