package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class RowMapper {
    public static User mapRowToUser(ResultSet rs, Integer rowNum) throws SQLException {
        log.debug("RowMapper: получен запрос от БД на преобразование в User.");
        return User.builder()
                .id(rs.getInt("user_id"))
                .email(rs.getString("user_email"))
                .login(rs.getString("user_login"))
                .name(rs.getString("user_name"))
                .birthday(rs.getDate("user_birthday").toLocalDate())
                .build();
    }

    public static Integer mapRowToFriendId(ResultSet rs, Integer rowNum) throws SQLException {
        log.debug("RowMapper: получен запрос от БД на преобразование в id друга.");
        return rs.getInt("friend_id");
    }

    public static Integer mapRowToCount(ResultSet rs, Integer rowNum) throws SQLException {
        log.debug("RowMapper: получен запрос от БД на преобразование в количество записей");
        return rs.getInt("count");
    }

    public static Film mapRowToFilm(ResultSet rs, Integer rowNum) throws SQLException {
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

    public static Integer mapRowToLike(ResultSet rs, Integer rowNum) throws SQLException {
        log.debug("RowMapper: получен запрос от БД на преобразование в лайк.");
        return rs.getInt("user_id");
    }

    public static Integer mapRowToLikeId(ResultSet rs, Integer rowNum) throws SQLException {
        log.debug("RowMapper: получен запрос от БД на преобразование в id понравившегося фильма.");
        return rs.getInt("film_id");
    }

    public static String mapRowToMpaRating(ResultSet rs, Integer rowNum) throws SQLException {
        log.debug("RowMapper: получен запрос от БД на преобразование в количество лайков.");
        return rs.getString("mpa_rating");
    }

    public static Mpa mapRowToMpa(ResultSet rs, Integer rowNum) throws SQLException {
        log.debug("RowMapper: получен запрос от БД на преобразование в рейтинг Mpa.");
        return Mpa.builder()
                .id(rs.getInt("mpa_id"))
                .name(rs.getString("mpa_rating"))
                .build();
    }

    public static Genre mapRowToGenre(ResultSet rs, Integer rowNum) throws SQLException {
        log.debug("RowMapper: получен запрос от БД на преобразование в Жанр.");
        return Genre.builder()
                .id(rs.getInt("genre_id"))
                .name(rs.getString("genre"))
                .build();
    }
}
