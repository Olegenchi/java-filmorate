package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserDoesNotExistException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.userImpl.UserStorage;

import java.util.List;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> findAllUsers() {
        log.debug("UserService: список пользователей получен.");
        return userStorage.findAll();
    }

    public User createUser(User user) {
        log.debug("UserService: пользователь c id: {} добавлен.", user.getId());
        return userStorage.create(user);
    }

    public User updateUser(User user) {
        log.debug("UserService: пользователь c id: {} обновлен.", user.getId());
        validateUserExists(user.getId());
        return userStorage.update(user);
    }

    public User getUserById(Integer userId) {
        log.debug("UserService: пользователь c id: {} получен.", userId);
        validateUserExists(userId);
        return userStorage.get(userId);
    }

    public User delete(Integer userId) {
        log.debug("UserService: пользователь c id: {} удален.", userId);
        validateUserExists(userId);
        return userStorage.delete(userId);
    }

    public void validateUserExists(Integer userId) {
        log.debug("UserService: запрос на проверку наличия пользователя с id: {} в БД.", userId);
        if (!userStorage.validateDataExists(userId)) {
            String message = "Пользователя c таким id не существует.";
            throw new UserDoesNotExistException(message);
        }
    }
}
