package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.GenreDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.filmImpl.GenreStorage;

import java.util.List;

@Slf4j
@Service
public class GenreService {
    private final GenreStorage genreStorage;

    @Autowired
    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public List<Genre> findAll() {
        log.debug("GenreService: запрос на получение всех жанров.");
        return genreStorage.findAll();
    }

    public Genre get(Integer genreId) {
        log.debug("GenreService: запрос на получение жанра с id: {}.", genreId);
        validateGenreExists(genreId);
        return genreStorage.get(genreId);
    }

    public void validateGenreExists(Integer genreId) {
        log.debug("GenreService: запрос на проверку наличия жанра с id: {} в базе данных жанров.", genreId);
        if (!genreStorage.validateDataExists(genreId)) {
            String message = "Жанра c таким id не существует.";
            throw new GenreDoesNotExistException(message);
        }
    }
}
