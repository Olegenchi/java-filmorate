package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public List<Film> findAllFilms() {
        return filmStorage.findAllFilms();
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film getFilmById(Integer filmId) {
        log.debug("Фильм c id: {} получен.", filmId);
        return filmStorage.getFilmById(filmId);
    }

    public Film likeFilm(Integer filmId, Integer userId) {
        Film film = getFilmById(filmId);
        film.getLikes().add(userId);
        log.debug("Пользователь с id: {} поставил лайк фильму с id: {}.", userId, filmId);
        return film;
    }

    public Film dislikeFilm(Integer filmId, Integer userId) {
        Film film = getFilmById(filmId);
        film.getLikes().remove(userId);
        log.debug("Пользователь с id: {} удалил лайк у фильма с id: {}.", userId, filmId);
        return film;
    }

    public List<Film> getMostPopularFilms(Integer count) {
        log.debug("Получен список самых популярных фильмов: {}.", count);
        return filmStorage.findAllFilms().stream()
                .sorted((film1, film2) -> film2.getLikes().size() - film1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }
}
