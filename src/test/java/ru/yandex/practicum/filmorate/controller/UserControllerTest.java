package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

/*    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;
*/
    @Autowired
    private UserController userController;

    protected User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .name("Oleg")
                .email("terentjev.dr@yandex.ru")
                .login("OlegT")
                .birthday(LocalDate.of(1993, 12, 3))
                .build();
        userController.createUser(user);
    }

    @AfterEach
    void clear() {
        userController.getUserList().clear();
    }

    @Test
    void shouldCreateUser() {
        assertEquals(user, userController.getUserList().get(1));
    }

    @Test
    void shouldUpdateUser() {
        user.setName("Saw 2");
        userController.updateUser(user);

        assertEquals(user, userController.getUserList().get(1));
    }

    @Test
    void shouldValueInTheUserList() {
        assertEquals(1, userController.getUserList().size());
    }

}