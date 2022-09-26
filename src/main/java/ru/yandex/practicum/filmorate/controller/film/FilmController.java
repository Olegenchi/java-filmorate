package ru.yandex.practicum.filmorate.controller.film;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.user.UserValidator;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

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
    private final InMemoryUserStorage userStorage;
    private final UserValidator userValidator;

    @Autowired
    public FilmController(InMemoryFilmStorage filmStorage, FilmService filmService, InMemoryUserStorage userStorage, FilmValidator filmValidator, UserValidator userValidator) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
        this.userStorage = userStorage;
        this.filmValidator = filmValidator;
        this.userValidator = userValidator;
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        filmValidator.filmValidationByReleaseDate(film);
        return filmStorage.createFilm(film);
    }

    @GetMapping
    public List<Film> findAllFilms() {
        return filmStorage.findAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable Integer id) {
        filmValidator.filmValidationById(id);
        return filmService.getFilm(id);
    }

    @GetMapping("/popular")
    public List<Film> getMostPopularFilms(@RequestParam(defaultValue = "10", required = false) Integer count) {
        filmValidator.popularFilmValidation(count);
        return filmService.getMostPopularFilms(count);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        filmValidator.filmValidationByReleaseDate(film);
        filmValidator.filmValidationById(film.getId());
        return filmStorage.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film likeFilm(@PathVariable Integer id, @PathVariable Integer userId) {
        filmValidator.filmValidationById(id);
        userValidator.userValidationById(userId);
        return filmService.likeFilm(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film dislikeFilm(@PathVariable Integer id, @PathVariable Integer userId) {
        filmValidator.filmValidationById(id);
        userValidator.userValidationById(userId);
        filmValidator.dislikeValidation(id, userId);
        return filmService.dislikeFilm(id, userId);
    }
}
