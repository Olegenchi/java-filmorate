package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.RowMapper;

import java.util.List;

@Slf4j
@Repository("GenreDbStorage")
public class GenreDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Genre> findAll() {
        log.debug("GenreDbStorage: получить все жанры из БД.");
        String sql = "SELECT * " +
                "FROM GENRES ";
        return jdbcTemplate.query(sql, RowMapper::mapRowToGenre);
    }

    public Genre get(Integer genreId) {
        log.debug("GenreDbStorage: получить жанр с id: {}.", genreId);
        String sql = "SELECT genre_id, genre " +
                "FROM GENRES " +
                "WHERE genre_id = ?";
        return jdbcTemplate.queryForObject(sql, RowMapper::mapRowToGenre, genreId);
    }

    public boolean validateDataExists(Integer genreId) {
        log.debug("GenreDbStorage: проверка наличия жанра с id: {} в БД.", genreId);
        String sql = "SELECT COUNT(*) AS count " +
                "FROM GENRES " +
                "WHERE genre_id = ?";
        int count = jdbcTemplate.queryForObject(sql, RowMapper::mapRowToCount, genreId);
        return count != 0;
    }
}
