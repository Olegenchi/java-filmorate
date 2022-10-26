package ru.yandex.practicum.filmorate.storage;

import java.util.List;

public interface Storage<T> {

    List<T> findAll();

    T get(Integer id);

    T create(T t);

    T update(T t);

    T delete(Integer id);

    boolean validateDataExists(Integer id);
}
