package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@AllArgsConstructor
@Data
@Builder
public class User {

    private Integer id;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Pattern(regexp = "^\\S*$")
    private String login;
    private String name;
    @Past
    private LocalDate birthday;
    private final Set<Integer> friends = new HashSet<>();

    public Map<String,Object> userToMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("user_email", email);
        values.put("user_name", name);
        values.put("user_login", login);
        values.put("user_birthday", birthday);
        return values;
    }
}
