package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FriendDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Optional;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;
    private final FriendDbStorage friendStorage;

    public UserService(@Qualifier("userDbStorage") UserStorage userStorage, FriendDbStorage friendStorage) {
        this.userStorage = userStorage;
        this.friendStorage = friendStorage;
    }

    public Collection<User> getAll() {
        return userStorage.findAll();
    }

    public Optional<User> getById(Long id) {
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
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        userStorage.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Друг с id=" + friendId + " не найден"));
        friendStorage.addFriend(userId, friendId);
        log.info("пользователи с id {} и {} теперь друзья", userId, friendId);
    }

    public Collection<User> getFriends(Long userId) {
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        return friendStorage.getFriends(userId);
    }

    public void removeFriend(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new ValidationException("Нельзя удалить самого себя из друзей");
        }
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        userStorage.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Друг с id=" + friendId + " не найден"));
        friendStorage.removeFriend(userId, friendId);
        log.info("пользователи с id {} и {} больше не друзья", userId, friendId);
    }

    public Collection<User> getCommonFriends(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new ValidationException("Выбрали одинаковых пользователей");
        }
        return friendStorage.getCommonFriends(userId, friendId);
    }

}
