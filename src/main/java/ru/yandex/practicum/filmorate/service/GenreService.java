package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.GenreDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.GenreDbStorage;

import java.util.List;

@Slf4j
@Service
public class GenreService {
    private final GenreDbStorage genreDbStorage;

    @Autowired
    public GenreService(GenreDbStorage genreDbStorage) {
        this.genreDbStorage = genreDbStorage;
    }

    public List<Genre> findAll() {
        log.debug("GenreService: запрос на получение всех жанров.");
        return genreDbStorage.findAll();
    }

    public Genre get(Integer genreId) {
        log.debug("GenreService: запрос на получение жанра с id: {}.", genreId);
        validateDataExists(genreId);
        return genreDbStorage.get(genreId);
    }

    public void validateDataExists(Integer genreId) {
        log.debug("GenreService: запрос на проверку наличия жанра с id: {} в базе данных жанров.", genreId);
        if (!genreDbStorage.validateDataExists(genreId)) {
            String message = "Жанра c таким id не существует.";
            throw new GenreDoesNotExistException(message);
        }
    }
}
