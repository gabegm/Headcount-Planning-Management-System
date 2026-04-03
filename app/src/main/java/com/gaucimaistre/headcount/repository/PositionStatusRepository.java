package com.gaucimaistre.headcount.repository;

import com.gaucimaistre.headcount.mapper.PositionStatusRowMapper;
import com.gaucimaistre.headcount.model.PositionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
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
}
