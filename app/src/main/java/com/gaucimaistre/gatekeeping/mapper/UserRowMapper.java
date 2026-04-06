package com.gaucimaistre.gatekeeping.mapper;

import com.gaucimaistre.gatekeeping.model.User;
import com.gaucimaistre.gatekeeping.model.enums.UserType;
import org.springframework.jdbc.core.RowMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
@Component
public class UserRowMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new User(
                rs.getInt("id"),
                rs.getString("email"),
                rs.getString("password"),
                rs.getString("password_reset_token"),
                UserType.valueOf(rs.getString("type")),
                rs.getBoolean("active")
        );
    }
}
