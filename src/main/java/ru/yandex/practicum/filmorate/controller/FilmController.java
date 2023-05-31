package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.validation.Validation;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
public class FilmController {
    private int id = 0;
    private final HashMap<Integer, Film> films = new HashMap<>();
    private final Validation validator = new Validation();

    @GetMapping("/films")
    public List<Film> getFilms() {
        log.info("Получен запрос к эндпоинту: GET /films '");
        return new ArrayList<Film>(films.values());
    }

    @PostMapping("/films")
    public Film addFilm(@Valid @RequestBody  Film film) {
        log.info("Получен запрос к эндпоинту: POST /films ', Строка параметров запроса: '{}'", film);
        validateFilm(film, "POST");
        id++;
        film.setId(id);
        films.put(id,film);
        log.info("Ответ на запрос к эндпоинту: POST /films ', : '{}'", film);
        return film;
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("запрос к эндпоинту: PUT /films ', Строка параметров запроса: '{}'", film);
        validateFilm(film, "PUT");
        int filmId = film.getId();
        Film currentFilm = films.get(filmId);
        currentFilm.setName(film.getName());
        currentFilm.setDescription(film.getDescription());
        currentFilm.setReleaseDate(film.getReleaseDate());
        currentFilm.setDuration(film.getDuration());
        log.info("Ответ на запрос к эндпоинту: PUT /films ', '{}'", films.get(filmId));
        return films.get(filmId);
    }

    private void validateFilm(Film film, String method)  {
        if ("PUT".equals(method)) {
            if (!films.containsKey(film.getId())) {
                logAndThrow(film,method);
            }
        }
        if ((film.getName() == null) || film.getName().isBlank()) {
            logAndThrow(film,method);
        }
        String description = film.getDescription();
        if (!validator.isLengthOk(description)) {
            logAndThrow(film,method);
        }
        LocalDate date = film.getReleaseDate();
        if (!validator.isDateFilmOk(date)) {
            logAndThrow(film,method);
        }
        if (film.getDuration() < 0) {
            logAndThrow(film,method);
        }
    }

    private void logAndThrow(Film film, String method) {
        log.error("Ошибка в данных запроса к эндпоинту:{} /films ', : '{}'", method, film);
        throw new ValidationException("ошибка в данных фильма");
    }
}