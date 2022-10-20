package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.RowMapper;
import ru.yandex.practicum.filmorate.storage.film.LikeDbStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Repository("UserDbStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final LikeDbStorage likeDbStorage;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate, LikeDbStorage likeDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.likeDbStorage = likeDbStorage;
    }

    @Override
    public List<User> findAll() {
        log.debug("UserDbStorage: запрос на получение всех пользователей из БД.");
        String sql = "SELECT * " +
                "FROM USERS";
        List<User> result = jdbcTemplate.query(sql, RowMapper::mapRowToUser);
        result.forEach(this::setFriend);
        return result;
    }

    @Override
    public User get(Integer userId) {
        log.debug("UserDbStorage: запрос к БД на получение пользователя с id: {}.", userId);
        String sql = "SELECT user_id, user_email, user_name, user_login, user_birthday " +
                "FROM USERS " +
                "WHERE user_id = ?";
        User result = jdbcTemplate.queryForObject(sql, RowMapper::mapRowToUser, userId);
        setFriend(result);
        return result;
    }

    @Override
    public User create(User user) {
        log.debug("UserDbStorage: запрос к БД на добавление нового пользователя: {}.", user.getLogin());
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("USERS")
                .usingGeneratedKeyColumns("user_id");
        Integer userId = simpleJdbcInsert.executeAndReturnKey(user.userToMap()).intValue();
        user.setId(userId);
        return get(userId);
    }

    @Override
    public User update(User user) {
        int userId = user.getId();
        log.debug("UserDbStorage: запрос к БД на обновление пользователя с id: {}.", userId);
        String sql = "UPDATE USERS SET " +
                "user_email = ?, user_name = ?, user_login = ?, user_birthday = ? "
                + "WHERE user_id = ?";
        jdbcTemplate.update(sql,
                user.getEmail(),
                user.getName(),
                user.getLogin(),
                user.getBirthday(),
                user.getId());
        return get(userId);
    }

    @Override
    public User delete(Integer userId) {
        log.debug("UserDbStorage: запрос к БД на удаление пользователя с id: {}.", userId);
        String sqlDeleteFriendship = "DELETE FROM FRIENDS WHERE friend_id = ?";
        jdbcTemplate.update(sqlDeleteFriendship, userId);

        String sqlGetLikedFilms = "SELECT film_id FROM LIKES WHERE user_id = ?";
        List<Integer> likedFilms = jdbcTemplate.query(sqlGetLikedFilms, RowMapper::mapRowToLikeId, userId);
        likedFilms.forEach(filmId -> likeDbStorage.dislikeFilm(filmId, userId));
        String sqlDeleteLike = "DELETE FROM LIKES WHERE user_id = ?";
        jdbcTemplate.update(sqlDeleteLike, userId);
        log.debug("UserDbStorage: ссылки на пользователя id: {} удалены из БД.", userId);

        String sqlDeleteUser = "DELETE FROM USERS WHERE user_id = ?";
        User deletedUser = get(userId);
        jdbcTemplate.update(sqlDeleteUser, userId);
        log.debug("UserDbStorage: пользователь с id: {} удален.", deletedUser.getId());
        return deletedUser;
    }

    @Override
    public boolean validateDataExists(Integer userId) {
        log.debug("UserDbStorage: запрос на проверку наличия пользователя с id: {} в БД.", userId);
        String sql = "SELECT COUNT(*) AS count " +
                "FROM USERS " +
                "WHERE user_id = ?";
        int count = jdbcTemplate.queryForObject(sql, RowMapper::mapRowToCount, userId);
        return count != 0;
    }
//duplicate
    public void setFriend(User user) {
        Integer userId = user.getId();
        log.debug("UserDbStorage: запрос на обновление друзей пользователя c id: {}.", userId);
        String sql = "SELECT friend_id " +
                "FROM FRIENDS " +
                "WHERE user_id = ?";
        Set<Integer> friends = new HashSet<>(jdbcTemplate.query(sql, RowMapper::mapRowToFriendId, userId));
        user.getFriends().addAll(friends);
    }
}
