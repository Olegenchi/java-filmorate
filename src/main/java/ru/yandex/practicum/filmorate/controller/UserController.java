package ru.yandex.practicum.filmorate.controller;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
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
    private Map<Integer, User> userList = new HashMap<>();
    private int id = 0;

    private int generateNextId() {
        return ++id;
    }

    @GetMapping
    public List<User> findAllUsers() {
        log.debug("Текущее количество пользователей: {}", userList.size());
        return new ArrayList<>(userList.values());
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        if (userList.containsKey(user.getId())) {
            return user;
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
            log.debug("Пользователь создан. В качестве имени использовал login: {}", user);
        }
        user.setId(generateNextId());
        userList.put(user.getId(), user);
        log.debug("Пользователь добавлен с именем и логином: {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        if (userList.containsKey(user.getId())) {
            userList.put(user.getId(), user);
            log.debug("Пользователь обновлен: {}", user);
            return user;
        }
        log.warn("Ошибка при обновлении пользователя: {}", user);
        throw new ValidationException("Ошибка обновления данных пользователя.");
    }
}
