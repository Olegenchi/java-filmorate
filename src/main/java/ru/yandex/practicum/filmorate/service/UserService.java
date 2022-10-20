package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserDoesNotExistException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.List;

@Slf4j
@Service
public class UserService {
    private final UserDbStorage userDbStorage;

    @Autowired
    public UserService(UserDbStorage userDbStorage) {
        this.userDbStorage = userDbStorage;
    }

    public List<User> findAllUsers() {
        log.debug("UserService: список пользователей получен.");
        return userDbStorage.findAll();
    }

    public User createUser(User user) {
        log.debug("UserService: пользователь c id: {} добавлен.", user.getId());
        return userDbStorage.create(user);
    }

    public User updateUser(User user) {
        log.debug("UserService: пользователь c id: {} обновлен.", user.getId());
        validateUserExists(user.getId());
        return userDbStorage.update(user);
    }

    public User getUserById(Integer userId) {
        log.debug("UserService: пользователь c id: {} получен.", userId);
        validateUserExists(userId);
        return userDbStorage.get(userId);
    }

    public User delete(Integer userId) {
        log.debug("UserService: пользователь c id: {} удален.", userId);
        validateUserExists(userId);
        return userDbStorage.delete(userId);
    }

    public void validateUserExists(Integer userId) {
        log.debug("UserService: запрос на проверку наличия пользователя с id: {} в БД.", userId);
        if (!userDbStorage.validateDataExists(userId)) {
            String message = "Пользователя c таким id не существует.";
            throw new UserDoesNotExistException(message);
        }
    }
}
