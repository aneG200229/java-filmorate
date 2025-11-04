package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(Optional.ofNullable(users.get(id))
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден")));
    }

    @Override
    public User create(User user) {
        checkName(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Создан новый пользователь: {}", user);
        return user;
    }

    @Override
    public User update(User newUser) {
        if (newUser.getId() == null) {
            throw new ValidationException("id должен быть введен");
        }
        if (users.containsKey(newUser.getId())) {
            checkName(newUser);
            users.put(newUser.getId(), newUser);
            log.info("Обновлен пользователь с id={}", newUser.getId());
            return newUser;
        }
        log.warn("Попытка обновить несуществующего пользователя с id={}", newUser.getId());
        throw new NotFoundException("User с id = " + newUser.getId() + " не найден");
    }

    @Override
    public void delete(Long id) {
        if (users.remove(id) == null) {
            throw new NotFoundException("Пользователь с id=" + id + " не найден");
        }
        log.info("удален пользователь id={}", id);
    }

    private void checkName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
