package com.gaucimaistre.headcount.mapper;

import com.gaucimaistre.headcount.model.Page;
import org.springframework.jdbc.core.RowMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;

@Slf4j
@Component
public class PageRowMapper implements RowMapper<Page> {

    @Override
    public Page mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Page(
                rs.getInt("id"),
                rs.getObject("submitted", OffsetDateTime.class),
                rs.getObject("edited", OffsetDateTime.class),
                rs.getString("name"),
                rs.getString("title"),
                rs.getString("body")
        );
    }
}
