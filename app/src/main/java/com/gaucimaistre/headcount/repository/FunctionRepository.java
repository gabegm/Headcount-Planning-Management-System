package com.gaucimaistre.headcount.repository;

import com.gaucimaistre.headcount.mapper.FunctionRowMapper;
import com.gaucimaistre.headcount.model.Function;
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
        return jdbc.query(sql, new MapSqlParameterSource("id", id), rowMapper)
                .stream().findFirst();
    }

    public int save(Function function) {
        String sql = """
                INSERT INTO "function" (name) VALUES (:name)
                """;
        MapSqlParameterSource params = new MapSqlParameterSource("name", function.name());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sql, params, keyHolder, new String[]{"id"});
        return keyHolder.getKey().intValue();
    }

    public void update(Function function) {
        String sql = """
                UPDATE "function" SET name = :name WHERE id = :id
                """;
        jdbc.update(sql, new MapSqlParameterSource("id", function.id()).addValue("name", function.name()));
    }

    public void delete(int id) {
        String sql = """
                DELETE FROM "function" WHERE id = :id
                """;
        jdbc.update(sql, new MapSqlParameterSource("id", id));
    }
}
