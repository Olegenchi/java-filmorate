package ru.yandex.practicum.filmorate.controller.user;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@Data
public class UserController {

    private final InMemoryUserStorage userStorage;
    private final UserController userController;
    private final UserValidator userValidator;

    @GetMapping
    public List<User> findAllUsers() {
        return userStorage.findAllUsers();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        return userStorage.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        userValidator.userValidationById(user.getId());
        return userStorage.updateUser(user);
    }
}
