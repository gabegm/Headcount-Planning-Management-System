package com.gaucimaistre.headcount.repository;

import com.gaucimaistre.headcount.mapper.UserRowMapper;
import com.gaucimaistre.headcount.model.User;
import com.gaucimaistre.headcount.model.enums.UserType;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final NamedParameterJdbcTemplate jdbc;
    private final UserRowMapper rowMapper;

    public Optional<User> findById(int id) {
        String sql = """
                SELECT id, email, password, password_reset_token, type, active
                FROM "user"
                WHERE id = :id
                """;
        return jdbc.query(sql, new MapSqlParameterSource("id", id), rowMapper)
                .stream().findFirst();
    }

    public Optional<User> findByEmail(String email) {
        String sql = """
                SELECT id, email, password, password_reset_token, type, active
                FROM "user"
                WHERE email = :email
                """;
        return jdbc.query(sql, new MapSqlParameterSource("email", email), rowMapper)
                .stream().findFirst();
    }

    public Optional<User> findByPasswordResetToken(String token) {
        String sql = """
                SELECT id, email, password, password_reset_token, type, active
                FROM "user"
                WHERE password_reset_token = :token
                """;
        return jdbc.query(sql, new MapSqlParameterSource("token", token), rowMapper)
                .stream().findFirst();
    }

    public List<User> findAll() {
        String sql = """
                SELECT id, email, password, password_reset_token, type, active
                FROM "user"
                ORDER BY id
                """;
        return jdbc.query(sql, rowMapper);
    }

    public List<User> findAllActive() {
        String sql = """
                SELECT id, email, password, password_reset_token, type, active
                FROM "user"
                WHERE active = TRUE
                ORDER BY id
                """;
        return jdbc.query(sql, rowMapper);
    }

    public List<User> findAllAdmins() {
        String sql = """
                SELECT id, email, password, password_reset_token, type, active
                FROM "user"
                WHERE type = :type
                ORDER BY id
                """;
        return jdbc.query(sql, new MapSqlParameterSource("type", UserType.ADMIN.name()), rowMapper);
    }

    public int save(User user) {
        String sql = """
                INSERT INTO "user" (email, password, password_reset_token, type, active)
                VALUES (:email, :password, :passwordResetToken, :type, :active)
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("email", user.email())
                .addValue("password", user.password())
                .addValue("passwordResetToken", user.passwordResetToken())
                .addValue("type", user.type().name())
                .addValue("active", user.active());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sql, params, keyHolder, new String[]{"id"});
        return keyHolder.getKey().intValue();
    }

    public void update(User user) {
        String sql = """
                UPDATE "user"
                SET email = :email,
                    password = :password,
                    password_reset_token = :passwordResetToken,
                    type = :type,
                    active = :active
                WHERE id = :id
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", user.id())
                .addValue("email", user.email())
                .addValue("password", user.password())
                .addValue("passwordResetToken", user.passwordResetToken())
                .addValue("type", user.type().name())
                .addValue("active", user.active());
        jdbc.update(sql, params);
    }

    public void updatePassword(int id, String hashedPassword) {
        String sql = """
                UPDATE "user" SET password = :password WHERE id = :id
                """;
        jdbc.update(sql, new MapSqlParameterSource("id", id)
                .addValue("password", hashedPassword));
    }

    public void clearPasswordResetToken(int id) {
        String sql = """
                UPDATE "user" SET password_reset_token = NULL WHERE id = :id
                """;
        jdbc.update(sql, new MapSqlParameterSource("id", id));
    }

    public void setPasswordResetToken(int id, String token) {
        String sql = """
                UPDATE "user" SET password_reset_token = :token WHERE id = :id
                """;
        jdbc.update(sql, new MapSqlParameterSource("id", id).addValue("token", token));
    }

    public List<Integer> findFunctionIdsByUserId(int userId) {
        String sql = """
                SELECT function_id FROM user_function WHERE user_id = :userId
                """;
        return jdbc.queryForList(sql, new MapSqlParameterSource("userId", userId), Integer.class);
    }

    public boolean hasFunctions(int userId) {
        String sql = """
                SELECT COUNT(*) FROM user_function WHERE user_id = :userId
                """;
        Integer count = jdbc.queryForObject(sql, new MapSqlParameterSource("userId", userId), Integer.class);
        return count != null && count > 0;
    }

    public void addFunction(int userId, int functionId) {
        String sql = """
                INSERT INTO user_function (user_id, function_id) VALUES (:userId, :functionId)
                ON CONFLICT DO NOTHING
                """;
        jdbc.update(sql, new MapSqlParameterSource("userId", userId)
                .addValue("functionId", functionId));
    }

    public void removeAllFunctions(int userId) {
        String sql = """
                DELETE FROM user_function WHERE user_id = :userId
                """;
        jdbc.update(sql, new MapSqlParameterSource("userId", userId));
    }
}
