package com.gaucimaistre.headcount.repository;

import com.gaucimaistre.headcount.mapper.PositionStatusRowMapper;
import com.gaucimaistre.headcount.model.PositionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PositionStatusRepository {

    private final NamedParameterJdbcTemplate jdbc;
    private final PositionStatusRowMapper rowMapper;

    public List<PositionStatus> findAll() {
        return jdbc.query("SELECT id, name FROM position_status ORDER BY id", rowMapper);
    }

    public Optional<PositionStatus> findById(int id) {
        String sql = "SELECT id, name FROM position_status WHERE id = :id";
        return jdbc.query(sql, new MapSqlParameterSource("id", id), rowMapper)
                .stream().findFirst();
    }

    public int save(PositionStatus status) {
        String sql = "INSERT INTO position_status (name) VALUES (:name)";
        MapSqlParameterSource params = new MapSqlParameterSource("name", status.name());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sql, params, keyHolder, new String[]{"id"});
        return keyHolder.getKey().intValue();
    }

    public void update(PositionStatus status) {
        jdbc.update("UPDATE position_status SET name = :name WHERE id = :id",
                new MapSqlParameterSource("id", status.id()).addValue("name", status.name()));
    }

    public void delete(int id) {
        jdbc.update("DELETE FROM position_status WHERE id = :id", new MapSqlParameterSource("id", id));
    }
}
