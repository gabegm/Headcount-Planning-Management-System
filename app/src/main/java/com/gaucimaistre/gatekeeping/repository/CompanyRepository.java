package com.gaucimaistre.gatekeeping.repository;

import com.gaucimaistre.gatekeeping.mapper.CompanyRowMapper;
import com.gaucimaistre.gatekeeping.model.Company;
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
public class CompanyRepository {

    private final NamedParameterJdbcTemplate jdbc;
    private final CompanyRowMapper rowMapper;

    public List<Company> findAll() {
        return jdbc.query("SELECT id, exchange_rate_id, name FROM company ORDER BY name", rowMapper);
    }

    public Optional<Company> findById(int id) {
        String sql = "SELECT id, exchange_rate_id, name FROM company WHERE id = :id";
        Optional<Company> result = jdbc.query(sql, new MapSqlParameterSource("id", id), rowMapper)
                .stream().findFirst();
        if (result.isEmpty()) log.debug("Company not found with id={}", id);
        return result;
    }

    public int save(Company company) {
        log.debug("Saving {}: {}", "company", company.name());
        String sql = """
                INSERT INTO company (exchange_rate_id, name) VALUES (:exchangeRateId, :name)
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("exchangeRateId", company.exchangeRateId())
                .addValue("name", company.name());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sql, params, keyHolder, new String[]{"id"});
        return keyHolder.getKey().intValue();
    }

    public void update(Company company) {
        log.debug("Updating {} id={}", "company", company.id());
        String sql = """
                UPDATE company SET exchange_rate_id = :exchangeRateId, name = :name WHERE id = :id
                """;
        jdbc.update(sql, new MapSqlParameterSource()
                .addValue("id", company.id())
                .addValue("exchangeRateId", company.exchangeRateId())
                .addValue("name", company.name()));
    }

    public void delete(int id) {
        log.debug("Deleting {} id={}", "company", id);
        jdbc.update("DELETE FROM company WHERE id = :id", new MapSqlParameterSource("id", id));
    }
}
