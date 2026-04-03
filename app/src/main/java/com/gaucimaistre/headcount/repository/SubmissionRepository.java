package com.gaucimaistre.headcount.repository;

import com.gaucimaistre.headcount.mapper.SubmissionRowMapper;
import com.gaucimaistre.headcount.model.Submission;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SubmissionRepository {

    private final NamedParameterJdbcTemplate jdbc;
    private final SubmissionRowMapper rowMapper;

    private static final String SELECT_ALL_COLUMNS = """
            SELECT id, submitter_id, gatekeeping_id, position_id, status_id,
                   reason_id, rationale, effective_date, submitted, comment
            FROM submission
            """;

    public Optional<Submission> findById(int id) {
        String sql = SELECT_ALL_COLUMNS + "WHERE id = :id";
        return jdbc.query(sql, new MapSqlParameterSource("id", id), rowMapper)
                .stream().findFirst();
    }

    public List<Submission> findAll() {
        return jdbc.query(SELECT_ALL_COLUMNS + "ORDER BY submitted DESC", rowMapper);
    }

    public List<Submission> findBySubmitterId(int submitterId) {
        String sql = SELECT_ALL_COLUMNS + "WHERE submitter_id = :submitterId ORDER BY submitted DESC";
        return jdbc.query(sql, new MapSqlParameterSource("submitterId", submitterId), rowMapper);
    }

    public List<Submission> findEffectiveDateToday() {
        String sql = SELECT_ALL_COLUMNS + "WHERE effective_date = :today ORDER BY id";
        return jdbc.query(sql, new MapSqlParameterSource("today", LocalDate.now()), rowMapper);
    }

    public int save(Submission submission) {
        String sql = """
                INSERT INTO submission (
                    submitter_id, gatekeeping_id, position_id, status_id,
                    reason_id, rationale, effective_date, comment
                ) VALUES (
                    :submitterId, :gatekeepingId, :positionId, :statusId,
                    :reasonId, :rationale, :effectiveDate, :comment
                )
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("submitterId", submission.submitterId())
                .addValue("gatekeepingId", submission.gatekeepingId())
                .addValue("positionId", submission.positionId())
                .addValue("statusId", submission.statusId())
                .addValue("reasonId", submission.reasonId())
                .addValue("rationale", submission.rationale())
                .addValue("effectiveDate", submission.effectiveDate())
                .addValue("comment", submission.comment());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sql, params, keyHolder, new String[]{"id"});
        return keyHolder.getKey().intValue();
    }

    public void updateStatus(int id, int statusId, String comment) {
        String sql = """
                UPDATE submission SET status_id = :statusId, comment = :comment WHERE id = :id
                """;
        jdbc.update(sql, new MapSqlParameterSource("id", id)
                .addValue("statusId", statusId)
                .addValue("comment", comment));
    }

    public void delete(int id) {
        jdbc.update("DELETE FROM submission WHERE id = :id", new MapSqlParameterSource("id", id));
    }
}
