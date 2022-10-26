package ru.yandex.practicum.filmorate.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserValidator userValidator;

    @Autowired
    public UserController(UserService userService, UserValidator userValidator) {
        this.userService = userService;
        this.userValidator = userValidator;
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        userValidator.userValidationByName(user);
        return userService.createUser(user);
    }

    @GetMapping
    public List<User> findAllUsers() {
        return userService.findAllUsers();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        userValidator.userValidationByName(user);
        return userService.updateUser(user);
    }

    @DeleteMapping("/{id}")
    public User delete(@PathVariable Integer id) {
        return userService.delete(id);
    }
}
