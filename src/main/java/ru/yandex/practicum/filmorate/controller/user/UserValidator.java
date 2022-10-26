package ru.yandex.practicum.filmorate.controller.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.exception.UserDoesNotExistException;
import ru.yandex.practicum.filmorate.exception.UserValidationException;
import ru.yandex.practicum.filmorate.model.User;
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
            log.warn("UserValidator: ошибка при добавлении пользователя с id: {}.", userId);
            throw new UserValidationException("Пользователь с таким id не существует.");
        }
        return true;
    }

    public boolean userValidationAddHimselfAsFriend(@PathVariable Integer userId, @PathVariable Integer friendId) {
        if (userId.equals(friendId)) {
            log.error("UserValidator: добавлять себя в друзья запрещено.");
            throw new UserDoesNotExistException("Добавлять себя в друзья запрещено.");
        }
        return true;
    }

    public void userValidationByName(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
            log.debug("Пользователь создан. В качестве имени использован login: {}", user);
        }
    }
}
