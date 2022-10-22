package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.userImpl.FriendStorage;
import ru.yandex.practicum.filmorate.storage.userImpl.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Repository("FriendDbStorage")
public class FriendDbStorage implements FriendStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;

    public FriendDbStorage(JdbcTemplate jdbcTemplate, UserStorage userStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;
    }

    @Override
    public List<User> addFriend(Integer userId, Integer friendId) {
        log.debug("FriendStorage: запрос к БД от пользователя c id: {} на добавление в друзья пользователя с id: {}.",
                userId, friendId);
        String sql = "INSERT INTO FRIENDS(user_id, friend_id) " +
                "VALUES (?,?)";
        jdbcTemplate.update(sql, userId, friendId);
        List<User> result = new ArrayList<>();
        result.add(userStorage.get(userId));
        result.add(userStorage.get(friendId));
        return result;
    }

    @Override
    public List<User> deleteFriend(Integer userId, Integer friendId) {
        log.debug("FriendStorage: запрос к БД от пользователя c id: {} на удаление из друзей пользователя с id: {}.",
                userId, friendId);
        String sqlDelete = "DELETE FROM FRIENDS WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sqlDelete, userId, friendId);
        List<User> result = new ArrayList<>();
        result.add(userStorage.get(userId));
        result.add(userStorage.get(friendId));
        return result;
    }

    @Override
    public List<User> getAllFriends(Integer userId) {
        log.debug("FriendStorage: запрос к БД на получение списка всех друзей пользователя id: {}.", userId);
        String sql = "SELECT uf.friend_id, u.user_id, u.user_email, u.user_name, u.user_login, u.user_birthday " +
                "FROM FRIENDS AS uf " +
                "LEFT JOIN USERS AS u ON uf.friend_id = u.user_id " +
                "WHERE uf.user_id = ?";
        List<User> result = jdbcTemplate.query(sql, this::mapRowToUser, userId);
        result.forEach(this::setFriend);
        return result;
    }

    @Override
    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        log.debug("FriendStorage: запрос к БД на получение общих друзей пользователей c id: {} и id: {}.",
                userId, otherId);
        String sql = "SELECT friend_id " +
                "FROM FRIENDS " +
                "WHERE user_id = ? and friend_id IN (SELECT friend_id " +
                "FROM FRIENDS " +
                "WHERE user_id = ?)";
        List<Integer> common_id = jdbcTemplate.query(sql, this::mapRowToFriendId, userId, otherId);
        return common_id.stream()
                .map(userStorage::get)
                .collect(Collectors.toList());
    }

    public void setFriend(User user) {
        Integer userId = user.getId();
        log.debug("UserDbStorage: запрос на обновление друзей пользователя c id: {}.", userId);
        String sql = "SELECT friend_id " +
                "FROM FRIENDS " +
                "WHERE user_id = ?";
        Set<Integer> friends = new HashSet<>(jdbcTemplate.query(sql, this::mapRowToFriendId, userId));
        user.getFriends().addAll(friends);
    }

    public User mapRowToUser(ResultSet rs, Integer rowNum) throws SQLException {
        log.debug("RowMapper: получен запрос от БД на преобразование в User.");
        return User.builder()
                .id(rs.getInt("user_id"))
                .email(rs.getString("user_email"))
                .login(rs.getString("user_login"))
                .name(rs.getString("user_name"))
                .birthday(rs.getDate("user_birthday").toLocalDate())
                .build();
    }

    public Integer mapRowToFriendId(ResultSet rs, Integer rowNum) throws SQLException {
        log.debug("RowMapper: получен запрос от БД на преобразование в id друга.");
        return rs.getInt("friend_id");
    }
}
