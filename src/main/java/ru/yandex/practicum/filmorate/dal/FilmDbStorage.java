package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Qualifier("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbc;
    private final FilmRowMapper mapper;
    private final GenreDbStorage genreStorage;
    private final MpaDbStorage mpaStorage;
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

    @Override
    public Collection<Film> findAll() {
        String query = "SELECT * FROM films";
        List<Film> films = jdbc.query(query, mapper);

        for (Film film : films) {
            loadFullData(film);
        }

        return films;
    }

    @Override
    public Optional<Film> findById(Long id) {
        String query = "SELECT * FROM films WHERE film_id = ?";

        try {
            Film film = jdbc.queryForObject(query, mapper, id);

            if (film == null) {
                return Optional.empty();
            }

            loadFullData(film);

            return Optional.of(film);

        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Film create(Film film) {
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(CINEMA_BIRTHDAY)) {
            throw new ValidationException("Дата релиза не может быть раньше 28.12.1895");
        }
        if (film.getMpa() == null || film.getMpa().getId() == null) {
            throw new ValidationException("Рейтинг MPA обязателен");
        }

        mpaStorage.findById(film.getMpa().getId())
                .orElseThrow(() -> new NotFoundException("Рейтинг с id=" + film.getMpa().getId() + " не найден"));

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                genreStorage.findById(genre.getId())
                        .orElseThrow(() -> new NotFoundException("Жанр с id=" + genre.getId() + " не найден"));
            }
        }

        String query = """
                INSERT INTO films (name, description, release_date, duration, rating_id) 
                VALUES (?, ?, ?, ?, ?)
                """;

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);


        Number key = keyHolder.getKey();
        Long filmId = null;
        if (key != null) {
            filmId = key.longValue();
        }

        if (filmId == null) {
            throw new InternalServerException("Не удалось создать фильм: сгенерированный ключ отсутствует");
        }
        film.setId(filmId);


        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                genreStorage.addGenreToFilm(filmId, genre.getId());
            }
        }


        loadFullData(film);

        return film;
    }

    @Override
    public Film update(Film film) {
        findById(film.getId())
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + film.getId() + " не найден"));

        if (film.getMpa() != null && film.getMpa().getId() != null) {
            mpaStorage.findById(film.getMpa().getId())
                    .orElseThrow(() -> new NotFoundException("Рейтинг с id=" + film.getMpa().getId() + " не найден"));
        }

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                genreStorage.findById(genre.getId())
                        .orElseThrow(() -> new NotFoundException("Жанр с id=" + genre.getId() + " не найден"));
            }
        }

        String query = """
                UPDATE films 
                SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? 
                WHERE film_id = ?
                """;

        jdbc.update(query,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );

        genreStorage.removeAllGenresFromFilm(film.getId());

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                genreStorage.addGenreToFilm(film.getId(), genre.getId());
            }
        }

        loadFullData(film);

        return film;
    }

    @Override
    public void delete(Long id) {
        String query = "DELETE FROM films WHERE film_id = ?";
        int rowsDeleted = jdbc.update(query, id);
        if (rowsDeleted == 0) {
            throw new NotFoundException("Фильм с id=" + id + " не найден");
        }
    }

    private void loadFullData(Film film) {

        if (film.getMpa() != null && film.getMpa().getId() != null) {
            mpaStorage.findById(film.getMpa().getId())
                    .ifPresent(film::setMpa);
        }


        List<Genre> genres = genreStorage.findByFilmId(film.getId());
        film.setGenres(genres);
    }
}