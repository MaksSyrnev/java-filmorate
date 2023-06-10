package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.service.validation.Validation;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
public class UserController {
    private final InMemoryUserStorage userStorage;
    private final Validation validator;

    @Autowired
    public UserController(InMemoryUserStorage userStorage, Validation validator) {
        this.userStorage = userStorage;
        this.validator = validator;
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        log.info("Получен запрос к эндпоинту: GET /users '");
        return userStorage.getAllUser();
    }

    @PostMapping("/users")
    public User addUser(@Valid @RequestBody User user) {
        log.info("Получен запрос к эндпоинту: POST /users ', Строка параметров запроса: '{}'", user);
        validateUser(user, "POST");
        User newUser = userStorage.addUser(user);
        log.info("Ответ на запрос к эндпоинту: POST /users ', '{}'", user);
        return newUser;
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Получен запрос к эндпоинту: PUT /users ', Строка параметров запроса: '{}'", user);
        validateUser(user, "PUT");
        User updUser = userStorage.updateUser(user);
        if (updUser != null) {
            log.info("Ответ на запрос к эндпоинту: PUT /users ', Строка параметров запроса: '{}'", updUser);
            return updUser;
        } else {
            throw new RuntimeException("нет пользователя с таким id"); // ??
        }
    }

    private void validateUser(User user, String method) {
        if ("PUT".equals(method)) {
            Optional<User> userInMemory = userStorage.getUserById(user.getId());
            if (userInMemory.isEmpty()) {
                logAndThrow(user, method);
            }
        }
        String email = user.getEmail();
        if ((email == null) || email.isBlank() || (!validator.isHasEmailSymbol(email))
            || validator.isHasSpaceSymbol(email)) {
            logAndThrow(user, method);
        }
        String login = user.getLogin();
        if ((login == null) || login.isBlank() || validator.isHasSpaceSymbol(login)) {
            logAndThrow(user, method);
        }
        LocalDate date = user.getBirthday();
        if ((date != null) && (!validator.isDateUserOk(date))) {
            logAndThrow(user, method);
        }
    }

    private void logAndThrow(User user, String method) {
        log.error("Ошибка в данных запроса к эндпоинту:{} /users ', : '{}'", method, user);
        throw new ValidationException("некоректные данные пользователя");
    }
}