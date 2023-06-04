package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.validation.Validation;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
public class UserController {
    private int id = 0;
    private final HashMap<Integer,User> users = new HashMap<>();
    private final Validation validator = new Validation();

    @GetMapping("/users")
    public List<User> getUsers() {
        log.info("Получен запрос к эндпоинту: GET /users '");
        return new ArrayList<User>(users.values());
    }

    @PostMapping("/users")
    public User addUser(@Valid @RequestBody User user) {
        log.info("Получен запрос к эндпоинту: POST /users ', Строка параметров запроса: '{}'", user);
        validateUser(user, "POST");
        id++;
        user.setId(id);
        String name = user.getName();
        if ((name == null) || name.isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(id, user);
        log.info("Ответ на запрос к эндпоинту: POST /users ', '{}'", user);
        return user;
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Получен запрос к эндпоинту: PUT /users ', Строка параметров запроса: '{}'", user);
        validateUser(user, "PUT");
        int userId = user.getId();
        User currentUser = users.get(userId);
        currentUser.setName(user.getName());
        currentUser.setEmail(user.getEmail());
        currentUser.setBirthday(user.getBirthday());
        currentUser.setLogin(user.getLogin());
        log.info("Ответ на запрос к эндпоинту: PUT /users ', Строка параметров запроса: '{}'", users.get(userId));
        return users.get(userId);
    }

    private void validateUser(User user, String method) {
        if ("PUT".equals(method)) {
            if (!users.containsKey(user.getId())) {
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