package com.gaucimaistre.gatekeeping.mapper;

import com.gaucimaistre.gatekeeping.model.Function;
import org.springframework.jdbc.core.RowMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
@Component
public class FunctionRowMapper implements RowMapper<Function> {

    @Override
    public Function mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Function(
                rs.getInt("id"),
                rs.getString("name")
        );
    }
}
