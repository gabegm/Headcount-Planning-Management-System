package com.gaucimaistre.headcount.mapper;

import com.gaucimaistre.headcount.model.Function;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

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
