package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exception.MpaDoesNotExistException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class MpaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void givenFindMpa_whenFind_thenStatus200AndFindMpa() throws Exception {
        mockMvc.perform(
                        get("/mpa")
                )

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("G"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("PG"))
                .andExpect(jsonPath("$[2].id").value(3))
                .andExpect(jsonPath("$[2].name").value("PG-13"))
                .andExpect(jsonPath("$[3].id").value(4))
                .andExpect(jsonPath("$[3].name").value("R"))
                .andExpect(jsonPath("$[4].id").value(5))
                .andExpect(jsonPath("$[4].name").value("NC-17"));
    }

    @Test
    public void givenFindMpaById_whenFind_thenStatus200AndFindMpaById() throws Exception {
        Integer id = 1;

        mockMvc.perform(
                        get("/mpa/{id}", id)
                )

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("G"));
    }

    @Test
    public void givenFindMpaByInvalidId_whenFind_thenStatus400() throws Exception {
        Integer id = 6;

        mockMvc.perform(
                        get("/mpa/{id}", id)
                )

                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MpaDoesNotExistException))
                .andExpect(result -> assertEquals("Рейтинга c таким id не существует.",
                        result.getResolvedException().getMessage()));
    }
}
