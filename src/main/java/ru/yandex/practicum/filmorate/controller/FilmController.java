package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.exeption.ValidationException;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Validation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private int id = 0;
    private final HashMap<Integer, Film> films = new HashMap<>();

    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList<Film>(films.values());
    }

    @PostMapping()
    public Film addFilm(@RequestBody Film film) {
        if (!isValidateFilm(film)) {
            log.error("Получен POST запрос к эндпоинту: /films ', Строка параметров запроса: '{}'", film);
            throw new ValidationException("ошибка в данных фильма");
        }
        log.info("Получен POST запрос к эндпоинту: /films ', Строка параметров запроса: '{}'", film);
        id++;
        film.setId(id);
        films.put(id,film);
        return film;
    }

    @PutMapping()
    public Film updateFilm(@RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            log.error("Получен PUT запрос к эндпоинту: /films ', Строка параметров запроса: '{}'", film);
            throw new ValidationException("неверный id " + film.getId() + "фильма");
        }
        if (!isValidateFilm(film)) {
            log.error("Получен PUT запрос к эндпоинту: /films ', Строка параметров запроса: '{}'", film);
            throw new ValidationException("ошибка в данных фильма");
        }
        log.info("Получен PUT запрос к эндпоинту: /films ', Строка параметров запроса: '{}'", film);
        films.put(film.getId(), film);
        return film;
    }

    private boolean isValidateFilm(Film film)  {
        if ((film.getName() == null) || film.getName().isBlank()) {
            return false;
        }
        String description = film.getDescription();
        if (!Validation.isLengthOk(description)) {
            return false;
        }
        LocalDate date = film.getReleaseDate();
        if (!Validation.isDateFilmOk(date)){
            return false;
        }
        if (film.getDuration() < 0) {
            return false;
        }
        return true;
    }
}