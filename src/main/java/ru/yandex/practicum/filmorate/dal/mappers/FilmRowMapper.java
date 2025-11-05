package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("film_id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));

        Date sqlDate = rs.getDate("release_date");
        if (sqlDate != null) {
            film.setReleaseDate(sqlDate.toLocalDate());
        }

        film.setDuration(rs.getInt("duration"));

        int ratingId = rs.getInt("rating_id");
        if (!rs.wasNull()) {
            Mpa mpa = new Mpa();
            mpa.setId(ratingId);

            try {
                String ratingName = rs.getString("rating");
                if (ratingName != null) {
                    mpa.setName(ratingName);
                }
            } catch (SQLException e) {
            }

            film.setMpa(mpa);
        }

        return film;
    }
}