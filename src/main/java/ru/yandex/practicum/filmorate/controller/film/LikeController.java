package ru.yandex.practicum.filmorate.controller.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.LikeService;

import java.util.List;

@RestController
@RequestMapping("/films")
public class LikeController {
    private final LikeService likeService;
    private final FilmValidator filmValidator;

    @Autowired
    public LikeController(LikeService likeService, FilmValidator filmValidator) {
        this.likeService = likeService;
        this.filmValidator = filmValidator;
    }

    @GetMapping("/popular")
    public List<Film> getMostPopularFilms(@RequestParam(defaultValue = "10", required = false) Integer count) {
        filmValidator.popularFilmValidation(count);
        return likeService.getMostPopularFilms(count);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film likeFilm(@PathVariable Integer id, @PathVariable Integer userId) {
        return likeService.likeFilm(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film dislikeFilm(@PathVariable Integer id, @PathVariable Integer userId) {
        filmValidator.dislikeValidation(id, userId);
        return likeService.dislikeFilm(id, userId);
    }
}
