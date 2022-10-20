package ru.yandex.practicum.filmorate.controller.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.filmorate.exception.FilmValidationException;
import ru.yandex.practicum.filmorate.exception.LikeDoesNotExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;

@Slf4j
@Component
public class FilmValidator {
    private static final LocalDate MIN_DATE = LocalDate.of(1895, 12, 28);
    private final FilmService filmService;

    @Autowired
    public FilmValidator(FilmService filmService) {
        this.filmService = filmService;
    }

    public boolean filmValidationByReleaseDate(Film film){
        if (film.getReleaseDate().isBefore(MIN_DATE)) {
            log.warn("FilmValidator: ошибка при добавлении фильма: {}.", film);
            throw new ValidationException("Дата релиза не может быть раньше 28.12.1895 г.");
        }
        return true;
    }

    public boolean filmValidationById(@PathVariable Integer filmId) {
        if (filmService.getFilmById(filmId) == null) {
            log.warn("FilmValidator: ошибка при добавлении фильма с id: {}.", filmId);
            throw new FilmValidationException("Фильм с таким id не существует.");
        }
        return true;
    }

    public boolean popularFilmValidation(@RequestParam(defaultValue = "10", required = false) Integer count) {
        if (count <= 0) {
            log.warn("FilmValidator: значение count должно быть больше 0.");
            throw new IllegalArgumentException("Значение count должно быть больше 0.");
        }
        return true;
    }

    public boolean dislikeValidation(@PathVariable Integer filmId, @PathVariable Integer userId) {
        if (!filmService.getFilmById(filmId).getLikes().contains(userId)) {
            log.warn("FilmValidator: пользователь с id: {} хочет удалить лайк, который он не ставил, " +
                            "у фильма с id: {}.", userId, filmId);
            throw new LikeDoesNotExistException("Пользователь с id: " + userId +
                    " хочет удалить лайк, который он не ставил, у фильма с id: " + filmId + ".");
        }
        return true;
    }
}
