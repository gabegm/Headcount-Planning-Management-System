package com.gaucimaistre.headcount.repository;

import com.gaucimaistre.headcount.mapper.DepartmentRowMapper;
import com.gaucimaistre.headcount.model.Department;
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
public class DepartmentRepository {

    private final NamedParameterJdbcTemplate jdbc;
    private final DepartmentRowMapper rowMapper;

    public List<Department> findAll() {
        return jdbc.query("SELECT id, name FROM department ORDER BY name", rowMapper);
    }

    public Optional<Department> findById(int id) {
        String sql = "SELECT id, name FROM department WHERE id = :id";
        return jdbc.query(sql, new MapSqlParameterSource("id", id), rowMapper)
                .stream().findFirst();
    }

    public int save(Department department) {
        String sql = "INSERT INTO department (name) VALUES (:name)";
        MapSqlParameterSource params = new MapSqlParameterSource("name", department.name());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sql, params, keyHolder, new String[]{"id"});
        return keyHolder.getKey().intValue();
    }

    public void update(Department department) {
        jdbc.update("UPDATE department SET name = :name WHERE id = :id",
                new MapSqlParameterSource("id", department.id()).addValue("name", department.name()));
    }

    public void delete(int id) {
        jdbc.update("DELETE FROM department WHERE id = :id", new MapSqlParameterSource("id", id));
    }
}
