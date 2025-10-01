package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> getAll() {
        return userStorage.findAll();
    }

    public User getById(Long id) {
        return userStorage.findById(id);
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User newUser) {
        return userStorage.update(newUser);
    }

    public void addFriend(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new ValidationException("Нельзя добавить самого себя в друзья");
        }
        User user = getById(userId);
        User friend = getById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        log.info("пользователи с id {} и {} теперь друзья", userId, friendId);
    }

    public Collection<User> getFriends(Long userId) {
        User user = getById(userId);

        return user.getFriends().stream()
                .map(this::getById).collect(Collectors.toList());
    }

    public void removeFriend(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new ValidationException("Нельзя удалить самого себя из друзей");
        }
        User user = getById(userId);
        User friend = getById(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        log.info("пользователи с id {} и {} больше не друзья", userId, friendId);
    }

    public Collection<User> getCommonFriends(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new ValidationException("Выбрали одинаковых пользователей");
        }
        User user = getById(userId);
        User friend = getById(friendId);
        return user.getFriends().stream()
                .filter(friend.getFriends()::contains)
                .map(this::getById)
                .collect(Collectors.toList());
    }

}
