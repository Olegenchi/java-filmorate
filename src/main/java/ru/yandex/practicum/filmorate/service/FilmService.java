package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.filmImpl.FilmStorage;

import java.util.List;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public List<Film> findAllFilms() {
        log.debug("FilmService: получен список всех фильмов.");
        List<Film> films = filmStorage.findAll();
        films.forEach(filmStorage::setGenre);
        return films;
    }

    public Film createFilm(Film film) {
        log.debug("FilmService: фильм c id: {} добавлен.", film.getId());
        return filmStorage.create(film);
    }

    public Film updateFilm(Film film) {
        log.debug("FilmService: фильм c id: {} обновлен.", film.getId());
        validateFilmExists(film.getId());
        return filmStorage.update(film);
    }

    public Film getFilmById(Integer filmId) {
        log.debug("FilmService: фильм c id: {} получен.", filmId);
        validateFilmExists(filmId);
        return filmStorage.get(filmId);
    }

    public Film delete(Integer filmId) {
        log.debug("FilmService: запрос на удаление фильма с id: {}.", filmId);
        validateFilmExists(filmId);
        return filmStorage.delete(filmId);
    }

    public void validateFilmExists(Integer filmId) {
        log.debug("FilmService: запрос на проверку наличия фильма с id: {} в БД.", filmId);
        if (!filmStorage.validateDataExists(filmId)) {
            String message = "Фильм c таким id не существует.";
            throw new FilmValidationException(message);
        }
    }
}
