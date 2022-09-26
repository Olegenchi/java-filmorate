package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final InMemoryUserStorage userStorage;

    @Autowired
    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User getUser(Integer userId) {
        log.debug("Пользователь c id: {} получен.", userId);
        return userStorage.getAllUsers().get(userId);
    }

    public List<User> addFriend(Integer userId, Integer friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        log.debug("Пользователь c id: {} подружился с другим пользователем с id: {}.", userId, friendId);
        return List.of(user, friend);
    }

    public List<User> deleteFriend(Integer userId, Integer friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        log.debug("Пользователь c id: {} удалил из друзей пользователя с id: {}.", userId, friendId);
        return List.of(user, friend);
    }

    public List<User> getCommonFriends(Integer userId, Integer otherUserId) {
        Set<Integer> commonFriends = new HashSet<>(getUser(userId).getFriends());
        commonFriends.retainAll(getUser(otherUserId).getFriends());
        log.debug("Общие друзья у пользователя с id: {} с пользователем с id: {}.", userId, otherUserId);
        return userStorage.getAllUsers().values().stream()
                .filter(user -> commonFriends.contains(user.getId()))
                .collect(Collectors.toList());
    }

    public List<User> getAllUserFriends(Integer userId) {
        Set<Integer> friends = getUser(userId).getFriends();
        log.debug("Количество друзей у пользователя с id: {} равно {}.", userId, friends.size());
        return userStorage.getAllUsers().values().stream()
                .filter(user -> friends.contains(user.getId()))
                .collect(Collectors.toList());
    }
}
