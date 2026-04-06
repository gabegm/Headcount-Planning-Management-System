package com.gaucimaistre.gatekeeping.repository;

import com.gaucimaistre.gatekeeping.mapper.FunctionRowMapper;
import com.gaucimaistre.gatekeeping.model.Function;
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
public class FunctionRepository {

    private final NamedParameterJdbcTemplate jdbc;
    private final FunctionRowMapper rowMapper;

    public List<Function> findAll() {
        return jdbc.query("""
                SELECT id, name FROM "function" ORDER BY name
                """, rowMapper);
    }

    public Optional<Function> findById(int id) {
        String sql = """
                SELECT id, name FROM "function" WHERE id = :id
                """;
        Optional<Function> result = jdbc.query(sql, new MapSqlParameterSource("id", id), rowMapper)
                .stream().findFirst();
        if (result.isEmpty()) log.debug("Function not found with id={}", id);
        return result;
    }

    public int save(Function function) {
        log.debug("Saving {}: {}", "function", function.name());
        String sql = """
                INSERT INTO "function" (name) VALUES (:name)
                """;
        MapSqlParameterSource params = new MapSqlParameterSource("name", function.name());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sql, params, keyHolder, new String[]{"id"});
        return keyHolder.getKey().intValue();
    }

    public void update(Function function) {
        log.debug("Updating {} id={}", "function", function.id());
        String sql = """
                UPDATE "function" SET name = :name WHERE id = :id
                """;
        jdbc.update(sql, new MapSqlParameterSource("id", function.id()).addValue("name", function.name()));
    }

    public void delete(int id) {
        log.debug("Deleting {} id={}", "function", id);
        String sql = """
                DELETE FROM "function" WHERE id = :id
                """;
        jdbc.update(sql, new MapSqlParameterSource("id", id));
    }
}
