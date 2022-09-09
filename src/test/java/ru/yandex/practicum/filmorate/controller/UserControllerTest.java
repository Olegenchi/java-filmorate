package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserController userController;

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenNewValidUser_whenCreated_thenStatus200AndUserCreated() throws Exception {
        User user = User.builder()
                .name("Oleg")
                .email("terentjev.dr@yandex.ru")
                .login("OlegT")
                .birthday(LocalDate.of(1993, 12, 3))
                .build();

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON))


                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("terentjev.dr@yandex.ru"))
                .andExpect(jsonPath("$.login").value("OlegT"))
                .andExpect(jsonPath("$.name").value("Oleg"))
                .andExpect(jsonPath("$.birthday")
                        .value(LocalDate.of(1993, 12, 3).toString()));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenNewUserWithEmptyName_whenCreated_thenAddUserWithNameEqualsLogin() throws Exception {
        User user = User.builder()
                .name("")
                .email("terentjev.dr@yandex.ru")
                .login("OlegT")
                .birthday(LocalDate.of(1993, 12, 3))
                .build();

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON))


                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("terentjev.dr@yandex.ru"))
                .andExpect(jsonPath("$.login").value("OlegT"))
                .andExpect(jsonPath("$.name").value("OlegT"))
                .andExpect(jsonPath("$.birthday")
                        .value(LocalDate.of(1993, 12, 3).toString()));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenNewUserWithEmptyEmail_whenCreated_thenStatus400() throws Exception {
        User user = User.builder()
                .name("Oleg")
                .email("")
                .login("OlegT")
                .birthday(LocalDate.of(1993, 12, 3))
                .build();

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenNewUserWithInCorrectEmail_whenCreated_thenStatus400() throws Exception {
        User user = User.builder()
                .name("Oleg")
                .email("terentjev.dr@")
                .login("OlegT")
                .birthday(LocalDate.of(1993, 12, 3))
                .build();

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest());

    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenNewUserWithSpaceInLogin_whenCreated_thenStatus400() throws Exception {
        User user = User.builder()
                .name("Oleg")
                .email("terentjev.dr@yandex.ru")
                .login("Oleg T")
                .birthday(LocalDate.of(1993, 12, 3))
                .build();

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest());

    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenNewUserWithBirthdayInFuture_whenCreated_thenStatus400() throws Exception {
        User user = User.builder()
                .name("Oleg")
                .email("terentjev.dr@yandex.ru")
                .login("Oleg T")
                .birthday(LocalDate.of(2033, 12, 3))
                .build();

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest());

    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenUpdateValidUser_whenUpdated_thenStatus200AndUserUpdated() throws Exception {
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

        User userUpdate = User.builder()
                .id(1)
                .name("Oleg_new")
                .email("terentjevNew@yandex.ru")
                .login("OlegT_new")
                .birthday(LocalDate.of(1993, 12, 3))
                .build();

        mockMvc.perform(
                        put("/users")
                                .content(objectMapper.writeValueAsString(userUpdate))
                                .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("terentjevNew@yandex.ru"))
                .andExpect(jsonPath("$.login").value("OlegT_new"))
                .andExpect(jsonPath("$.name").value("Oleg_new"))
                .andExpect(jsonPath("$.birthday")
                        .value(LocalDate.of(1993, 12, 3).toString()));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenUpdateUserWithEmptyName_whenUpdated_thenUpdateUserWithNameEqualsLogin() throws Exception {
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

        User userUpdate = User.builder()
                .id(1)
                .name("")
                .email("terentjevNew@yandex.ru")
                .login("OlegT_new")
                .birthday(LocalDate.of(1993, 12, 3))
                .build();

        mockMvc.perform(
                        put("/users")
                                .content(objectMapper.writeValueAsString(userUpdate))
                                .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("terentjevNew@yandex.ru"))
                .andExpect(jsonPath("$.login").value("OlegT_new"))
                .andExpect(jsonPath("$.name").value("OlegT_new"))
                .andExpect(jsonPath("$.birthday")
                        .value(LocalDate.of(1993, 12, 3).toString()));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenUpdateUserWithEmptyEmail_whenUpdated_thenStatus400() throws Exception {
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

        User userUpdate = User.builder()
                .id(1)
                .name("Oleg")
                .email("")
                .login("OlegT")
                .birthday(LocalDate.of(1993, 12, 3))
                .build();

        mockMvc.perform(
                        put("/users")
                                .content(objectMapper.writeValueAsString(userUpdate))
                                .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenUpdateUserWithIncorrectEmail_whenUpdated_thenStatus400() throws Exception {
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

        User userUpdate = User.builder()
                .id(1)
                .name("Oleg")
                .email("terentjev.dr@")
                .login("OlegT")
                .birthday(LocalDate.of(1993, 12, 3))
                .build();

        mockMvc.perform(
                        put("/users")
                                .content(objectMapper.writeValueAsString(userUpdate))
                                .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest());
    }


    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenUpdateUserWithSpaceInLogin_whenUpdated_thenStatus400() throws Exception {
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

        User userUpdate = User.builder()
                .id(1)
                .name("Oleg")
                .email("terentjev.dr@yandex.ru")
                .login("Oleg T")
                .birthday(LocalDate.of(1993, 12, 3))
                .build();

        mockMvc.perform(
                        put("/users")
                                .content(objectMapper.writeValueAsString(userUpdate))
                                .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenUpdateUserWithBirthdayInFuture_whenUpdated_thenStatus400() throws Exception {
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

        User userUpdate = User.builder()
                .id(1)
                .name("Oleg")
                .email("terentjev.dr@yandex.ru")
                .login("Oleg T")
                .birthday(LocalDate.of(2033, 12, 3))
                .build();

        mockMvc.perform(
                        put("/users")
                                .content(objectMapper.writeValueAsString(userUpdate))
                                .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest());
    }
}