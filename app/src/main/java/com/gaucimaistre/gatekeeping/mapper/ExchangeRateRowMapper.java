package com.gaucimaistre.gatekeeping.mapper;

import com.gaucimaistre.gatekeeping.model.ExchangeRate;
import org.springframework.jdbc.core.RowMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
@Component
public class ExchangeRateRowMapper implements RowMapper<ExchangeRate> {

    @Override
    public ExchangeRate mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new ExchangeRate(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("code"),
                rs.getBigDecimal("rate")
        );
    }
}
