package ru.yandex.practicum.filmorate.model;


import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    Long id;
    @NotBlank(message = "email не должен быть пустым")
    @Email(message = "Некорректный формат email")
    String email;
    @NotBlank(message = "Login не должен быть пустым")
    @Pattern(regexp = "^\\S*$", message = "Login не должно содержать пробелы")
    String login;
    String name;
    @PastOrPresent(message = "дата рождения не должна быть в будущем")
    LocalDate birthday;

}
