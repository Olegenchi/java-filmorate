package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.filmImpl.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Repository("MpaDbStorage")
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> findAll() {
        log.debug("MpaDbStorage: запрос к БД на получение всех рейтингов.");
        String sql = "SELECT * " +
                "FROM MPA ";
        return jdbcTemplate.query(sql, this::mapRowToMpa);
    }

    @Override
    public Mpa get(Integer mpaId) {
        log.debug("MpaDbStorage: запрос к БД на получение рейтинга с id: {}.", mpaId);
        String sql = "SELECT mpa_id, mpa_rating " +
                "FROM MPA " +
                "WHERE mpa_id = ?";
        return jdbcTemplate.queryForObject(sql, this::mapRowToMpa, mpaId);
    }

    @Override
    public boolean validateDataExists(Integer mpaId) {
        log.debug("MpaDbStorage: запрос сервиса на проверку наличия рейтинга с id: {} в БД.", mpaId);
        String sql = "SELECT COUNT(*) AS count " +
                "FROM MPA " +
                "WHERE mpa_id = ?";
        int count = jdbcTemplate.queryForObject(sql, this::mapRowToCount, mpaId);
        return count != 0;
    }

    public Mpa mapRowToMpa(ResultSet rs, Integer rowNum) throws SQLException {
        log.debug("RowMapper: получен запрос от БД на преобразование в рейтинг Mpa.");
        return Mpa.builder()
                .id(rs.getInt("mpa_id"))
                .name(rs.getString("mpa_rating"))
                .build();
    }

    public Integer mapRowToCount(ResultSet rs, Integer rowNum) throws SQLException {
        log.debug("RowMapper: получен запрос от БД на преобразование в количество записей");
        return rs.getInt("count");
    }
}
