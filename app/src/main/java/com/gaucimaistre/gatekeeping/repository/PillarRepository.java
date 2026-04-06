package com.gaucimaistre.gatekeeping.repository;

import com.gaucimaistre.gatekeeping.mapper.PillarRowMapper;
import com.gaucimaistre.gatekeeping.model.Pillar;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PillarRepository {

    private final NamedParameterJdbcTemplate jdbc;
    private final PillarRowMapper rowMapper;

    public List<Pillar> findAll() {
        return jdbc.query("SELECT id, name FROM pillar ORDER BY name", rowMapper);
    }

    public Optional<Pillar> findById(int id) {
        String sql = "SELECT id, name FROM pillar WHERE id = :id";
        Optional<Pillar> result = jdbc.query(sql, new MapSqlParameterSource("id", id), rowMapper)
                .stream().findFirst();
        if (result.isEmpty()) log.debug("Pillar not found with id={}", id);
        return result;
    }

    public int save(Pillar pillar) {
        log.debug("Saving {}: {}", "pillar", pillar.name());
        String sql = "INSERT INTO pillar (name) VALUES (:name)";
        MapSqlParameterSource params = new MapSqlParameterSource("name", pillar.name());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sql, params, keyHolder, new String[]{"id"});
        return keyHolder.getKey().intValue();
    }

    public void update(Pillar pillar) {
        log.debug("Updating {} id={}", "pillar", pillar.id());
        jdbc.update("UPDATE pillar SET name = :name WHERE id = :id",
                new MapSqlParameterSource("id", pillar.id()).addValue("name", pillar.name()));
    }

    public void delete(int id) {
        log.debug("Deleting {} id={}", "pillar", id);
        jdbc.update("DELETE FROM pillar WHERE id = :id", new MapSqlParameterSource("id", id));
    }
}
