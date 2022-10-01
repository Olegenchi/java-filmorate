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
        return userService.createUser(user);
    }

    @GetMapping
    public List<User> findAllUsers() {
        return userService.findAllUsers();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Integer id) {
        userValidator.userValidationById(id);
        return userService.getUserById(id);
    }

    @GetMapping("/{id}/friends")
    public List<User> getAllUserFriends(@PathVariable Integer id) {
        userValidator.userValidationById(id);
        return userService.getAllUserFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        userValidator.userValidationById(id);
        userValidator.userValidationById(otherId);
        return userService.getCommonFriends(id, otherId);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        userValidator.userValidationById(user.getId());
        return userService.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public List<User> addFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        userValidator.userValidationAddHimselfAsFriend(id, friendId);
        userValidator.userValidationById(id);
        userValidator.userValidationById(friendId);
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public List<User> deleteFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        userValidator.userValidationById(id);
        userValidator.userValidationById(friendId);
        return userService.deleteFriend(id, friendId);
    }
}
