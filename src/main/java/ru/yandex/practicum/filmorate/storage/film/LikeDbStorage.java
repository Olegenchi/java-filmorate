package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.RowMapper;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.List;

@Slf4j
@Repository("LikeDbStorage")
public class LikeDbStorage {
    private final JdbcTemplate jdbcTemplate;
    private final Storage<Film> filmStorage;
    private final FilmDbStorage filmDbStorage;

    @Autowired
    public LikeDbStorage(JdbcTemplate jdbcTemplate, @Qualifier("FilmDbStorage") Storage<Film> filmStorage,
                         FilmDbStorage filmDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmStorage = filmStorage;
        this.filmDbStorage = filmDbStorage;
    }

    public Film likeFilm(Integer filmId, Integer userId) {
        log.debug("LikesDbStorage: запрос к БД от пользователя с userId: {} лайк фильма с filmId: {}.", userId, filmId);
        String sql = "INSERT INTO LIKES (film_id, user_id) " +
                "VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
        updateRate(filmId, true);
        return filmStorage.get(filmId);
    }

    public Film dislikeFilm(Integer filmId, Integer userId) {
        log.debug("LikesDbStorage: запрос к БД от пользователя с userId: {} дизлай фильма с filmId: {}.", userId, filmId);
        String sqlDelete = "DELETE FROM LIKES WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlDelete, filmId, userId);
        updateRate(filmId, false);
        return filmStorage.get(filmId);
    }

    public List<Film> getMostPopularFilms(Integer count) {
        log.debug("LikesDbStorage: запрос к БД на получение списка самых популярных фильмов размером {}.", count);
        String sql = "SELECT * " +
                "FROM FILMS as f " +
                "LEFT JOIN MPA AS m ON f.mpa_id = m.mpa_id " +
                "ORDER BY film_rate DESC " +
                "LIMIT ?";
        List<Film> result = jdbcTemplate.query(sql, RowMapper::mapRowToFilm, count);
        log.debug("LikesDbStorage: список самых популярных фильмов длиной {} при запросе списка длиной {}.",
                result.size(), count);
        return filmDbStorage.setMpaLikesGenre(result);
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
}
