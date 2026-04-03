package com.gaucimaistre.headcount.repository;

import com.gaucimaistre.headcount.mapper.SubmissionStatusRowMapper;
import com.gaucimaistre.headcount.model.SubmissionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
        return jdbc.query(sql, new MapSqlParameterSource("id", id), rowMapper)
                .stream().findFirst();
    }
}
