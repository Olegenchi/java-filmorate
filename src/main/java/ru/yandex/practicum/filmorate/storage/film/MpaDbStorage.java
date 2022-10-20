package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.RowMapper;

import java.util.List;

@Slf4j
@Repository("MpaDbStorage")
public class MpaDbStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Mpa> findAll() {
        log.debug("MpaDbStorage: запрос к БД на получение всех рейтингов.");
        String sql = "SELECT * " +
                "FROM MPA ";
        return jdbcTemplate.query(sql, RowMapper::mapRowToMpa);
    }

    public Mpa get(Integer mpaId) {
        log.debug("MpaDbStorage: запрос к БД на получение рейтинга с id: {}.", mpaId);
        String sql = "SELECT mpa_id, mpa_rating " +
                "FROM MPA " +
                "WHERE mpa_id = ?";
        return jdbcTemplate.queryForObject(sql, RowMapper::mapRowToMpa, mpaId);
    }

    public boolean validateDataExists(Integer mpaId) {
        log.debug("MpaDbStorage: запрос сервиса на проверку наличия рейтинга с id: {} в БД.", mpaId);
        String sql = "SELECT COUNT(*) AS count " +
                "FROM MPA " +
                "WHERE mpa_id = ?";
        int count = jdbcTemplate.queryForObject(sql, RowMapper::mapRowToCount, mpaId);
        return count != 0;
    }
}
