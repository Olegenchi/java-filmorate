package ru.yandex.practicum.filmorate.controller.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.exception.FilmValidationException;
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
            log.warn("Ошибка при добавлении фильма: {}", film);
            throw new ValidationException("Дата релиза не может быть раньше 28.12.1895 г.");
        }
        return true;
    }

    public boolean filmValidationById(@PathVariable Integer id) {
        if (!filmStorage.getAllFilms().containsKey(id)) {
            log.warn("Ошибка при добавлении фильма с ID: {}", id);
            throw new FilmValidationException("Фильма с таким ID не существует.");
        }
        return true;
    }
}
