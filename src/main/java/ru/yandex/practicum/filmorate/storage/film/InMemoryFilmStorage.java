package ru.yandex.practicum.filmorate.storage.film;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Data
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> allFilms = new HashMap<>();
    private int id = 0;

    private int generateNextId() {
        return ++id;
    }

    public List<Film> findAllFilms() {
        log.debug("Текущее количество фильмов: {}", allFilms.size());
        return new ArrayList<>(allFilms.values());
    }

    public Film createFilm(Film film) {
            film.setId(generateNextId());
            allFilms.put(film.getId(), film);
            log.debug("Фильм добавлен: {}", film);
            return film;
    }

    public Film updateFilm(Film film) {
            allFilms.put(film.getId(), film);
            log.debug("Фильм обновлен: {}", film);
            return film;
    }
}
