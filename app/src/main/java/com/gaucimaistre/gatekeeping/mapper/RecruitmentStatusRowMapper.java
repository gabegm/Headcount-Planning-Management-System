package com.gaucimaistre.gatekeeping.mapper;

import com.gaucimaistre.gatekeeping.model.RecruitmentStatus;
import org.springframework.jdbc.core.RowMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
@Component
public class RecruitmentStatusRowMapper implements RowMapper<RecruitmentStatus> {

    @Override
    public RecruitmentStatus mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new RecruitmentStatus(
                rs.getInt("id"),
                rs.getString("name")
        );
    }
}
