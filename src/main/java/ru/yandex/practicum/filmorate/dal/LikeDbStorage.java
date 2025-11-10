package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LikeDbStorage {
    private final JdbcTemplate jdbc;

    public void addLike(long filmId, long userId) {
        String query = "INSERT INTO user_like (film_id, user_id) VALUES (?, ?)";
        jdbc.update(query, filmId, userId);
    }

    public void removeLike(long filmId, long userId) {
        String query = "DELETE FROM user_like WHERE film_id = ? AND user_id = ?";
        jdbc.update(query, filmId, userId);
    }


    public int getLikesCount(long filmId) {
        String query = "SELECT COUNT(*) FROM user_like WHERE film_id = ?";
        Integer count = jdbc.queryForObject(query, Integer.class, filmId);
        return count != null ? count : 0;
    }
}
