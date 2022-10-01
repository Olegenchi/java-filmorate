package ru.yandex.practicum.filmorate.controller.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.exception.UserDoesNotExistException;
import ru.yandex.practicum.filmorate.exception.UserValidationException;
import ru.yandex.practicum.filmorate.service.UserService;

@Slf4j
@Component
public class UserValidator {
    private final UserService userService;

    @Autowired
    public UserValidator(UserService userService) {
        this.userService = userService;
    }

    public boolean userValidationById(@PathVariable Integer userId) {
        if (userService.getUserById(userId) == null) {
            log.warn("Ошибка при добавлении пользователя с id: {}.", userId);
            throw new UserValidationException("Пользователь с таким id не существует.");
        }
        if (!userService.findAllUsers().contains(userService.getUserById(userId))) {
            log.warn("Ошибка при добавлении пользователя с id: {}.", userId);
            throw new UserValidationException("Пользователь с таким id не существует.");
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
