package ru.yandex.practicum.filmorate.model;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;


import java.time.LocalDate;

@Data
public class Film {
    Long id;
    @NotBlank(message = "Название фильма не должно быть пустым")
    String name;
    @Size(max = 200, message = "максимальная длина описания — 200 символов")
    String description;
    LocalDate releaseDate;
    @Positive(message = "продолжительность фильма должна быть положительной")
    int duration;
}
