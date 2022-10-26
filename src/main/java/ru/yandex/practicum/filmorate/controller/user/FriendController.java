package ru.yandex.practicum.filmorate.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FriendService;

import java.util.List;

@RestController
@RequestMapping("/users/{id}/friends")
public class FriendController {
    private final FriendService friendService;
    private final UserValidator userValidator;

    @Autowired
    public FriendController(FriendService friendService, UserValidator userValidator) {
        this.friendService = friendService;
        this.userValidator = userValidator;
    }

    @GetMapping
    public List<User> getAllUserFriends(@PathVariable Integer id) {
        userValidator.userValidationById(id);
        return friendService.findAll(id);
    }

    @GetMapping("/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        userValidator.userValidationById(id);
        userValidator.userValidationById(otherId);
        return friendService.getCommonFriends(id, otherId);
    }

    @PutMapping("/{friendId}")
    public List<User> addFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        userValidator.userValidationAddHimselfAsFriend(id, friendId);
        userValidator.userValidationById(id);
        userValidator.userValidationById(friendId);
        return friendService.addFriend(id, friendId);
    }

    @DeleteMapping("/{friendId}")
    public List<User> deleteFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        userValidator.userValidationById(id);
        userValidator.userValidationById(friendId);
        return friendService.deleteFriend(id, friendId);
    }
}
