package com.gaucimaistre.headcount.repository;

import com.gaucimaistre.headcount.AbstractIntegrationTest;
import com.gaucimaistre.headcount.model.User;
import com.gaucimaistre.headcount.model.enums.UserType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class UserRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NamedParameterJdbcTemplate jdbc;

    private static final String ADMIN_EMAIL = "gabriel.gaucimaistre@gaucimaistre.com";

    @Test
    void findByEmail_returnsAdminUser() {
        Optional<User> user = userRepository.findByEmail(ADMIN_EMAIL);
        assertThat(user).isPresent();
        assertThat(user.get().email()).isEqualTo(ADMIN_EMAIL);
        assertThat(user.get().type()).isEqualTo(UserType.ADMIN);
        assertThat(user.get().active()).isTrue();
    }

    @Test
    void findAll_returnsAtLeastOneUser() {
        List<User> users = userRepository.findAll();
        assertThat(users).isNotEmpty();
    }

    @Test
    void save_andFindById() {
        User newUser = new User(0, "test@gaucimaistre.com", "hashed-pw", null, UserType.USER, true);
        int id = userRepository.save(newUser);

        Optional<User> found = userRepository.findById(id);
        assertThat(found).isPresent();
        assertThat(found.get().email()).isEqualTo("test@gaucimaistre.com");
        assertThat(found.get().type()).isEqualTo(UserType.USER);
        assertThat(found.get().active()).isTrue();
    }

    @Test
    void update_changesUserFields() {
        User newUser = new User(0, "update-test@gaucimaistre.com", "hashed-pw", null, UserType.USER, false);
        int id = userRepository.save(newUser);

        User toUpdate = new User(id, "update-test@gaucimaistre.com", "new-hashed-pw", null, UserType.ADMIN, true);
        userRepository.update(toUpdate);

        Optional<User> found = userRepository.findById(id);
        assertThat(found).isPresent();
        assertThat(found.get().password()).isEqualTo("new-hashed-pw");
        assertThat(found.get().type()).isEqualTo(UserType.ADMIN);
        assertThat(found.get().active()).isTrue();
    }

    @Test
    void delete_removesUser() {
        User newUser = new User(0, "delete-test@gaucimaistre.com", "hashed-pw", null, UserType.USER, false);
        int id = userRepository.save(newUser);

        assertThat(userRepository.findById(id)).isPresent();

        jdbc.update("DELETE FROM \"user\" WHERE id = :id", new MapSqlParameterSource("id", id));

        assertThat(userRepository.findById(id)).isEmpty();
    }
}
