package ru.yandex.practicum.filmorate.storage.filmImpl;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.Storage;

public interface FilmStorage extends Storage<Film> {

    void setGenre(Film film);
}
