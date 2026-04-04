package com.gaucimaistre.headcount.mapper;

import com.gaucimaistre.headcount.model.PositionStatus;
import org.springframework.jdbc.core.RowMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
@Component
public class PositionStatusRowMapper implements RowMapper<PositionStatus> {

    @Override
    public PositionStatus mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new PositionStatus(
                rs.getInt("id"),
                rs.getString("name")
        );
    }
}
