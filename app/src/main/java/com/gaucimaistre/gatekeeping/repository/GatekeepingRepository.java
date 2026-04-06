package com.gaucimaistre.gatekeeping.repository;

import com.gaucimaistre.gatekeeping.mapper.GatekeepingRowMapper;
import com.gaucimaistre.gatekeeping.model.Gatekeeping;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class GatekeepingRepository {

    private final NamedParameterJdbcTemplate jdbc;
    private final GatekeepingRowMapper rowMapper;

    private static final String SELECT_ALL_COLUMNS = """
            SELECT id, date, submission_deadline, notes FROM gatekeeping
            """;

    public Optional<Gatekeeping> findById(int id) {
        String sql = SELECT_ALL_COLUMNS + "WHERE id = :id";
        Optional<Gatekeeping> result = jdbc.query(sql, new MapSqlParameterSource("id", id), rowMapper)
                .stream().findFirst();
        if (result.isEmpty()) log.debug("Gatekeeping not found with id={}", id);
        return result;
    }

    public List<Gatekeeping> findAll() {
        return jdbc.query(SELECT_ALL_COLUMNS + "ORDER BY date DESC", rowMapper);
    }

    public Optional<Gatekeeping> findCurrent() {
        String sql = SELECT_ALL_COLUMNS + """
                WHERE date <= :today AND submission_deadline >= :today
                ORDER BY date DESC
                LIMIT 1
                """;
        return jdbc.query(sql, new MapSqlParameterSource("today", LocalDate.now()), rowMapper)
                .stream().findFirst();
    }

    public int save(Gatekeeping gatekeeping) {
        log.debug("Saving {}: {}", "gatekeeping", gatekeeping.date());
        String sql = """
                INSERT INTO gatekeeping (date, submission_deadline, notes)
                VALUES (:date, :submissionDeadline, :notes)
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("date", gatekeeping.date())
                .addValue("submissionDeadline", gatekeeping.submissionDeadline())
                .addValue("notes", gatekeeping.notes());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sql, params, keyHolder, new String[]{"id"});
        return keyHolder.getKey().intValue();
    }

    public void update(Gatekeeping gatekeeping) {
        log.debug("Updating {} id={}", "gatekeeping", gatekeeping.id());
        String sql = """
                UPDATE gatekeeping
                SET date = :date, submission_deadline = :submissionDeadline, notes = :notes
                WHERE id = :id
                """;
        jdbc.update(sql, new MapSqlParameterSource()
                .addValue("id", gatekeeping.id())
                .addValue("date", gatekeeping.date())
                .addValue("submissionDeadline", gatekeeping.submissionDeadline())
                .addValue("notes", gatekeeping.notes()));
    }

    public void delete(int id) {
        log.debug("Deleting {} id={}", "gatekeeping", id);
        jdbc.update("DELETE FROM gatekeeping WHERE id = :id", new MapSqlParameterSource("id", id));
    }
}
