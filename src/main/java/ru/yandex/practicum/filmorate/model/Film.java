package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.*;

@AllArgsConstructor
@Data
@Builder
public class Film {

    private Integer id;
    @NotNull
    @NotBlank
    private String name;
    @NotNull
    @Size(max = 200)
    private String description;
    @NotNull
    @Past
    private LocalDate releaseDate;
    @NotNull
    @Positive
    private long duration;
    private Integer rate;
    @NotNull
    private Mpa mpa;
    private final Set<Genre> genres = new TreeSet<>(Comparator.comparingInt(Genre::getId));
    private final Set<Integer> likes = new HashSet<>();

    public Map<String,Object> filmToMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("film_name", name);
        values.put("film_description", description);
        values.put("film_release_date", releaseDate);
        values.put("film_duration", duration);
        values.put("film_rate", rate);
        values.put("mpa_id", mpa.getId());
        return values;
    }
}
