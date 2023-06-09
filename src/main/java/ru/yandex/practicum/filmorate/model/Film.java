package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import javax.validation.constraints.NotBlank;


@RequiredArgsConstructor
@Data
public class Film {
    private int id;
    @NotBlank
    private String name;
    private String description;
    private LocalDate releaseDate;
    private long duration;
    @JsonIgnore
    private final Set<Integer> likes = new HashSet<>();

}
