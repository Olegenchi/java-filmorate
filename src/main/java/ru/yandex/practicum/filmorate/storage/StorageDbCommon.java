package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Repository("StorageDbCommon")
public class StorageDbCommon {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public StorageDbCommon(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setFriend(User user) {
        Integer userId = user.getId();
        log.debug("UserDbStorage: запрос на обновление друзей пользователя c id: {}.", userId);
        String sql = "SELECT friend_id " +
                "FROM FRIENDS " +
                "WHERE user_id = ?";
        Set<Integer> friends = new HashSet<>(jdbcTemplate.query(sql, RowMapper::mapRowToFriendId, userId));
        user.getFriends().addAll(friends);
    }

    private void setMpaName(Film film) {
        log.debug("FilmDbStorage: установлено значение поля name у рейтинга фильма с id: {}.",
                film.getId());
        String sql = "SELECT mpa_rating " +
                "FROM MPA " +
                "WHERE mpa_id = ?";
        String rating = jdbcTemplate.queryForObject(sql, RowMapper::mapRowToMpaRating, film.getMpa().getId());
        film.getMpa().setName(rating);
    }

    public void setLike(Film film) {
        log.debug("FilmDbStorage: установлено значение поля likes у фильма с id: {}.", film.getId());
        String sql = "SELECT user_id " +
                "FROM LIKES " +
                "WHERE film_id = ?";
        Set<Integer> likes = new HashSet<>(jdbcTemplate.query(sql, RowMapper::mapRowToLike, film.getId()));
        film.getLikes().addAll(likes);
    }

    public void setGenre(Film film) {
        log.debug("FilmDbStorage: установлено значение поля name у жанров фильма с id: {}.", film.getId());
        String sql = "SELECT fg.genre_id, g.genre " +
                "FROM FILM_GENRES AS fg " +
                "LEFT JOIN GENRES AS g ON fg.genre_id = g.genre_id " +
                "WHERE film_id = ?";
        Set<Genre> genre = new HashSet<>(jdbcTemplate.query(sql, RowMapper::mapRowToGenre, film.getId()));
        film.getGenres().addAll(genre);
    }

    public List<Film> setMpaLikesGenre(List<Film> films) {
        films.forEach(this::setMpaName);
        log.debug("FilmDbStorage: установлены значения поля name у рейтингов полученных фильмов.");
        films.forEach(this::setLike);
        log.debug("FilmDbStorage: установлены значения поля likes у полученных фильмов.");
        films.forEach(this::setGenre);
        log.debug("FilmDbStorage: установлены значения поля name у жанров полученных фильмов.");
        return films;
    }
}
