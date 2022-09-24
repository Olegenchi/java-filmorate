package ru.yandex.practicum.filmorate.controller.film;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@Data
public class FilmController {

    private final InMemoryFilmStorage filmStorage;
    private final FilmValidator filmValidator;
    private final FilmService filmService;

    @GetMapping
    public List<Film> findAllFilms() {
        return filmStorage.findAllFilms();
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        filmValidator.filmValidationByReleaseDate(film);
        return filmStorage.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        filmValidator.filmValidationByReleaseDate(film);
        filmValidator.filmValidationById(film.getId());
        return filmStorage.updateFilm(film);
    }
}
