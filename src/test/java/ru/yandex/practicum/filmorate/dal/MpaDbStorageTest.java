package ru.yandex.practicum.filmorate.dal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({MpaDbStorage.class, MpaRowMapper.class})
class MpaDbStorageTest {

    @Autowired
    private MpaDbStorage mpaStorage;

    @Test
    void testFindAll() {
        List<Mpa> ratings = mpaStorage.findAll();
        assertThat(ratings).hasSize(5);  // Должно быть ровно 5 рейтингов
    }

    @Test
    void testFindById() {
        Optional<Mpa> mpa = mpaStorage.findById(1);
        assertThat(mpa).isPresent();
        assertThat(mpa.get().getName()).isEqualTo("G");
    }

    @Test
    void testFindByIdNotFound() {
        Optional<Mpa> mpa = mpaStorage.findById(999);
        assertThat(mpa).isEmpty();
    }
}