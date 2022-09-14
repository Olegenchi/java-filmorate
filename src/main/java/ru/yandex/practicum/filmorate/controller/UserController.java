package ru.yandex.practicum.filmorate.controller;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.UserValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
@Data
public class UserController {
    private final Map<Integer, User> allUsers = new HashMap<>();
    private int id = 0;

    private int generateNextId() {
        return ++id;
    }

    @GetMapping
    public List<User> findAllUsers() {
        log.debug("Текущее количество пользователей: {}", allUsers.size());
        return new ArrayList<>(allUsers.values());
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        if (allUsers.containsKey(user.getId())) {
            return user;
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
            log.debug("Пользователь создан. В качестве имени использован login: {}", user);
        }
        user.setId(generateNextId());
        allUsers.put(user.getId(), user);
        log.debug("Пользователь добавлен с именем и логином: {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
            log.debug("Пользователь создан. В качестве имени использован login: {}", user);
        }
        if (allUsers.containsKey(user.getId())) {
            allUsers.put(user.getId(), user);
            log.debug("Пользователь обновлен: {}", user);
            return user;
        }
        log.warn("Ошибка при обновлении пользователя: {}", user);
        throw new UserValidationException("Ошибка обновления данных пользователя.");
    }
}
