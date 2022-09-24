package ru.yandex.practicum.filmorate.controller.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.exception.FilmValidationException;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

@Slf4j
@Component
public class UserValidator {
    private final InMemoryUserStorage userStorage;

    @Autowired
    public UserValidator(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public boolean userValidationById(@PathVariable Integer id) {
        if (!userStorage.getAllUsers().containsKey(id)) {
            log.warn("Ошибка при добавлении пользователя с ID: {}", id);
            throw new FilmValidationException("Пользователя с таким ID не существует.");
        }
        return true;
    }
}
