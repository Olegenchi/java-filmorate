package ru.yandex.practicum.filmorate.storage.user;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Data
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> allUsers = new HashMap<>();
    private int id = 0;

    private int generateNextId() {
        return ++id;
    }

    public List<User> findAllUsers() {
        log.debug("Текущее количество пользователей: {}", allUsers.size());
        return new ArrayList<>(allUsers.values());
    }

    public User createUser(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
            log.debug("Пользователь создан. В качестве имени использован login: {}", user);
        }
        user.setId(generateNextId());
        allUsers.put(user.getId(), user);
        log.debug("Пользователь добавлен с именем и логином: {}", user);
        return user;
    }

    public User updateUser(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
            log.debug("Пользователь создан. В качестве имени использован login: {}", user);
        }
            allUsers.put(user.getId(), user);
            log.debug("Пользователь обновлен: {}", user);
            return user;
    }
}
