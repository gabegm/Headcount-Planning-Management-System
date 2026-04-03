package com.gaucimaistre.headcount.repository;

import com.gaucimaistre.headcount.mapper.SubmissionReasonRowMapper;
import com.gaucimaistre.headcount.model.SubmissionReason;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
        return jdbc.query(sql, new MapSqlParameterSource("id", id), rowMapper)
                .stream().findFirst();
    }
}
