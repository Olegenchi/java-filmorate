package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.filmImpl.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Repository("GenreDbStorage")
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public List<Genre> findAll() {
        log.debug("GenreDbStorage: получить все жанры из БД.");
        String sql = "SELECT * " +
                "FROM GENRES ";
        return jdbcTemplate.query(sql, this::mapRowToGenre);
    }


    @Override
    public Genre get(Integer genreId) {
        log.debug("GenreDbStorage: получить жанр с id: {}.", genreId);
        String sql = "SELECT genre_id, genre " +
                "FROM GENRES " +
                "WHERE genre_id = ?";
        return jdbcTemplate.queryForObject(sql, this::mapRowToGenre, genreId);
    }

    @Override
    public boolean validateDataExists(Integer genreId) {
        log.debug("GenreDbStorage: проверка наличия жанра с id: {} в БД.", genreId);
        String sql = "SELECT COUNT(*) AS count " +
                "FROM GENRES " +
                "WHERE genre_id = ?";
        int count = jdbcTemplate.queryForObject(sql, this::mapRowToCount, genreId);
        return count != 0;
    }

    public Integer mapRowToCount(ResultSet rs, Integer rowNum) throws SQLException {
        log.debug("RowMapper: получен запрос от БД на преобразование в количество записей");
        return rs.getInt("count");
    }

    public Genre mapRowToGenre(ResultSet rs, Integer rowNum) throws SQLException {
        log.debug("RowMapper: получен запрос от БД на преобразование в Жанр.");
        return Genre.builder()
                .id(rs.getInt("genre_id"))
                .name(rs.getString("genre"))
                .build();
    }
}
