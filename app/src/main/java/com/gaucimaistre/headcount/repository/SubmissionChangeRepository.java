package com.gaucimaistre.headcount.repository;

import com.gaucimaistre.headcount.mapper.SubmissionChangeRowMapper;
import com.gaucimaistre.headcount.model.SubmissionChange;
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
public class SubmissionChangeRepository {

    private final NamedParameterJdbcTemplate jdbc;
    private final SubmissionChangeRowMapper rowMapper;

    public List<SubmissionChange> findBySubmissionId(int submissionId) {
        String sql = """
                SELECT id, submission_id, submitted, field, change
                FROM submission_change
                WHERE submission_id = :submissionId
                ORDER BY submitted ASC
                """;
        return jdbc.query(sql, new MapSqlParameterSource("submissionId", submissionId), rowMapper);
    }

    public Optional<SubmissionChange> findById(int id) {
        String sql = """
                SELECT id, submission_id, submitted, field, change
                FROM submission_change
                WHERE id = :id
                """;
        return jdbc.query(sql, new MapSqlParameterSource("id", id), rowMapper)
                .stream().findFirst();
    }

    public int save(SubmissionChange change) {
        String sql = """
                INSERT INTO submission_change (submission_id, field, change)
                VALUES (:submissionId, :field, :change)
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("submissionId", change.submissionId())
                .addValue("field", change.field())
                .addValue("change", change.change());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sql, params, keyHolder, new String[]{"id"});
        return keyHolder.getKey().intValue();
    }
}
