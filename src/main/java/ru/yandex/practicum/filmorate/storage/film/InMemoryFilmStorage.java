package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private static final LocalDate MIN_DATE = LocalDate.of(1895, 12, 28);
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Optional<Film> findById(Long id) {
        return Optional.ofNullable(Optional.ofNullable(films.get(id))
                .orElseThrow(() -> new NotFoundException("фильм с id= " + id + " не найден")));
    }

    @Override
    public Film create(Film film) {
        checkDate(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Создан новый фильм: {}", film);
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        if (newFilm.getId() == null) {
            throw new ValidationException("id должен быть введен");
        }
        if (films.containsKey(newFilm.getId())) {
            checkDate(newFilm);
            films.put(newFilm.getId(), newFilm);
            log.info("Обновлен фильм с id={}", newFilm.getId());
            return newFilm;
        }
        log.warn("Попытка обновить несуществующий фильм с id={}", newFilm.getId());
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    @Override
    public void delete(Long id) {
        if (films.remove(id) == null) {
            throw new NotFoundException("фильм с id= " + id + " не найден");
        }
        log.info("удален фильм id={}", id);
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void checkDate(Film film) {
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(MIN_DATE)) {
            log.warn("Ошибка валидации даты релиза: {}", film.getReleaseDate());
            throw new ValidationException("дата выхода фильма - не раньше 28 декабря 1895 года");
        }
    }
}
