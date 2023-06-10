package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.service.validation.Validation;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
public class FilmController {
    private final InMemoryFilmStorage filmStorage;
    private final Validation validator;

    @Autowired
    public FilmController(InMemoryFilmStorage filmStorage, Validation validator) {
        this.filmStorage = filmStorage;
        this.validator = validator;
    }

    @GetMapping("/films")
    public List<Film> getFilms() {
        log.info("Получен запрос к эндпоинту: GET /films '");
        return filmStorage.getAllFilms();
    }

    @PostMapping("/films")
    public Film addFilm(@Valid @RequestBody  Film film) {
        log.info("Получен запрос к эндпоинту: POST /films ', Строка параметров запроса: '{}'", film);
        validateFilm(film, "POST");
        Film newFilm = filmStorage.addFilm(film);
        log.info("Ответ на запрос к эндпоинту: POST /films ', : '{}'", film);
        return newFilm;
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("запрос к эндпоинту: PUT /films ', Строка параметров запроса: '{}'", film);
        validateFilm(film, "PUT");
        Film updFilm = filmStorage.updateFilm(film);
        if (updFilm != null) {
            log.info("Ответ на запрос к эндпоинту: PUT /films ', '{}'", updFilm);
            return updFilm;
        } else {
            throw new RuntimeException("wrong id");
        }
    }

    private void validateFilm(Film film, String method)  {
        if ("PUT".equals(method)) {
            Optional<Film> filmInMemory = filmStorage.getFilmById(film.getId());
            if (filmInMemory.isEmpty()) {
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