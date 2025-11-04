package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GenreDbStorage {
    private final JdbcTemplate jdbc;
    private final GenreRowMapper mapper;

    public List<Genre> findAll() {
        String query = "SELECT * FROM film_genre ORDER BY genre_id";
        return jdbc.query(query, mapper);
    }

    public Optional<Genre> findById(int id) {
        String query = "SELECT * FROM film_genre WHERE genre_id = ?";
        try {
            Genre genre = jdbc.queryForObject(query, mapper, id);
            return Optional.ofNullable(genre);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }

    }

    public List<Genre> findByFilmId(long filmId) {
        String query = """
                SELECT g.*
                FROM film_genre g
                INNER JOIN film_genres fg ON g.genre_id = fg.genre_id
                WHERE fg.film_id = ?
                ORDER BY g.genre_id
                """;
        return jdbc.query(query, mapper, filmId);
    }

    public void addGenreToFilm(long filmId, int genreId) {
        String sql = """
                INSERT INTO film_genres (film_id, genre_id)
                SELECT ?, ?
                WHERE NOT EXISTS (
                    SELECT 1 FROM film_genres WHERE film_id = ? AND genre_id = ?
                )
                """;
        jdbc.update(sql, filmId, genreId, filmId, genreId);
    }

    public void removeAllGenresFromFilm(long filmId) {
        String query = "DELETE FROM film_genres WHERE film_id = ?";
        jdbc.update(query, filmId);
    }
}
