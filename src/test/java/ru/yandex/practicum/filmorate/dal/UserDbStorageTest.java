package ru.yandex.practicum.filmorate.dal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({UserDbStorage.class, UserRowMapper.class})
class UserDbStorageTest {

    @Autowired
    private UserDbStorage userStorage;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setLogin("testlogin");
        testUser.setName("Test User");
        testUser.setBirthday(LocalDate.of(1990, 1, 1));
    }

    @Test
    void testCreateUser() {
        User created = userStorage.create(testUser);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getEmail()).isEqualTo(testUser.getEmail());
        assertThat(created.getLogin()).isEqualTo(testUser.getLogin());
        assertThat(created.getName()).isEqualTo(testUser.getName());
        assertThat(created.getBirthday()).isEqualTo(testUser.getBirthday());
    }

    @Test
    void testFindById() {
        User created = userStorage.create(testUser);

        Optional<User> found = userStorage.findById(created.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo(testUser.getEmail());
        assertThat(found.get().getLogin()).isEqualTo(testUser.getLogin());
    }

    @Test
    void testFindAll() {
        userStorage.create(testUser);

        Collection<User> users = userStorage.findAll();

        assertThat(users).isNotEmpty();
    }

    @Test
    void testUpdateUser() {
        User created = userStorage.create(testUser);

        created.setName("Updated Name");
        created.setEmail("updated@example.com");
        created.setLogin("updatedLogin");
        created.setBirthday(LocalDate.of(1991, 2, 2));

        User updated = userStorage.update(created);

        assertThat(updated.getId()).isEqualTo(created.getId());
        assertThat(updated.getName()).isEqualTo("Updated Name");
        assertThat(updated.getEmail()).isEqualTo("updated@example.com");
        assertThat(updated.getLogin()).isEqualTo("updatedLogin");
        assertThat(updated.getBirthday()).isEqualTo(LocalDate.of(1991, 2, 2));
    }

    @Test
    void testDeleteUser() {
        User created = userStorage.create(testUser);
        Long id = created.getId();

        userStorage.delete(id);

        Optional<User> maybe = userStorage.findById(id);
        assertThat(maybe).isEmpty();
    }
}
