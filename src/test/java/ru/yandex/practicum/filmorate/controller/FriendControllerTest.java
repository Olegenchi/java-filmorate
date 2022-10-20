package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exception.UserDoesNotExistException;
import ru.yandex.practicum.filmorate.exception.UserValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class FriendControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenAddFriend_whenAddFriend_thenStatus200AndAddFriend() throws Exception {
        User user = User.builder()
                .name("Oleg")
                .email("terentjev.dr@yandex.ru")
                .login("OlegT")
                .birthday(LocalDate.of(1993, 12, 3))
                .build();
        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON));

        User user2 = User.builder()
                .name("Oleg2")
                .email("terentjev2.dr@yandex.ru")
                .login("OlegT2")
                .birthday(LocalDate.of(1995, 12, 3))
                .build();
        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user2))
                        .contentType(MediaType.APPLICATION_JSON));

        Integer userId = 1;
        Integer friendId = 2;

        mockMvc.perform(
                        put("/users/{id}/friends/{friendId}", userId, friendId))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].email").value("terentjev.dr@yandex.ru"))
                .andExpect(jsonPath("$[0].login").value("OlegT"))
                .andExpect(jsonPath("$[0].name").value("Oleg"))
                .andExpect(jsonPath("$[0].birthday")
                        .value(LocalDate.of(1993, 12, 3).toString()))
                .andExpect(jsonPath("$[0].friends[0]").value(2))
                .andExpect(jsonPath("$[0].friends.length()").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].email").value("terentjev2.dr@yandex.ru"))
                .andExpect(jsonPath("$[1].login").value("OlegT2"))
                .andExpect(jsonPath("$[1].name").value("Oleg2"))
                .andExpect(jsonPath("$[1].birthday")
                        .value(LocalDate.of(1995, 12, 3).toString()))
                .andExpect(jsonPath("$[1].friends.length()").value(0))
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenAddFriendWithInvalidUserId_whenAddFriend_thenStatus404() throws Exception {
        User user = User.builder()
                .name("Oleg")
                .email("terentjev.dr@yandex.ru")
                .login("OlegT")
                .birthday(LocalDate.of(1993, 12, 3))
                .build();
        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON));

        User user2 = User.builder()
                .name("Oleg2")
                .email("terentjev2.dr@yandex.ru")
                .login("OlegT2")
                .birthday(LocalDate.of(1995, 12, 3))
                .build();
        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user2))
                        .contentType(MediaType.APPLICATION_JSON));

        Integer userId = 3;
        Integer friendId = 2;

        mockMvc.perform(
                        put("/users/{id}/friends/{friendId}", userId, friendId))

                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof UserDoesNotExistException))
                .andExpect(result -> assertEquals("Пользователя c таким id не существует.",
                        result.getResolvedException().getMessage()));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenAddFriendWithInvalidFriendId_whenAddFriend_thenStatus404() throws Exception {
        User user = User.builder()
                .name("Oleg")
                .email("terentjev.dr@yandex.ru")
                .login("OlegT")
                .birthday(LocalDate.of(1993, 12, 3))
                .build();
        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON));

        User user2 = User.builder()
                .name("Oleg2")
                .email("terentjev2.dr@yandex.ru")
                .login("OlegT2")
                .birthday(LocalDate.of(1995, 12, 3))
                .build();
        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user2))
                        .contentType(MediaType.APPLICATION_JSON));

        Integer userId = 1;
        Integer friendId = 3;

        mockMvc.perform(
                        put("/users/{id}/friends/{friendId}", userId, friendId))

                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof UserDoesNotExistException))
                .andExpect(result -> assertEquals("Пользователя c таким id не существует.",
                        result.getResolvedException().getMessage()));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenAddFriendYourself_whenAddFriend_thenStatus400() throws Exception {
        User user = User.builder()
                .name("Oleg")
                .email("terentjev.dr@yandex.ru")
                .login("OlegT")
                .birthday(LocalDate.of(1993, 12, 3))
                .build();
        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON));

        Integer userId = 1;

        mockMvc.perform(
                        put("/users/{id}/friends/{friendId}", userId, userId))

                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof UserDoesNotExistException))
                .andExpect(result -> assertEquals("Добавлять себя в друзья запрещено.",
                        result.getResolvedException().getMessage()));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenDeleteFriend_whenDeleteFriend_thenStatus200AndDeleteFriend() throws Exception {
        User user = User.builder()
                .name("Oleg")
                .email("terentjev.dr@yandex.ru")
                .login("OlegT")
                .birthday(LocalDate.of(1993, 12, 3))
                .build();
        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON));

        User user2 = User.builder()
                .name("Oleg2")
                .email("terentjev2.dr@yandex.ru")
                .login("OlegT2")
                .birthday(LocalDate.of(1995, 12, 3))
                .build();
        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user2))
                        .contentType(MediaType.APPLICATION_JSON));

        Integer userId = 1;
        Integer friendId = 2;

        mockMvc.perform(
                put("/users/{id}/friends/{friendId}", userId, friendId));

        mockMvc.perform(
                        delete("/users/{id}/friends/{friendId}", userId, friendId)
                )

                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].email").value("terentjev.dr@yandex.ru"))
                .andExpect(jsonPath("$[0].login").value("OlegT"))
                .andExpect(jsonPath("$[0].name").value("Oleg"))
                .andExpect(jsonPath("$[0].birthday")
                        .value(LocalDate.of(1993, 12, 3).toString()))
                .andExpect(jsonPath("$[0].friends.length()").value(0))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].email").value("terentjev2.dr@yandex.ru"))
                .andExpect(jsonPath("$[1].login").value("OlegT2"))
                .andExpect(jsonPath("$[1].name").value("Oleg2"))
                .andExpect(jsonPath("$[1].birthday")
                        .value(LocalDate.of(1995, 12, 3).toString()))
                .andExpect(jsonPath("$[1].friends.length()").value(0))
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenDeleteFriendWithInvalidUserId_whenDeleteFriend_thenStatus404() throws Exception {
        User user = User.builder()
                .name("Oleg")
                .email("terentjev.dr@yandex.ru")
                .login("OlegT")
                .birthday(LocalDate.of(1993, 12, 3))
                .build();
        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON));

        User user2 = User.builder()
                .name("Oleg2")
                .email("terentjev2.dr@yandex.ru")
                .login("OlegT2")
                .birthday(LocalDate.of(1995, 12, 3))
                .build();
        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user2))
                        .contentType(MediaType.APPLICATION_JSON));

        Integer userId = 1;
        Integer friendId = 2;

        mockMvc.perform(
                put("/users/{id}/friends/{friendId}", userId, friendId));

        mockMvc.perform(
                        delete("/users/{id}/friends/{friendId}", 3, friendId)
                )

                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof UserDoesNotExistException))
                .andExpect(result -> assertEquals("Пользователя c таким id не существует.",
                        result.getResolvedException().getMessage()));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenDeleteFriendWithInvalidFriendId_whenDeleteFriend_thenStatus404() throws Exception {
        User user = User.builder()
                .name("Oleg")
                .email("terentjev.dr@yandex.ru")
                .login("OlegT")
                .birthday(LocalDate.of(1993, 12, 3))
                .build();
        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON));

        User user2 = User.builder()
                .name("Oleg2")
                .email("terentjev2.dr@yandex.ru")
                .login("OlegT2")
                .birthday(LocalDate.of(1995, 12, 3))
                .build();
        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user2))
                        .contentType(MediaType.APPLICATION_JSON));

        Integer userId = 1;
        Integer friendId = 2;

        mockMvc.perform(
                put("/users/{id}/friends/{friendId}", userId, friendId));

        mockMvc.perform(
                        delete("/users/{id}/friends/{friendId}", userId, 3)
                )

                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof UserDoesNotExistException))
                .andExpect(result -> assertEquals("Пользователя c таким id не существует.",
                        result.getResolvedException().getMessage()));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenGetAllFriends_whenGetAllFriends_thenStatus200AndGetAllFriends() throws Exception {
        User user = User.builder()
                .name("Oleg")
                .email("terentjev.dr@yandex.ru")
                .login("OlegT")
                .birthday(LocalDate.of(1993, 12, 3))
                .build();
        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON));

        User user2 = User.builder()
                .name("Oleg2")
                .email("terentjev2.dr@yandex.ru")
                .login("OlegT2")
                .birthday(LocalDate.of(1995, 12, 3))
                .build();
        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user2))
                        .contentType(MediaType.APPLICATION_JSON));

        Integer userId = 1;
        Integer friendId = 2;

        mockMvc.perform(
                put("/users/{id}/friends/{friendId}", userId, friendId));

        mockMvc.perform(
                        get("/users/{id}/friends", userId))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].email").value("terentjev2.dr@yandex.ru"))
                .andExpect(jsonPath("$[0].login").value("OlegT2"))
                .andExpect(jsonPath("$[0].name").value("Oleg2"))
                .andExpect(jsonPath("$[0].birthday")
                        .value(LocalDate.of(1995, 12, 3).toString()))
                .andExpect(jsonPath("$[0].friends.length()").value(0))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenGetAllFriendsWithInvalidId_whenGetAllFriends_thenStatus404() throws Exception {
        User user = User.builder()
                .name("Oleg")
                .email("terentjev.dr@yandex.ru")
                .login("OlegT")
                .birthday(LocalDate.of(1993, 12, 3))
                .build();
        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON));

        User user2 = User.builder()
                .name("Oleg2")
                .email("terentjev2.dr@yandex.ru")
                .login("OlegT2")
                .birthday(LocalDate.of(1995, 12, 3))
                .build();
        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user2))
                        .contentType(MediaType.APPLICATION_JSON));

        Integer userId = 1;
        Integer friendId = 2;

        mockMvc.perform(
                put("/users/{id}/friends/{friendId}", userId, friendId));

        mockMvc.perform(
                        get("/users/{id}/friends", 3))

                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof UserDoesNotExistException))
                .andExpect(result -> assertEquals("Пользователя c таким id не существует.",
                        result.getResolvedException().getMessage()));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenGetCommonFriends_whenGetCommonFriends_thenStatus200AndGetCommonFriends() throws Exception {
        User user = User.builder()
                .name("Oleg")
                .email("terentjev.dr@yandex.ru")
                .login("OlegT")
                .birthday(LocalDate.of(1993, 12, 3))
                .build();
        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON));

        User user2 = User.builder()
                .name("Oleg2")
                .email("terentjev2.dr@yandex.ru")
                .login("OlegT2")
                .birthday(LocalDate.of(1995, 12, 3))
                .build();
        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user2))
                        .contentType(MediaType.APPLICATION_JSON));

        User user3 = User.builder()
                .name("Oleg3")
                .email("terentjev3.dr@yandex.ru")
                .login("OlegT3")
                .birthday(LocalDate.of(1996, 12, 3))
                .build();
        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user3))
                        .contentType(MediaType.APPLICATION_JSON));

        Integer userId = 1;
        Integer friendId = 2;

        mockMvc.perform(
                put("/users/{id}/friends/{friendId}", userId, 3));

        mockMvc.perform(
                put("/users/{id}/friends/{friendId}", friendId, 3));

        mockMvc.perform(
                        get("/users/{id}/friends/common/{otherId}", userId, friendId))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[0].email").value("terentjev3.dr@yandex.ru"))
                .andExpect(jsonPath("$[0].login").value("OlegT3"))
                .andExpect(jsonPath("$[0].name").value("Oleg3"))
                .andExpect(jsonPath("$[0].birthday")
                        .value(LocalDate.of(1996, 12, 3).toString()))
                .andExpect(jsonPath("$[0].friends.length()").value(0))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenGetCommonFriendsWithInvalidUserId_whenGetCommonFriends_thenStatus404() throws Exception {
        User user = User.builder()
                .name("Oleg")
                .email("terentjev.dr@yandex.ru")
                .login("OlegT")
                .birthday(LocalDate.of(1993, 12, 3))
                .build();
        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON));

        User user2 = User.builder()
                .name("Oleg2")
                .email("terentjev2.dr@yandex.ru")
                .login("OlegT2")
                .birthday(LocalDate.of(1995, 12, 3))
                .build();
        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user2))
                        .contentType(MediaType.APPLICATION_JSON));

        User user3 = User.builder()
                .name("Oleg3")
                .email("terentjev3.dr@yandex.ru")
                .login("OlegT3")
                .birthday(LocalDate.of(1996, 12, 3))
                .build();
        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user3))
                        .contentType(MediaType.APPLICATION_JSON));

        Integer userId = 1;
        Integer friendId = 2;

        mockMvc.perform(
                put("/users/{id}/friends/{friendId}", userId, 3));

        mockMvc.perform(
                put("/users/{id}/friends/{friendId}", friendId, 3));

        mockMvc.perform(
                        get("/users/{id}/friends/common/{otherId}", 4, friendId))

                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof UserDoesNotExistException))
                .andExpect(result -> assertEquals("Пользователя c таким id не существует.",
                        result.getResolvedException().getMessage()));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenGetCommonFriendsWithInvalidFriendId_whenGetCommonFriends_thenStatus404() throws Exception {
        User user = User.builder()
                .name("Oleg")
                .email("terentjev.dr@yandex.ru")
                .login("OlegT")
                .birthday(LocalDate.of(1993, 12, 3))
                .build();
        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON));

        User user2 = User.builder()
                .name("Oleg2")
                .email("terentjev2.dr@yandex.ru")
                .login("OlegT2")
                .birthday(LocalDate.of(1995, 12, 3))
                .build();
        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user2))
                        .contentType(MediaType.APPLICATION_JSON));

        User user3 = User.builder()
                .name("Oleg3")
                .email("terentjev3.dr@yandex.ru")
                .login("OlegT3")
                .birthday(LocalDate.of(1996, 12, 3))
                .build();
        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user3))
                        .contentType(MediaType.APPLICATION_JSON));

        Integer userId = 1;
        Integer friendId = 2;

        mockMvc.perform(
                put("/users/{id}/friends/{friendId}", userId, 3));

        mockMvc.perform(
                put("/users/{id}/friends/{friendId}", friendId, 3));

        mockMvc.perform(
                        get("/users/{id}/friends/common/{otherId}", userId, 4))

                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof UserDoesNotExistException))
                .andExpect(result -> assertEquals("Пользователя c таким id не существует.",
                        result.getResolvedException().getMessage()));
    }
}


