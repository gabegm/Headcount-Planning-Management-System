package com.gaucimaistre.gatekeeping.mapper;

import com.gaucimaistre.gatekeeping.model.SubmissionReason;
import org.springframework.jdbc.core.RowMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
@Component
public class SubmissionReasonRowMapper implements RowMapper<SubmissionReason> {

    @Override
    public SubmissionReason mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new SubmissionReason(
                rs.getInt("id"),
                rs.getString("name")
        );
    }
}
