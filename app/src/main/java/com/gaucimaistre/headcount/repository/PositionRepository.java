package com.gaucimaistre.headcount.repository;

import com.gaucimaistre.headcount.mapper.PositionRowMapper;
import com.gaucimaistre.headcount.mapper.PositionViewRowMapper;
import com.gaucimaistre.headcount.model.Position;
import com.gaucimaistre.headcount.model.PositionView;
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
public class PositionRepository {

    private final NamedParameterJdbcTemplate jdbc;
    private final PositionRowMapper rowMapper;
    private final PositionViewRowMapper viewRowMapper;

    private static final String SELECT_ALL_COLUMNS = """
            SELECT id, status_id, recruitment_status_id, number, pillar_id, company_id,
                   department_id, function_id, is_budget, title, functional_reporting_line,
                   disciplinary_reporting_line, holder, hours, start_date, end_date,
                   salary, fringe_benefit, social_security_contribution,
                   performance_bonus, super_bonus, management_bonus
            FROM position
            """;

    private static final String SELECT_VIEW_COLUMNS = """
            SELECT p.id, ps.name AS status_name, rs.name AS recruitment_status_name,
                   p.number, pi.name AS pillar_name, c.name AS company_name,
                   d.name AS department_name, f.name AS function_name,
                   p.is_budget, p.title, p.functional_reporting_line,
                   p.disciplinary_reporting_line, p.holder, p.hours,
                   p.start_date, p.end_date, p.salary, p.fringe_benefit,
                   p.social_security_contribution, p.performance_bonus,
                   p.super_bonus, p.management_bonus
            FROM position p
            LEFT JOIN position_status ps ON p.status_id = ps.id
            LEFT JOIN recruitment_status rs ON p.recruitment_status_id = rs.id
            LEFT JOIN pillar pi ON p.pillar_id = pi.id
            LEFT JOIN company c ON p.company_id = c.id
            LEFT JOIN department d ON p.department_id = d.id
            LEFT JOIN "function" f ON p.function_id = f.id
            """;

    public Optional<Position> findById(int id) {
        String sql = SELECT_ALL_COLUMNS + "WHERE id = :id";
        Optional<Position> result = jdbc.query(sql, new MapSqlParameterSource("id", id), rowMapper)
                .stream().findFirst();
        if (result.isEmpty()) log.debug("Position not found with id={}", id);
        return result;
    }

    public Optional<Position> findByNumber(String number, boolean isBudget) {
        String sql = SELECT_ALL_COLUMNS + "WHERE number = :number AND is_budget = :isBudget";
        return jdbc.query(sql, new MapSqlParameterSource("number", number)
                        .addValue("isBudget", isBudget), rowMapper)
                .stream().findFirst();
    }

    public List<Position> findAll() {
        return jdbc.query(SELECT_ALL_COLUMNS + "ORDER BY id", rowMapper);
    }

    public List<PositionView> findAllViews() {
        return jdbc.query(SELECT_VIEW_COLUMNS + "WHERE p.is_budget = FALSE ORDER BY p.id",
                new MapSqlParameterSource(), viewRowMapper);
    }

    public List<PositionView> findBudgetViews() {
        return jdbc.query(SELECT_VIEW_COLUMNS + "WHERE p.is_budget = TRUE ORDER BY p.id",
                new MapSqlParameterSource(), viewRowMapper);
    }

    public List<Position> findAllByFunctionIds(List<Integer> functionIds) {
        if (functionIds == null || functionIds.isEmpty()) {
            return List.of();
        }
        String sql = SELECT_ALL_COLUMNS + "WHERE function_id IN (:functionIds) ORDER BY id";
        return jdbc.query(sql, new MapSqlParameterSource("functionIds", functionIds), rowMapper);
    }

    public List<PositionView> findAllViewsByFunctionIds(List<Integer> functionIds) {
        if (functionIds == null || functionIds.isEmpty()) {
            return List.of();
        }
        String sql = SELECT_VIEW_COLUMNS + "WHERE p.is_budget = FALSE AND p.function_id IN (:functionIds) ORDER BY p.id";
        return jdbc.query(sql, new MapSqlParameterSource("functionIds", functionIds), viewRowMapper);
    }

    public List<Position> findBudget() {
        String sql = SELECT_ALL_COLUMNS + "WHERE is_budget = TRUE ORDER BY id";
        return jdbc.query(sql, rowMapper);
    }

    public int save(Position position) {
        log.debug("Saving {}: {}", "position", position.number());
        String sql = """
                INSERT INTO position (
                    status_id, recruitment_status_id, number, pillar_id, company_id,
                    department_id, function_id, is_budget, title, functional_reporting_line,
                    disciplinary_reporting_line, holder, hours, start_date, end_date,
                    salary, fringe_benefit, social_security_contribution,
                    performance_bonus, super_bonus, management_bonus
                ) VALUES (
                    :statusId, :recruitmentStatusId, :number, :pillarId, :companyId,
                    :departmentId, :functionId, :isBudget, :title, :functionalReportingLine,
                    :disciplinaryReportingLine, :holder, :hours, :startDate, :endDate,
                    :salary, :fringeBenefit, :socialSecurityContribution,
                    :performanceBonus, :superBonus, :managementBonus
                )
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sql, positionParams(position), keyHolder, new String[]{"id"});
        return keyHolder.getKey().intValue();
    }

    public void update(Position position) {
        log.debug("Updating {} id={}", "position", position.id());
        String sql = """
                UPDATE position SET
                    status_id = :statusId,
                    recruitment_status_id = :recruitmentStatusId,
                    number = :number,
                    pillar_id = :pillarId,
                    company_id = :companyId,
                    department_id = :departmentId,
                    function_id = :functionId,
                    is_budget = :isBudget,
                    title = :title,
                    functional_reporting_line = :functionalReportingLine,
                    disciplinary_reporting_line = :disciplinaryReportingLine,
                    holder = :holder,
                    hours = :hours,
                    start_date = :startDate,
                    end_date = :endDate,
                    salary = :salary,
                    fringe_benefit = :fringeBenefit,
                    social_security_contribution = :socialSecurityContribution,
                    performance_bonus = :performanceBonus,
                    super_bonus = :superBonus,
                    management_bonus = :managementBonus
                WHERE id = :id
                """;
        jdbc.update(sql, positionParams(position).addValue("id", position.id()));
    }

    public void delete(int id) {
        log.debug("Deleting {} id={}", "position", id);
        jdbc.update("DELETE FROM position WHERE id = :id", new MapSqlParameterSource("id", id));
    }

    private MapSqlParameterSource positionParams(Position p) {
        return new MapSqlParameterSource()
                .addValue("statusId", p.statusId())
                .addValue("recruitmentStatusId", p.recruitmentStatusId())
                .addValue("number", p.number())
                .addValue("pillarId", p.pillarId())
                .addValue("companyId", p.companyId())
                .addValue("departmentId", p.departmentId())
                .addValue("functionId", p.functionId())
                .addValue("isBudget", p.isBudget())
                .addValue("title", p.title())
                .addValue("functionalReportingLine", p.functionalReportingLine())
                .addValue("disciplinaryReportingLine", p.disciplinaryReportingLine())
                .addValue("holder", p.holder())
                .addValue("hours", p.hours())
                .addValue("startDate", p.startDate())
                .addValue("endDate", p.endDate())
                .addValue("salary", p.salary())
                .addValue("fringeBenefit", p.fringeBenefit())
                .addValue("socialSecurityContribution", p.socialSecurityContribution())
                .addValue("performanceBonus", p.performanceBonus())
                .addValue("superBonus", p.superBonus())
                .addValue("managementBonus", p.managementBonus());
    }
}
