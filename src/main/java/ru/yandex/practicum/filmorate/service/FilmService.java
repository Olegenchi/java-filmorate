package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.util.List;

@Slf4j
@Service
public class FilmService {
    private final FilmDbStorage filmDbStorage;

    @Autowired
    public FilmService(FilmDbStorage filmDbStorage) {
        this.filmDbStorage = filmDbStorage;
    }

    public List<Film> findAllFilms() {
        return filmDbStorage.findAll();
    }

    public Film createFilm(Film film) {
        return filmDbStorage.create(film);
    }

    public Film updateFilm(Film film) {
        log.debug("FilmService: фильм c id: {} обновлен.", film.getId());
        validateDataExists(film.getId());
        return filmDbStorage.update(film);
    }

    public Film getFilmById(Integer filmId) {
        log.debug("FilmService: фильм c id: {} получен.", filmId);
        validateDataExists(filmId);
        return filmDbStorage.get(filmId);
    }

    public Film delete(Integer filmId) {
        log.debug("FilmService: запрос на удаление фильма с id: {}.", filmId);
        validateDataExists(filmId);
        return filmDbStorage.delete(filmId);
    }

    public void validateDataExists(Integer filmId) {
        log.debug("FilmService: запрос на проверку наличия фильма с id: {} в БД.", filmId);
        if (!filmDbStorage.validateDataExists(filmId)) {
            String message = "Фильм c таким id не существует.";
            throw new FilmValidationException(message);
        }
    }
}
