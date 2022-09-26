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
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.time.LocalDate;

@Slf4j
@Component
public class FilmValidator {
    private static final LocalDate MIN_DATE = LocalDate.of(1895, 12, 28);
    private final InMemoryFilmStorage filmStorage;

    @Autowired
    public FilmValidator(InMemoryFilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public boolean filmValidationByReleaseDate(Film film){
        if (film.getReleaseDate().isBefore(MIN_DATE)) {
            log.warn("Ошибка при добавлении фильма: {}.", film);
            throw new ValidationException("Дата релиза не может быть раньше 28.12.1895 г.");
        }
        return true;
    }

    public boolean filmValidationById(@PathVariable Integer filmId) {
        if (!filmStorage.getAllFilms().containsKey(filmId)) {
            log.warn("Ошибка при добавлении фильма с id: {}.", filmId);
            throw new FilmValidationException("Фильма с таким ID не существует.");
        }
        return true;
    }

    public boolean popularFilmValidation(@RequestParam(defaultValue = "10", required = false) Integer count) {
        if (count <= 0) {
            log.warn("Значение count должно быть больше 0.");
            throw new IllegalArgumentException("Значение count должно быть больше 0.");
        }
        return true;
    }

    public boolean dislikeValidation(@PathVariable Integer filmId, @PathVariable Integer userId) {
        if (!filmStorage.getAllFilms().get(filmId).getLikes().contains(userId)) {
            log.warn("Пользователь с id: {} хочет удалить лайк, который он не ставил, у фильма с id: {}.",
                    userId, filmId);
            throw new LikeDoesNotExistException("Пользователь с id: " + userId +
                    " хочет удалить лайк, который он не ставил, у фильма с id: " + filmId + ".");
        }
        return true;
    }
}
