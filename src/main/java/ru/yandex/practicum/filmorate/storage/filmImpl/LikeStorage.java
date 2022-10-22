package ru.yandex.practicum.filmorate.storage.filmImpl;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface LikeStorage {

    Film likeFilm(Integer filmId, Integer userId);

    Film dislikeFilm(Integer filmId, Integer userId);

    List<Film> getMostPopularFilms(Integer count);

    void setLike(Film film);
}
