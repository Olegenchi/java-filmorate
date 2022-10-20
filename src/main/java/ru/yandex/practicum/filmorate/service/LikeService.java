package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.LikeDbStorage;

import java.util.List;

@Slf4j
@Service
public class LikeService {
    private final UserService userService;
    private final FilmService filmService;
    private final LikeDbStorage likeDbStorage;

    @Autowired
    public LikeService(UserService userService, FilmService filmService, LikeDbStorage likeDbStorage) {
        this.userService = userService;
        this.filmService = filmService;
        this.likeDbStorage = likeDbStorage;
    }

    public Film likeFilm(Integer filmId, Integer userId) {
        log.debug("LikesService: пользователь с id: {} поставил лайк фильму с id: {}.", userId, filmId);
        filmService.validateFilmExists(filmId);
        userService.validateUserExists(userId);
        return likeDbStorage.likeFilm(filmId, userId);
    }

    public Film dislikeFilm(Integer filmId, Integer userId) {
        log.debug("LikesService: пользователь с id: {} удалил лайк фильму с id: {}.", userId, filmId);
        filmService.validateFilmExists(filmId);
        userService.validateUserExists(userId);
        return likeDbStorage.dislikeFilm(filmId, userId);
    }

    public List<Film> getMostPopularFilms(Integer count) {
        log.debug("LikesService: запрос на получение списка самых популярных фильмов размером {}.", count);
        return likeDbStorage.getMostPopularFilms(count);
    }
}
