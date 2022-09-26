package ru.yandex.practicum.filmorate.controller.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.exception.UserDoesNotExistException;
import ru.yandex.practicum.filmorate.exception.UserValidationException;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

@Slf4j
@Component
public class UserValidator {
    private final InMemoryUserStorage userStorage;

    @Autowired
    public UserValidator(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public boolean userValidationById(@PathVariable Integer userId) {
        if (!userStorage.getAllUsers().containsKey(userId)) {
            log.warn("Ошибка при добавлении пользователя с id: {}.", userId);
            throw new UserValidationException("Пользователя с таким ID не существует.");
        }
        return true;
    }

    public boolean userValidationAddHimselfAsFriend(@PathVariable Integer userId, @PathVariable Integer friendId) {
        if (userId.equals(friendId)) {
            log.error("Добавлять себя в друзья запрещено.");
            throw new UserDoesNotExistException("Добавлять себя в друзья запрещено.");
        }
        return true;
    }
}
