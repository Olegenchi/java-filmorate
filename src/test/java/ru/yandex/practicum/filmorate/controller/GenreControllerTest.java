package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exception.GenreDoesNotExistException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class GenreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void givenFindGenre_whenFind_thenStatus200AndFindGenre() throws Exception {
        mockMvc.perform(
                        get("/genres")
                )

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(6))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Комедия"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Драма"))
                .andExpect(jsonPath("$[2].id").value(3))
                .andExpect(jsonPath("$[2].name").value("Мультфильм"))
                .andExpect(jsonPath("$[3].id").value(4))
                .andExpect(jsonPath("$[3].name").value("Триллер"))
                .andExpect(jsonPath("$[4].id").value(5))
                .andExpect(jsonPath("$[4].name").value("Документальный"))
                .andExpect(jsonPath("$[5].id").value(6))
                .andExpect(jsonPath("$[5].name").value("Боевик"));
    }

    @Test
    public void givenFindGenreById_whenFind_thenStatus200AndFindGenreById() throws Exception {
        Integer id = 1;

        mockMvc.perform(
                        get("/genres/{id}", id)
                )

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Комедия"));
    }

    @Test
    public void givenFindGenreByInvalidId_whenFind_thenStatus400() throws Exception {
        Integer id = 7;

        mockMvc.perform(
                        get("/genres/{id}", id)
                )

                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof GenreDoesNotExistException))
                .andExpect(result -> assertEquals("Жанра c таким id не существует.",
                        result.getResolvedException().getMessage()));
    }
}
