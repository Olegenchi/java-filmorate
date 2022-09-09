package ru.yandex.practicum.filmorate.controller;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.FilmValidationException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
@Data
public class FilmController {
    private static final LocalDate MIN_DATE = LocalDate.of(1895, 12, 28);
    private final Map<Integer, Film> allFilms = new HashMap<>();
    private int id = 0;

    private int generateNextId() {
        return ++id;
    }

    @GetMapping
    public List<Film> findAllFilms() {
        log.debug("Текущее количество фильмов: {}", allFilms.size());
        return new ArrayList<>(allFilms.values());
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        if (allFilms.containsKey(film.getId())) {
            return film;
        }
        if (filmValidation(film)) {
            film.setId(generateNextId());
            allFilms.put(film.getId(), film);
            log.debug("Фильм добавлен: {}", film);
            return film;
        }
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (filmValidation(film) && allFilms.containsKey(film.getId())) {
            allFilms.put(film.getId(), film);
            log.debug("Фильм обновлен: {}", film);
            return film;
        } else {
            log.warn("Ошибка при обновлении фильма: {}", film);
            throw new FilmValidationException("Ошибка обновления фильма.");
        }
    }

    public boolean filmValidation(Film film){
        if (film.getReleaseDate().isBefore(MIN_DATE)) {
            log.warn("Ошибка при добавлении фильма: {}", film);
            throw new ValidationException("Дата релиза не может быть раньше 28.12.1895 г.");
        }
        return true;
    }
}
