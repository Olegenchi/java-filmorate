package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class FilmControllerTest {

/*  @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;
*/
    @Autowired
    protected FilmController filmController;

    protected Film film;

    @BeforeEach
    void setUp() {
        film = Film.builder()
                .name("Saw")
                .description("Мелкий пацан катается на велике и развлекает детектива.")
                .releaseDate(LocalDate.of(2003, 2, 13))
                .duration(100)
                .build();
        filmController.createFilm(film);
    }

    @AfterEach
    void clear() {
        filmController.getFilmList().clear();
    }

    @Test
    void shouldCreateFilm() {
        assertEquals(film, filmController.getFilmList().get(1));
    }

    @Test
    void shouldValidateWithNormalConditions() {
        assertTrue(filmController.filmValidation(film));
    }

    @Test
    void shouldThrowValidateException() {
        film.setReleaseDate(LocalDate.of(1800, 12, 28));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.filmValidation(film));

        assertEquals("Дата релиза не может быть раньше 28.12.1895 г.", exception.getMessage());
    }

    @Test
    void shouldUpdateFilm() {

        film.setName("Saw 2");
        filmController.updateFilm(film);

        assertEquals("Saw 2", film.getName());
    }

    @Test
    void shouldValueInTheFilmList() {
        filmController.createFilm(film);

        assertEquals(1, filmController.getFilmList().size());
    }
}
