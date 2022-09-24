package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.film.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FilmControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    protected FilmController filmController;

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenNewValidFilm_whenCreated_thenStatus200AndFilmCreated() throws Exception {
        Film film = Film.builder()
                .name("Saw")
                .description("Saw description.")
                .releaseDate(LocalDate.of(2003, 2, 13))
                .duration(100)
                .build();

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Saw"))
                .andExpect(jsonPath("$.description").value("Saw description."))
                .andExpect(jsonPath("$.releaseDate")
                        .value(LocalDate.of(2003, 2, 13).toString()))
                .andExpect(jsonPath("$.duration").value(100));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenNewFilmWithEmptyName_whenCreated_thenStatus400() throws Exception {
        Film film = Film.builder()
                .name("")
                .description("Saw description.")
                .releaseDate(LocalDate.of(2003, 2, 13))
                .duration(100)
                .build();

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenNewFilmWithInvalidDescription_whenCreated_thenStatus400() throws Exception {
        Film film = Film.builder()
                .name("Saw")
                .description("Two strangers awaken in a room with no recollection of how they got there," +
                        " and soon discover they're pawns in a deadly game perpetrated" +
                        " by a notorious serial killer. Waking up in a bathroom, two men," +
                        " Adam and Dr. Lawrence Gordon, discover they have been captured" +
                        " by the infamous Jigsaw Killer.")
                .releaseDate(LocalDate.of(2003, 2, 13))
                .duration(100)
                .build();

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenNewFilmWithInvalidReleaseDate_whenCreated_thenStatus400() throws Exception {
        Film film = Film.builder()
                .name("Saw")
                .description("Saw description.")
                .releaseDate(LocalDate.of(1700, 2, 13))
                .duration(100)
                .build();

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ValidationException))
                .andExpect(result -> assertEquals("Дата релиза не может быть раньше 28.12.1895 г.",
                        result.getResolvedException().getMessage()));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenNewFilmWithNegativeDuration_whenCreated_thenStatus400() throws Exception {
        Film film = Film.builder()
                .name("Saw")
                .description("Saw description.")
                .releaseDate(LocalDate.of(2003, 2, 13))
                .duration(-5)
                .build();

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenUpdateValidFilm_whenUpdated_thenStatus200AndFilmUpdated() throws Exception {
        Film film = Film.builder()
                .name("Saw")
                .description("Saw description.")
                .releaseDate(LocalDate.of(2003, 2, 13))
                .duration(100)
                .build();
        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON));

        Film filmUpdate = Film.builder()
                .id(1)
                .name("SawNew")
                .description("SawNew description.")
                .releaseDate(LocalDate.of(2004, 4, 15))
                .duration(105)
                .build();
        mockMvc.perform(
                        put("/films")
                                .content(objectMapper.writeValueAsString(filmUpdate))
                                .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("SawNew"))
                .andExpect(jsonPath("$.description").value("SawNew description."))
                .andExpect(jsonPath("$.releaseDate")
                        .value(LocalDate.of(2004, 4, 15).toString()))
                .andExpect(jsonPath("$.duration").value(105));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenUpdateFilmWithEmptyName_whenUpdated_thenStatus400() throws Exception {
        Film film = Film.builder()
                .name("Saw")
                .description("Saw description.")
                .releaseDate(LocalDate.of(2003, 2, 13))
                .duration(100)
                .build();
        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON));

        Film filmUpdate = Film.builder()
                .id(1)
                .name("")
                .description("SawNew description.")
                .releaseDate(LocalDate.of(2004, 4, 15))
                .duration(105)
                .build();
        mockMvc.perform(
                        put("/films")
                                .content(objectMapper.writeValueAsString(filmUpdate))
                                .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenUpdateFilmWithInvalidDescription_whenUpdated_thenStatus400() throws Exception {
        Film film = Film.builder()
                .name("Saw")
                .description("Saw description.")
                .releaseDate(LocalDate.of(2003, 2, 13))
                .duration(100)
                .build();
        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON));

        Film filmUpdate = Film.builder()
                .id(1)
                .name("SawNew")
                .description("Two strangers awaken in a room with no recollection of how they got there," +
                        " and soon discover they're pawns in a deadly game perpetrated" +
                        " by a notorious serial killer. Waking up in a bathroom, two men," +
                        " Adam and Dr. Lawrence Gordon, discover they have been captured" +
                        " by the infamous Jigsaw Killer.")
                .releaseDate(LocalDate.of(2004, 4, 15))
                .duration(105)
                .build();
        mockMvc.perform(
                        put("/films")
                                .content(objectMapper.writeValueAsString(filmUpdate))
                                .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenUpdateFilmWithInvalidReleaseDate_whenUpdated_thenStatus400() throws Exception {
        Film film = Film.builder()
                .name("Saw")
                .description("Saw description.")
                .releaseDate(LocalDate.of(2003, 2, 13))
                .duration(100)
                .build();
        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON));

        Film filmUpdate = Film.builder()
                .id(1)
                .name("SawNew")
                .description("SawNew description.")
                .releaseDate(LocalDate.of(1700, 2, 13))
                .duration(105)
                .build();

        mockMvc.perform(
                        put("/films")
                                .content(objectMapper.writeValueAsString(filmUpdate))
                                .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ValidationException))
                .andExpect(result -> assertEquals("Дата релиза не может быть раньше 28.12.1895 г.",
                        result.getResolvedException().getMessage()));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenUpdateFilmWithNegativeDuration_whenUpdated_thenStatus400() throws Exception {
        Film film = Film.builder()
                .name("Saw")
                .description("Saw description.")
                .releaseDate(LocalDate.of(2003, 2, 13))
                .duration(100)
                .build();
        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON));

        Film filmUpdate = Film.builder()
                .id(1)
                .name("SawNew")
                .description("SawNew description.")
                .releaseDate(LocalDate.of(2004, 4, 15))
                .duration(-10)
                .build();
        mockMvc.perform(
                        put("/films")
                                .content(objectMapper.writeValueAsString(filmUpdate))
                                .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenFindFilm_whenFind_thenStatus200AndFindFilm() throws Exception {
        Film film = Film.builder()
                .name("Saw")
                .description("Saw description.")
                .releaseDate(LocalDate.of(2003, 2, 13))
                .duration(100)
                .build();
        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(
                        get("/films"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Saw"))
                .andExpect(jsonPath("$[0].description").value("Saw description."))
                .andExpect(jsonPath("$[0].releaseDate")
                        .value(LocalDate.of(2003, 2, 13).toString()))
                .andExpect(jsonPath("$[0].duration").value(100));
    }
}
