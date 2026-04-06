package com.gaucimaistre.gatekeeping.repository;

import com.gaucimaistre.gatekeeping.mapper.SubmissionReasonRowMapper;
import com.gaucimaistre.gatekeeping.model.SubmissionReason;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class SubmissionReasonRepository {

    private final NamedParameterJdbcTemplate jdbc;
    private final SubmissionReasonRowMapper rowMapper;

    public List<SubmissionReason> findAll() {
        return jdbc.query("SELECT id, name FROM submission_reason ORDER BY id", rowMapper);
    }

    public Optional<SubmissionReason> findById(int id) {
        String sql = "SELECT id, name FROM submission_reason WHERE id = :id";
        Optional<SubmissionReason> result = jdbc.query(sql, new MapSqlParameterSource("id", id), rowMapper)
                .stream().findFirst();
        if (result.isEmpty()) log.debug("SubmissionReason not found with id={}", id);
        return result;
    }
}
