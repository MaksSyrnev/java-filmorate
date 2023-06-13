package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import ru.yandex.practicum.filmorate.exeption.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.service.validation.Validation;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
public class FilmController {
    private final Validation validator;
    private final FilmService filmService;

    @Autowired
    public FilmController(Validation validator, FilmService filmService) {
        this.validator = validator;
        this.filmService = filmService;
    }

    @GetMapping("/films")
    public List<Film> getFilms() {
        log.info("Получен запрос к эндпоинту: GET /films '");
        return filmService.getAllFilms();
    }

    @GetMapping("/films/{id}")
    public Film getFilms(@PathVariable int id) {
        log.info("Получен запрос к эндпоинту: GET /films/'{}' '", id);
        Optional<Film> film = filmService.getFilmById(id);
        if (film.isEmpty()) {
            throw new IncorrectIdException("wrong id");
        }
        return film.get();
    }

    @GetMapping("/films/popular") //?count={count}
    public List<Film> getPopularFilms(@RequestParam Optional<Integer> count) {
        int countValue = 0;
        if (count.isEmpty()) {
            countValue = 10;
        } else {
            countValue = count.get();
        }
        log.info("Получен запрос к эндпоинту: GET /films/popular?count={} '", countValue);
        List<Film> films = filmService.getTopFilms(countValue);
        return films;
    }

    @PostMapping("/films")
    public Film addFilm(@Valid @RequestBody  Film film) {
        log.info("Получен запрос к эндпоинту: POST /films ', Строка параметров запроса: '{}'", film);
        validateFilm(film, "POST");
        Film newFilm = filmService.addFilm(film);
        log.info("Ответ на запрос к эндпоинту: POST /films ', : '{}'", film);
        return newFilm;
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("запрос к эндпоинту: PUT /films ', Строка параметров запроса: '{}'", film);
        validateFilm(film, "PUT");
        Film updFilm = filmService.updateFilm(film);
        if (updFilm != null) {
            log.info("Ответ на запрос к эндпоинту: PUT /films ', '{}'", updFilm);
            return updFilm;
        } else {
            throw new IncorrectIdException("wrong id");
        }
    }

    @PutMapping("/films/{id}/like/{userId}")
    public Film addLikeFilm(@PathVariable int id, @PathVariable int userId) {
        log.info("запрос к эндпоинту: PUT /films/{}/like/{} ', Строка параметров запроса: ", id, userId);
        filmService.addLike(id, userId);
        Optional<Film> film = filmService.getFilmById(id);
        if (film.isPresent()) {
            return film.get();
        } else {
            throw new IncorrectIdException("wrong id");
        }
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public Film deleteLikeFilm(@PathVariable int id, @PathVariable int userId) {
        log.info("запрос к эндпоинту: DELETE /films/'{}'/like/'{}' ', Строка параметров запроса: ", id, userId);
        filmService.deleteLike(id, userId);
        Optional<Film> film = filmService.getFilmById(id);
        if (film.isPresent()) {
            return film.get();
        } else {
            throw new IncorrectIdException("wrong id");
        }
    }

    private void validateFilm(Film film, String method)  {
        if ("PUT".equals(method)) {
            Optional<Film> filmInMemory = filmService.getFilmById(film.getId());
            if (filmInMemory.isEmpty()) {
                log.error("Ошибка в данных запроса к эндпоинту:{} /films ', : '{}'", method, film);
                throw new IncorrectIdException("неверный id фильма");
            }
        }
        if ((film.getName() == null) || film.getName().isBlank()) {
            log.error("Ошибка в данных запроса к эндпоинту:{} /films ', : '{}'", method, film);
            throw new ValidationException("имя фильма не должно быть пустым");
        }
        String description = film.getDescription();
        if (!validator.isLengthOk(description)) {
            log.error("Ошибка в данных запроса к эндпоинту:{} /films ', : '{}'", method, film);
            throw new ValidationException("слишком длинное описание фильма");
        }
        LocalDate date = film.getReleaseDate();
        if (!validator.isDateFilmOk(date)) {
            log.error("Ошибка в данных запроса к эндпоинту:{} /films ', : '{}'", method, film);
            throw new ValidationException("дата релиза некоректная");
        }
        if (film.getDuration() < 0) {
            log.error("Ошибка в данных запроса к эндпоинту:{} /films ', : '{}'", method, film);
            throw new ValidationException("продолжительность фильма должна быть положительным числом");
        }
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationError(final ValidationException e) {
        return Map.of("ValidationFilmError", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleValidationError(final IncorrectIdException e) {
        return Map.of("IncorrectId", e.getMessage());
    }
}