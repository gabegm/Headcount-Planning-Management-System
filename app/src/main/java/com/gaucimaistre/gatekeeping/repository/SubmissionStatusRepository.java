package com.gaucimaistre.gatekeeping.repository;

import com.gaucimaistre.gatekeeping.mapper.SubmissionStatusRowMapper;
import com.gaucimaistre.gatekeeping.model.SubmissionStatus;
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
public class SubmissionStatusRepository {

    private final NamedParameterJdbcTemplate jdbc;
    private final SubmissionStatusRowMapper rowMapper;

    public List<SubmissionStatus> findAll() {
        return jdbc.query("SELECT id, name FROM submission_status ORDER BY id", rowMapper);
    }

    public Optional<SubmissionStatus> findById(int id) {
        String sql = "SELECT id, name FROM submission_status WHERE id = :id";
        Optional<SubmissionStatus> result = jdbc.query(sql, new MapSqlParameterSource("id", id), rowMapper)
                .stream().findFirst();
        if (result.isEmpty()) log.debug("SubmissionStatus not found with id={}", id);
        return result;
    }

    public int save(SubmissionStatus status) {
        log.debug("Saving {}: {}", "submission-status", status.name());
        String sql = "INSERT INTO submission_status (name) VALUES (:name)";
        MapSqlParameterSource params = new MapSqlParameterSource("name", status.name());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sql, params, keyHolder, new String[]{"id"});
        return keyHolder.getKey().intValue();
    }

    public void update(SubmissionStatus status) {
        log.debug("Updating {} id={}", "submission-status", status.id());
        jdbc.update("UPDATE submission_status SET name = :name WHERE id = :id",
                new MapSqlParameterSource("id", status.id()).addValue("name", status.name()));
    }

    public void delete(int id) {
        log.debug("Deleting {} id={}", "submission-status", id);
        jdbc.update("DELETE FROM submission_status WHERE id = :id", new MapSqlParameterSource("id", id));
    }
}
