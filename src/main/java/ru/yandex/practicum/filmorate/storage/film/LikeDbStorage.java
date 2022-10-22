package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.filmImpl.FilmStorage;
import ru.yandex.practicum.filmorate.storage.filmImpl.LikeStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Repository("LikeDbStorage")
public class LikeDbStorage implements LikeStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmStorage filmStorage;

    @Autowired
    public LikeDbStorage(JdbcTemplate jdbcTemplate, FilmStorage filmStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmStorage = filmStorage;
    }

    @Override
    public Film likeFilm(Integer filmId, Integer userId) {
        log.debug("LikesDbStorage: запрос к БД от пользователя с userId: {} лайк фильма с filmId: {}.", userId, filmId);
        String sql = "INSERT INTO LIKES (film_id, user_id) " +
                "VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
        updateRate(filmId, true);
        return filmStorage.get(filmId);
    }

    @Override
    public Film dislikeFilm(Integer filmId, Integer userId) {
        log.debug("LikesDbStorage: запрос к БД от пользователя с userId: {} дизлай фильма с filmId: {}.", userId, filmId);
        String sqlDelete = "DELETE FROM LIKES WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlDelete, filmId, userId);
        updateRate(filmId, false);
        return filmStorage.get(filmId);
    }

    @Override
    public List<Film> getMostPopularFilms(Integer count) {
        log.debug("LikesDbStorage: запрос к БД на получение списка самых популярных фильмов размером {}.", count);
        String sql = "SELECT * " +
                "FROM FILMS as f " +
                "LEFT JOIN MPA AS m ON f.mpa_id = m.mpa_id " +
                "ORDER BY film_rate DESC " +
                "LIMIT ?";
        List<Film> result = jdbcTemplate.query(sql, this::mapRowToFilm, count);
        log.debug("LikesDbStorage: список самых популярных фильмов длиной {} при запросе списка длиной {}.",
                result.size(), count);
        return result;
    }

    private void updateRate(Integer filmId, boolean isIncrease) {
        log.debug("LikesDbStorage: запрос к БД на обновление поля film_rate с id: {} и параметром isIncrease = {}.",
                filmId, isIncrease);
        String sql;
        if (isIncrease) {
            sql = "UPDATE FILMS " +
                    "SET film_rate = film_rate + 1 "
                    + "WHERE film_id = ?";
        } else {
            sql = "UPDATE FILMS " +
                    "SET film_rate = film_rate - 1 "
                    + "WHERE film_id = ?";
        }
        jdbcTemplate.update(sql, filmId);
    }

    @Override
    public void setLike(Film film) {
        log.debug("FilmDbStorage: установлено значение поля likes у фильма с id: {}.", film.getId());
        String sql = "SELECT user_id " +
                "FROM LIKES " +
                "WHERE film_id = ?";
        Set<Integer> likes = new HashSet<>(jdbcTemplate.query(sql, this::mapRowToLike, film.getId()));
        film.getLikes().addAll(likes);
    }

    public Film mapRowToFilm(ResultSet rs, Integer rowNum) throws SQLException {
        log.debug("RowMapper: получен запрос от БД на преобразование в Film.");
        return Film.builder()
                .id(rs.getInt("film_id"))
                .name(rs.getString("film_name"))
                .description(rs.getString("film_description"))
                .releaseDate(rs.getDate("film_release_date").toLocalDate())
                .duration(rs.getLong("film_duration"))
                .rate(rs.getInt("film_rate"))
                .mpa(Mpa.builder().id(rs.getInt("mpa_id"))
                        .name(rs.getString("mpa_rating")).build())
                .build();
    }

    public Integer mapRowToLike(ResultSet rs, Integer rowNum) throws SQLException {
        log.debug("RowMapper: получен запрос от БД на преобразование в лайк.");
        return rs.getInt("user_id");
    }
}
