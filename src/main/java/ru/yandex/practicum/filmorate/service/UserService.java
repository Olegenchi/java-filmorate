package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> findAllUsers() {
        return userStorage.findAllUsers();
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public User getUserById(Integer userId) {
        log.debug("Пользователь c id: {} получен.", userId);
        return userStorage.getUserById(userId);
    }

    public List<User> addFriend(Integer userId, Integer friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        log.debug("Пользователь c id: {} подружился с другим пользователем с id: {}.", userId, friendId);
        return List.of(user, friend);
    }

    public List<User> deleteFriend(Integer userId, Integer friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        log.debug("Пользователь c id: {} удалил из друзей пользователя с id: {}.", userId, friendId);
        return List.of(user, friend);
    }

    public List<User> getCommonFriends(Integer userId, Integer otherUserId) {
        Set<Integer> commonFriends = new HashSet<>(getUserById(userId).getFriends());
        commonFriends.retainAll(getUserById(otherUserId).getFriends());
        log.debug("Общие друзья у пользователя с id: {} с пользователем с id: {}.", userId, otherUserId);
        return userStorage.findAllUsers().stream()
                .filter(user -> commonFriends.contains(user.getId()))
                .collect(Collectors.toList());
    }

    public List<User> getAllUserFriends(Integer userId) {
        Set<Integer> friends = getUserById(userId).getFriends();
        log.debug("Количество друзей у пользователя с id: {} равно {}.", userId, friends.size());
        return userStorage.findAllUsers().stream()
                .filter(user -> friends.contains(user.getId()))
                .collect(Collectors.toList());
    }
}
