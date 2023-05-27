package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Validation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private int id = 0;
    private final HashMap<Integer,User> users = new HashMap<>();

    @GetMapping
    public List<User> getUsers() {
        return new ArrayList<User>(users.values());
    }

    @PostMapping
    public User addUser(@RequestBody User user) {
        if(!isUserDataValidate(user)) {
            log.error("Получен POST запрос к эндпоинту: /users ', Строка параметров запроса: '{}'", user);
            throw new ValidationException("некоректные данные пользователя");
        }
        log.info("Получен POST запрос к эндпоинту: /users ', Строка параметров запроса: '{}'", user);
        id++;
        user.setId(id);
        String name = user.getName();
        if((name == null) || name.isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(id, user);
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        if(!users.containsKey(user.getId())) {
            log.error("Получен PUT запрос к эндпоинту: /users ', Строка параметров запроса: '{}'", user);
            throw new RuntimeException("нет пользователя с таким id");
        }
        if(!isUserDataValidate(user)) {
            log.error("Получен PUT запрос к эндпоинту: /users ', Строка параметров запроса: '{}'", user);
            throw new ValidationException("некоректные данные пользователя");
        }
        log.info("Получен PUT запрос к эндпоинту: /users ', Строка параметров запроса: '{}'", user);
        users.put(user.getId(), user);
        return user;
    }

    private boolean isUserDataValidate(User user) {
        String email = user.getEmail();
        if((email == null) || email.isBlank() || (!Validation.isHasEmailSymbol(email))
            || Validation.isHasSpaceSymbol(email)) {
            return false;
        }
        String login = user.getLogin();
        if((login == null) || login.isBlank() || Validation.isHasSpaceSymbol(login)) {
            return false;
        }
        LocalDate date = user.getBirthday();
        if((date != null) && (!Validation.isDateUserOk(date))) {
                return false;
        }
        return true;
    }
}