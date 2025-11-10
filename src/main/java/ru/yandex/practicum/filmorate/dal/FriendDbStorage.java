package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FriendDbStorage {
    private final JdbcTemplate jdbc;
    private final UserRowMapper mapper;

    public void addFriend(long userId, long friendId) {
        String query = "INSERT INTO friends (user_id, friend_id) VALUES (?, ?)";
        jdbc.update(query, userId, friendId);
    }

    public void removeFriend(long userId, long friendId) {
        String query = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        jdbc.update(query, userId, friendId);
    }

    public List<User> getFriends(long userId) {
        String query = "SELECT u.* FROM users u " +
                "INNER JOIN friends f ON u.user_id = f.friend_id " +
                "WHERE f.user_id = ?";
        return jdbc.query(query, mapper, userId);
    }

    public List<User> getCommonFriends(long userId, long otherId) {
        String query = "SELECT u.* FROM users u\n" +
                "WHERE u.user_id IN (\n" +
                "    SELECT friend_id FROM friends WHERE user_id = ?\n" +
                ")\n" +
                "AND u.user_id IN (\n" +
                "    SELECT friend_id FROM friends WHERE user_id = ?\n" +
                ")";
        return jdbc.query(query, mapper, userId, otherId);
    }
}
