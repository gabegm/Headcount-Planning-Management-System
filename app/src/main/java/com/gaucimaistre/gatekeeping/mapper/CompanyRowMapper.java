package com.gaucimaistre.gatekeeping.mapper;

import com.gaucimaistre.gatekeeping.model.Company;
import org.springframework.jdbc.core.RowMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
@Component
public class CompanyRowMapper implements RowMapper<Company> {

    @Override
    public Company mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Company(
                rs.getInt("id"),
                rs.getInt("exchange_rate_id"),
                rs.getString("name")
        );
    }
}
