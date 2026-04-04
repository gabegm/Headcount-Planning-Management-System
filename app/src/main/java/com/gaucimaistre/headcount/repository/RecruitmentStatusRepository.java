package com.gaucimaistre.headcount.repository;

import com.gaucimaistre.headcount.mapper.RecruitmentStatusRowMapper;
import com.gaucimaistre.headcount.model.RecruitmentStatus;
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
public class RecruitmentStatusRepository {

    private final NamedParameterJdbcTemplate jdbc;
    private final RecruitmentStatusRowMapper rowMapper;

    public List<RecruitmentStatus> findAll() {
        return jdbc.query("SELECT id, name FROM recruitment_status ORDER BY id", rowMapper);
    }

    public Optional<RecruitmentStatus> findById(int id) {
        String sql = "SELECT id, name FROM recruitment_status WHERE id = :id";
        Optional<RecruitmentStatus> result = jdbc.query(sql, new MapSqlParameterSource("id", id), rowMapper)
                .stream().findFirst();
        if (result.isEmpty()) log.debug("RecruitmentStatus not found with id={}", id);
        return result;
    }
}
