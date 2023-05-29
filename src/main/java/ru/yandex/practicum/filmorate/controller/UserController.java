package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Validation;

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
    Validation validator = new Validation();

    @GetMapping("/users")
    public List<User> getUsers() {
        return new ArrayList<User>(users.values());
    }

    @PostMapping("/users")
    public User addUser(@Valid @RequestBody User user) {
        log.info("Получен запрос к эндпоинту: POST /users ', Строка параметров запроса: '{}'", user);
        if (!isUserDataValidate(user)) {
            log.error("Ошибка в данных, запрос к эндпоинту:POST /users ', '{}'", user);
            throw new ValidationException("некоректные данные пользователя");
        }
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
        if (!users.containsKey(user.getId())) {
            log.error("Неверный id в данных запроса к эндпоинту: PUT /users ', Строка параметров запроса: '{}'", user);
            throw new RuntimeException("нет пользователя с таким id");
        }
        if (!isUserDataValidate(user)) {
            log.error("Ошибка в данных запроса к эндпоинту: PUT /users ', Строка параметров запроса: '{}'", user);
            throw new ValidationException("некоректные данные пользователя");
        }
        int userId = user.getId();
        User currentUser = users.get(userId);
        currentUser.setName(user.getName());
        currentUser.setEmail(user.getEmail());
        currentUser.setBirthday(user.getBirthday());
        currentUser.setLogin(user.getLogin());
        log.info("Ответ на запрос к эндпоинту: PUT /users ', Строка параметров запроса: '{}'", users.get(userId));
        return users.get(userId);
    }

    private boolean isUserDataValidate(User user) {
        String email = user.getEmail();
        if ((email == null) || email.isBlank() || (!validator.isHasEmailSymbol(email))
            || validator.isHasSpaceSymbol(email)) {
            return false;
        }
        String login = user.getLogin();
        if ((login == null) || login.isBlank() || validator.isHasSpaceSymbol(login)) {
            return false;
        }
        LocalDate date = user.getBirthday();
        if ((date != null) && (!validator.isDateUserOk(date))) {
                return false;
        }
        return true;
    }
}