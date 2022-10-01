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
import ru.yandex.practicum.filmorate.exception.FilmValidationException;
import ru.yandex.practicum.filmorate.exception.LikeDoesNotExistException;
import ru.yandex.practicum.filmorate.exception.UserValidationException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

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
//new tests
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenGetFilmById_whenGetFilm_thenStatus200AndGetFilmById() throws Exception {
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

        Integer id = 1;

        mockMvc.perform(
                        get("/films/{id}", id))

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
    void givenGetFilmByInvalidId_whenGetFilm_thenStatus400() throws Exception {
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

        Integer id = 2;

        mockMvc.perform(
                        get("/films/{id}", id))

                .andExpect(status().isNotFound());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenLikeFilmWithValidFilmAndUser_whenLike_thenStatus200AndLikeFilm() throws Exception {
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

        Integer filmId = 1;
        Integer userId = 1;

        mockMvc.perform(
                        put("/films/{id}/like/{userId}", filmId, userId))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Saw"))
                .andExpect(jsonPath("$.description").value("Saw description."))
                .andExpect(jsonPath("$.releaseDate")
                        .value(LocalDate.of(2003, 2, 13).toString()))
                .andExpect(jsonPath("$.duration").value(100))
                .andExpect(jsonPath("$.likes.length()").value(1))
                .andExpect(jsonPath("$.likes[0]").value(1));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenLikeFilmWithInvalidFilmId_whenLike_thenStatus404() throws Exception {
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

        Integer filmId = 2;
        Integer userId = 1;

        mockMvc.perform(
                        put("/films/{id}/like/{userId}", filmId, userId))

                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof FilmValidationException))
                .andExpect(result -> assertEquals("Фильм с таким id не существует.",
                        result.getResolvedException().getMessage()));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenLikeFilmWithInvalidUserId_whenLike_thenStatus404() throws Exception {
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

        Integer filmId = 1;
        Integer userId = 2;

        mockMvc.perform(
                        put("/films/{id}/like/{userId}", filmId, userId))

                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserValidationException))
                .andExpect(result -> assertEquals("Пользователь с таким id не существует.",
                        result.getResolvedException().getMessage()));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenDislikeFilmWithValidFilmAndUser_whenDislike_thenStatus200AndDislikeFilm() throws Exception {
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

        Integer filmId = 1;
        Integer userId = 1;

        mockMvc.perform(
                        put("/films/{id}/like/{userId}", filmId, userId));

        mockMvc.perform(
                        delete("/films/{id}/like/{userId}", filmId, userId))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Saw"))
                .andExpect(jsonPath("$.description").value("Saw description."))
                .andExpect(jsonPath("$.releaseDate")
                        .value(LocalDate.of(2003, 2, 13).toString()))
                .andExpect(jsonPath("$.duration").value(100))
                .andExpect(jsonPath("$.likes.length()").value(0));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenDislikeFilmWithoutLike_whenDislike_thenStatus400() throws Exception {
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

        Integer filmId = 1;
        Integer userId = 1;

        mockMvc.perform(
                        delete("/films/{id}/like/{userId}", filmId, userId))

                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof LikeDoesNotExistException))
                .andExpect(result -> assertEquals("Пользователь с id: " + userId +
                                " хочет удалить лайк, который он не ставил, у фильма с id: " + filmId + ".",
                        result.getResolvedException().getMessage()));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenDislikeFilmWithInvalidFilmId_whenDislike_thenStatus404() throws Exception {
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

        Integer filmId = 1;
        Integer userId = 1;

        mockMvc.perform(
                put("/films/{id}/like/{userId}", filmId, userId));

        mockMvc.perform(
                        delete("/films/{id}/like/{userId}", 2, userId))

                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof FilmValidationException))
                .andExpect(result -> assertEquals("Фильм с таким id не существует.",
                        result.getResolvedException().getMessage()));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenDislikeFilmWithInvalidUserId_whenDislike_thenStatus404() throws Exception {
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

        Integer filmId = 1;
        Integer userId = 1;

        mockMvc.perform(
                put("/films/{id}/like/{userId}", filmId, userId));

        mockMvc.perform(
                        delete("/films/{id}/like/{userId}", filmId, 2))

                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof UserValidationException))
                .andExpect(result -> assertEquals("Пользователь с таким id не существует.",
                        result.getResolvedException().getMessage()));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenMostPopularFilmWithCount_whenCount_thenStatus200AndGetMostPopularFilm() throws Exception {
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

        Film film2 = Film.builder()
                .name("Saw2")
                .description("Saw2 description.")
                .releaseDate(LocalDate.of(2004, 3, 13))
                .duration(105)
                .build();
        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(film2))
                        .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(
                get("/films/popular")
                        .param("count", "1"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenMostPopularFilmWithoutCount_whenWithoutCount_thenStatus200AndGetMostPopularFilm() throws Exception {
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

        Film film2 = Film.builder()
                .name("Saw2")
                .description("Saw2 description.")
                .releaseDate(LocalDate.of(2004, 3, 13))
                .duration(105)
                .build();
        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(film2))
                        .contentType(MediaType.APPLICATION_JSON));

        Film film3 = Film.builder()
                .name("Saw3")
                .description("Saw3 description.")
                .releaseDate(LocalDate.of(2005, 5, 13))
                .duration(105)
                .build();
        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(film3))
                        .contentType(MediaType.APPLICATION_JSON));

        Film film4 = Film.builder()
                .name("Saw4")
                .description("Saw4 description.")
                .releaseDate(LocalDate.of(2006, 3, 13))
                .duration(105)
                .build();
        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(film4))
                        .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(
                        get("/films/popular"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenMostPopularFilmWithCountZero_whenCountZero_thenStatus400() throws Exception {
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

        Film film2 = Film.builder()
                .name("Saw2")
                .description("Saw2 description.")
                .releaseDate(LocalDate.of(2004, 3, 13))
                .duration(105)
                .build();
        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(film2))
                        .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(
                        get("/films/popular")
                                .param("count", "0"))

                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof IllegalArgumentException))
                .andExpect(result -> assertEquals("Значение count должно быть больше 0.",
                        result.getResolvedException().getMessage()));
    }
}
