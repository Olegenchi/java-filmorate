package ru.yandex.practicum.filmorate.storage.filmImpl;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaStorage {

    List<Mpa> findAll();

    Mpa get(Integer mpaId);

    boolean validateDataExists(Integer mpaId);
}
