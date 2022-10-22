package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.userImpl.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
//@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> allUsers = new HashMap<>();
    private int id = 0;

    private int generateNextId() {
        return ++id;
    }

    public List<User> findAll() {
        log.debug("InMemoryUserStorage: текущее количество пользователей: {}", allUsers.size());
        return new ArrayList<>(allUsers.values());
    }

    public User get(Integer userId) {
        log.debug("InMemoryUserStorage: пользователь c id: {} получен.", userId);
        return allUsers.get(userId);
    }

    public User create(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
            log.debug("InMemoryUserStorage: пользователь создан. В качестве имени использован login: {}", user);
        }
        user.setId(generateNextId());
        allUsers.put(user.getId(), user);
        log.debug("InMemoryUserStorage: пользователь добавлен с именем и логином: {}", user);
        return user;
    }

    public User update(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
            log.debug("InMemoryUserStorage: пользователь создан. В качестве имени использован login: {}", user);
        }
            allUsers.put(user.getId(), user);
            log.debug("InMemoryUserStorage: пользователь обновлен: {}", user);
            return user;
    }

    public User delete(Integer userId) {
        log.debug("Пользователь c id: {} удален.", userId);
        return allUsers.remove(userId);
    }

    public boolean validateDataExists(Integer id) {
        return allUsers.containsKey(id);
    }
}
