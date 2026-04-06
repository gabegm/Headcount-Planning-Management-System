package com.gaucimaistre.gatekeeping.repository;

import com.gaucimaistre.gatekeeping.mapper.DepartmentRowMapper;
import com.gaucimaistre.gatekeeping.model.Department;
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
public class DepartmentRepository {

    private final NamedParameterJdbcTemplate jdbc;
    private final DepartmentRowMapper rowMapper;

    public List<Department> findAll() {
        return jdbc.query("SELECT id, name FROM department ORDER BY name", rowMapper);
    }

    public Optional<Department> findById(int id) {
        String sql = "SELECT id, name FROM department WHERE id = :id";
        Optional<Department> result = jdbc.query(sql, new MapSqlParameterSource("id", id), rowMapper)
                .stream().findFirst();
        if (result.isEmpty()) log.debug("Department not found with id={}", id);
        return result;
    }

    public int save(Department department) {
        log.debug("Saving {}: {}", "department", department.name());
        String sql = "INSERT INTO department (name) VALUES (:name)";
        MapSqlParameterSource params = new MapSqlParameterSource("name", department.name());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sql, params, keyHolder, new String[]{"id"});
        return keyHolder.getKey().intValue();
    }

    public void update(Department department) {
        log.debug("Updating {} id={}", "department", department.id());
        jdbc.update("UPDATE department SET name = :name WHERE id = :id",
                new MapSqlParameterSource("id", department.id()).addValue("name", department.name()));
    }

    public void delete(int id) {
        log.debug("Deleting {} id={}", "department", id);
        jdbc.update("DELETE FROM department WHERE id = :id", new MapSqlParameterSource("id", id));
    }
}
