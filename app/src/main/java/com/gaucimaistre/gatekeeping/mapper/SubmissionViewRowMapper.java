package com.gaucimaistre.gatekeeping.mapper;

import com.gaucimaistre.gatekeeping.model.SubmissionView;
import org.springframework.jdbc.core.RowMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Slf4j
@Component
public class SubmissionViewRowMapper implements RowMapper<SubmissionView> {

    @Override
    public SubmissionView mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new SubmissionView(
                rs.getInt("id"),
                rs.getInt("submitter_id"),
                rs.getString("submitter_email"),
                rs.getInt("gatekeeping_id"),
                rs.getString("position_id"),
                rs.getInt("status_id"),
                rs.getString("status_name"),
                rs.getInt("reason_id"),
                rs.getString("reason_name"),
                rs.getString("rationale"),
                rs.getObject("effective_date", LocalDate.class),
                rs.getObject("submitted", OffsetDateTime.class),
                rs.getString("comment")
        );
    }
}
