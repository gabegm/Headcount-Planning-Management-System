package com.gaucimaistre.headcount.mapper;

import com.gaucimaistre.headcount.model.Department;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class DepartmentRowMapper implements RowMapper<Department> {

    @Override
    public Department mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Department(
                rs.getInt("id"),
                rs.getString("name")
        );
    }
}
