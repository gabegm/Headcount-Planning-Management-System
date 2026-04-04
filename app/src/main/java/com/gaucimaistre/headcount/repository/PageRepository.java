package com.gaucimaistre.headcount.repository;

import com.gaucimaistre.headcount.mapper.PageRowMapper;
import com.gaucimaistre.headcount.model.Page;
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
public class PageRepository {

    private final NamedParameterJdbcTemplate jdbc;
    private final PageRowMapper rowMapper;

    private static final String SELECT_ALL_COLUMNS = """
            SELECT id, submitted, edited, name, title, body FROM page
            """;

    public List<Page> findByName(String name) {
        String sql = SELECT_ALL_COLUMNS + "WHERE name = :name ORDER BY title";
        return jdbc.query(sql, new MapSqlParameterSource("name", name), rowMapper);
    }

    public Optional<Page> findByNameAndTitle(String name, String title) {
        String sql = SELECT_ALL_COLUMNS + "WHERE name = :name AND title = :title";
        return jdbc.query(sql, new MapSqlParameterSource("name", name)
                        .addValue("title", title), rowMapper)
                .stream().findFirst();
    }

    public Optional<Page> findById(int id) {
        String sql = SELECT_ALL_COLUMNS + "WHERE id = :id";
        return jdbc.query(sql, new MapSqlParameterSource("id", id), rowMapper)
                .stream().findFirst();
    }

    public int save(Page page) {
        log.debug("Saving {}: {}", "page", page.title());
        String sql = """
                INSERT INTO page (name, title, body)
                VALUES (:name, :title, :body)
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", page.name())
                .addValue("title", page.title())
                .addValue("body", page.body());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sql, params, keyHolder, new String[]{"id"});
        return keyHolder.getKey().intValue();
    }

    public void update(Page page) {
        log.debug("Updating {} id={}", "page", page.id());
        String sql = """
                UPDATE page
                SET name = :name, title = :title, body = :body, edited = now()
                WHERE id = :id
                """;
        jdbc.update(sql, new MapSqlParameterSource()
                .addValue("id", page.id())
                .addValue("name", page.name())
                .addValue("title", page.title())
                .addValue("body", page.body()));
    }

    public void delete(int id) {
        log.debug("Deleting {} id={}", "page", id);
        jdbc.update("DELETE FROM page WHERE id = :id", new MapSqlParameterSource("id", id));
    }
}
