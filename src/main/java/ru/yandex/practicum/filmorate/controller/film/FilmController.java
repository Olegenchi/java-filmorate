package ru.yandex.practicum.filmorate.controller.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.user.UserValidator;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;
    private final FilmValidator filmValidator;
    private final UserValidator userValidator;

    @Autowired
    public FilmController(FilmService filmService, FilmValidator filmValidator, UserValidator userValidator) {
        this.filmService = filmService;
        this.filmValidator = filmValidator;
        this.userValidator = userValidator;
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        filmValidator.filmValidationByReleaseDate(film);
        return filmService.createFilm(film);
    }

    @GetMapping
    public List<Film> findAllFilms() {
        return filmService.findAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable Integer id) {
        filmValidator.filmValidationById(id);
        return filmService.getFilmById(id);
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
        return filmService.updateFilm(film);
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
