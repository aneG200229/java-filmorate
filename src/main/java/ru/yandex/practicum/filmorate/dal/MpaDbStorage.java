package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MpaDbStorage {
    private final JdbcTemplate jdbc;
    private final MpaRowMapper mapper;

    public List<Mpa> findAll() {
        String query = "SELECT * FROM film_rating ORDER BY rating_id";
        return jdbc.query(query, mapper);
    }

    public Optional<Mpa> findById(int id) {
        String query = "SELECT * FROM film_rating WHERE rating_id = ?";
        try {
            Mpa mpa = jdbc.queryForObject(query, mapper, id);
            return Optional.ofNullable(mpa);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
