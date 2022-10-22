package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserDoesNotExistException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.userImpl.FriendStorage;
import ru.yandex.practicum.filmorate.storage.userImpl.UserStorage;

import java.util.List;

@Slf4j
@Service
public class FriendService {
    private final UserStorage userStorage;
    private final FriendStorage friendStorage;

    @Autowired
    public FriendService(UserStorage userStorage, FriendStorage friendStorage) {
        this.userStorage = userStorage;
        this.friendStorage = friendStorage;
    }

    public List<User> addFriend(Integer userId, Integer friendId) {
        log.debug("FriendsService: запрос от пользователя c id: {} на добавление в друзья пользователя с id: {}.",
                userId, friendId);
        validateFriendExists(userId);
        validateFriendExists(friendId);
        return friendStorage.addFriend(userId, friendId);
    }

    public List<User> findAll(Integer userId) {
        log.debug("FriendsService: запрос на получение всех друзей пользователя c id: {}.", userId);
        validateFriendExists(userId);
        return friendStorage.getAllFriends(userId);
    }

    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        log.debug("FriendsService: запрос на получение общих друзей пользователей c id: {} и id: {}.", userId, otherId);
        validateFriendExists(userId);
        validateFriendExists(otherId);
        return friendStorage.getCommonFriends(userId, otherId);
    }

    public List<User> deleteFriend(Integer userId, Integer friendId) {
        log.debug("FriendsService: запрос от пользователя c id: {} на удаление из друзей пользователя с id: {}.",
                userId, friendId);
        validateFriendExists(userId);
        validateFriendExists(friendId);
        return friendStorage.deleteFriend(userId, friendId);
    }

    public void validateFriendExists(Integer userId) {
        log.debug("FriendsService: проверка наличия пользователя с id: {} в БД.", userId);
        if (!userStorage.validateDataExists(userId)) {
            String message = "Пользователя c таким id не существует.";
            throw new UserDoesNotExistException(message);
        }
    }
}
