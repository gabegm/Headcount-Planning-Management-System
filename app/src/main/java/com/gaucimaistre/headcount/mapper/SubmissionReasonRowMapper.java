package com.gaucimaistre.headcount.mapper;

import com.gaucimaistre.headcount.model.SubmissionReason;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

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
