package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.filmImpl.FilmStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
//@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> allFilms = new HashMap<>();
    private int id = 0;

    private int generateNextId() {
        return ++id;
    }

    public List<Film> findAll() {
        log.debug("InMemoryFilmStorage: текущее количество фильмов: {}", allFilms.size());
        return new ArrayList<>(allFilms.values());
    }

    public Film get(Integer filmId) {
        log.debug("InMemoryFilmStorage: фильм c id: {} получен.", filmId);
        return allFilms.get(filmId);
    }

    public Film create(Film film) {
            film.setId(generateNextId());
            allFilms.put(film.getId(), film);
            log.debug("InMemoryFilmStorage: фильм добавлен: {}", film);
            return film;
    }

    public Film update(Film film) {
            allFilms.put(film.getId(), film);
            log.debug("InMemoryFilmStorage: фильм обновлен: {}", film);
            return film;
    }

    public Film delete(Integer filmId) {
        log.debug("InMemoryFilmStorage: фильм с id: {} удален.", filmId);
        return allFilms.remove(filmId);
    }

    public boolean validateDataExists(Integer id) {
        return allFilms.containsKey(id);
    }

    @Override
    public void setGenre(Film film) {
    }
}
