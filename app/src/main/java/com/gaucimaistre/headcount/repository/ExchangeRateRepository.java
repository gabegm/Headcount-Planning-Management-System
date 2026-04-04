package com.gaucimaistre.headcount.repository;

import com.gaucimaistre.headcount.mapper.ExchangeRateRowMapper;
import com.gaucimaistre.headcount.model.ExchangeRate;
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
public class ExchangeRateRepository {

    private final NamedParameterJdbcTemplate jdbc;
    private final ExchangeRateRowMapper rowMapper;

    public List<ExchangeRate> findAll() {
        return jdbc.query("SELECT id, name, code, rate FROM exchange_rate ORDER BY name", rowMapper);
    }

    public Optional<ExchangeRate> findById(int id) {
        String sql = "SELECT id, name, code, rate FROM exchange_rate WHERE id = :id";
        Optional<ExchangeRate> result = jdbc.query(sql, new MapSqlParameterSource("id", id), rowMapper)
                .stream().findFirst();
        if (result.isEmpty()) log.debug("ExchangeRate not found with id={}", id);
        return result;
    }

    public int save(ExchangeRate exchangeRate) {
        log.debug("Saving {}: {}", "exchange-rate", exchangeRate.code());
        String sql = """
                INSERT INTO exchange_rate (name, code, rate) VALUES (:name, :code, :rate)
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", exchangeRate.name())
                .addValue("code", exchangeRate.code())
                .addValue("rate", exchangeRate.rate());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sql, params, keyHolder, new String[]{"id"});
        return keyHolder.getKey().intValue();
    }

    public void update(ExchangeRate exchangeRate) {
        log.debug("Updating {} id={}", "exchange-rate", exchangeRate.id());
        String sql = """
                UPDATE exchange_rate SET name = :name, code = :code, rate = :rate WHERE id = :id
                """;
        jdbc.update(sql, new MapSqlParameterSource()
                .addValue("id", exchangeRate.id())
                .addValue("name", exchangeRate.name())
                .addValue("code", exchangeRate.code())
                .addValue("rate", exchangeRate.rate()));
    }

    public void delete(int id) {
        log.debug("Deleting {} id={}", "exchange-rate", id);
        jdbc.update("DELETE FROM exchange_rate WHERE id = :id", new MapSqlParameterSource("id", id));
    }
}
