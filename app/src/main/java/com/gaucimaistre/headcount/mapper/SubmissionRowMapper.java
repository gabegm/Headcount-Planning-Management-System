package com.gaucimaistre.headcount.mapper;

import com.gaucimaistre.headcount.model.Submission;
import org.springframework.jdbc.core.RowMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Slf4j
@Component
public class SubmissionRowMapper implements RowMapper<Submission> {

    @Override
    public Submission mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Submission(
                rs.getInt("id"),
                rs.getInt("submitter_id"),
                rs.getInt("gatekeeping_id"),
                rs.getString("position_id"),
                rs.getInt("status_id"),
                rs.getInt("reason_id"),
                rs.getString("rationale"),
                rs.getObject("effective_date", LocalDate.class),
                rs.getObject("submitted", OffsetDateTime.class),
                rs.getString("comment")
        );
    }
}
