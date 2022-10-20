package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.RowMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Repository("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> findAll() {
        log.debug("FilmDbStorage: запрос к БД на получение всех фильмов.");
        String sql = "SELECT * " +
                "FROM FILMS as f " +
                "LEFT JOIN MPA AS m ON f.mpa_id = m.mpa_id ";
        List<Film> result = jdbcTemplate.query(sql, RowMapper::mapRowToFilm);
        return setMpaLikesGenre(result);
    }

    @Override
    public Film get(Integer filmId) {
        log.debug("FilmDbStorage: запрос к БД на получение фильма с id: {}.", filmId);
        String sql = "SELECT f.film_id, f.film_name, f.film_description, f.film_release_date, f.film_duration, " +
                "f.film_rate, f.mpa_id, m.mpa_rating " +
                "FROM FILMS AS f " +
                "LEFT JOIN MPA AS m ON f.mpa_id = m.mpa_id " +
                "WHERE film_id = ?";
        Film result = jdbcTemplate.queryForObject(sql, RowMapper::mapRowToFilm, filmId);
        assert result != null;
        setLike(result);
        setGenre(result);
        return result;
    }

    @Override
    public Film create(Film film) {
        log.debug("FilmDbStorage: запрос к БД на добавление фильма: {}.", film.getName());
        SimpleJdbcInsert filmJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FILMS")
                .usingGeneratedKeyColumns("film_id");
        Integer filmId = filmJdbcInsert.executeAndReturnKey(film.filmToMap()).intValue();
        film.setId(filmId);
        updateGenre(film);
        return get(filmId);
    }

    @Override
    public Film update(Film film) {
        int filmId = film.getId();
        log.debug("FilmDbStorage: запрос к БД на обновление фильма с id: {}.", filmId);
        String sql = "UPDATE FILMS " +
                "SET film_name = ?, film_description = ?, film_release_date = ?, film_duration = ?, " +
                "film_rate = ?, mpa_id = ? " +
                "WHERE film_id = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRate(),
                film.getMpa().getId(),
                film.getId());
        deleteFilmGenre(filmId);
        updateGenre(film);
        return get(film.getId());
    }

    @Override
    public Film delete(Integer filmId) {
        log.debug("FilmDbStorage: запрос к БД на удаление фильма с id: {}.", filmId);
        String sqlDeleteGenre = "DELETE FROM FILM_GENRES WHERE film_id = ?";
        jdbcTemplate.update(sqlDeleteGenre, filmId);
        String sqlDeleteLike = "DELETE FROM LIKES WHERE film_id = ?";
        jdbcTemplate.update(sqlDeleteLike, filmId);
        String sqlDelete = "DELETE FROM FILMS WHERE film_id = ?";
        Film deletedFilm = get(filmId);
        jdbcTemplate.update(sqlDelete, filmId);
        return deletedFilm;
    }

    private void setMpaName(Film film) {
        log.debug("FilmDbStorage: установлено значение поля name у рейтинга фильма с id: {}.",
                film.getId());
        String sql = "SELECT mpa_rating " +
                "FROM MPA " +
                "WHERE mpa_id = ?";
        String rating = jdbcTemplate.queryForObject(sql, RowMapper::mapRowToRating, film.getMpa().getId());
        film.getMpa().setName(rating);
    }

    private void deleteFilmGenre(Integer filmId) {
        log.debug("FilmDbStorage: запрос на удаление фильма с id: {} в хранилище жанров.", filmId);
        String sqlDeleteGenre = "DELETE FROM FILM_GENRES WHERE film_id = ?";
        jdbcTemplate.update(sqlDeleteGenre, filmId);
    }

    private void updateGenre(Film film) {
        int filmId = film.getId();
        log.debug("FilmDbStorage: запрос на обновление жанров фильма с id: {}.", filmId);
        String sqlAddGenre = "INSERT INTO FILM_GENRES (film_id, genre_id) " +
                "values (?, ?)";

        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(sqlAddGenre, filmId, genre.getId());
        }
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

    @Override
    public boolean validateDataExists(Integer filmId) {
        log.debug("FilmDbStorage: запрос на проверку наличия фильма с id: {} в базе данных фильмов.", filmId);
        String sql = "SELECT COUNT(*) AS count " +
                "FROM FILMS " +
                "WHERE film_id = ?";
        int count = jdbcTemplate.queryForObject(sql, RowMapper::mapRowToCount, filmId);
        return count != 0;
    }
}
