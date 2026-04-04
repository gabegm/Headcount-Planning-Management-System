package com.gaucimaistre.headcount.mapper;

import com.gaucimaistre.headcount.model.Pillar;
import org.springframework.jdbc.core.RowMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
@Component
public class PillarRowMapper implements RowMapper<Pillar> {

    @Override
    public Pillar mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Pillar(
                rs.getInt("id"),
                rs.getString("name")
        );
    }
}
