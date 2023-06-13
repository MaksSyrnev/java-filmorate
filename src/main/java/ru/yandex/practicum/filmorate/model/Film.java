package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

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
    private final Set<Integer> likes = new HashSet<>();

}
