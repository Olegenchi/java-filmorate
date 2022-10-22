package ru.yandex.practicum.filmorate.storage.userImpl;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendStorage {

    List<User> addFriend(Integer userId, Integer friendId);

    List<User> deleteFriend(Integer userId, Integer friendId);

    List<User> getAllFriends(Integer userId);

    List<User> getCommonFriends(Integer userId, Integer otherId);
}
