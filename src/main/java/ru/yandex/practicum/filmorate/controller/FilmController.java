package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import jakarta.validation.Valid;
import ru.yandex.practicum.filmorate.model.Film;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private static final LocalDate MIN_DATE = LocalDate.of(1895, 12, 28);
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> allFilms() {
        return films.values();
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        checkDate(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Создан новый фильм: {}", film);
        return film;

    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film newFilm) {

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
