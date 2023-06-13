package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import ru.yandex.practicum.filmorate.exeption.IncorrectIdException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.service.validation.Validation;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
public class UserController {
    private final UserService userService;
    private final Validation validator;

    @Autowired
    public UserController(UserService userService, Validation validator) {
        this.userService = userService;
        this.validator = validator;
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        log.info("Получен запрос к эндпоинту: GET /users '");
        return userService.getAllUser();
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable int id) {
        log.info("Получен запрос к эндпоинту: GET /users/{id}");
        Optional<User> user = userService.getUserById(id);
        if (user.isEmpty()) {
            throw new IncorrectIdException("нет пользователя с таким id"); // ??
        }
        return user.get();
    }

    @PostMapping("/users")
    public User addUser(@Valid @RequestBody User user) {
        log.info("Получен запрос к эндпоинту: POST /users ', Строка параметров запроса: '{}'", user);
        validateUser(user, "POST");
        User newUser = userService.addUser(user);
        log.info("Ответ на запрос к эндпоинту: POST /users ', '{}'", user);
        return newUser;
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Получен запрос к эндпоинту: PUT /users ', Строка параметров запроса: '{}'", user);
        validateUser(user, "PUT");
        User updUser = userService.updateUser(user);
        if (updUser != null) {
            log.info("Ответ на запрос к эндпоинту: PUT /users ', Строка параметров запроса: '{}'", updUser);
            return updUser;
        } else {
            throw new IncorrectIdException("нет пользователя с таким id"); // ??
        }
    }

   @PutMapping("/users/{id}/friends/{friendId}")
   public User addUserFriend(@PathVariable int id, @PathVariable int friendId) {
       log.info("Получен запрос к эндпоинту: PUT /users/{}/friends/{} '", id, friendId);
       return userService.addFriendById(id,friendId);
   }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public User deleteUserFriendById(@PathVariable int id, @PathVariable int friendId) {
        log.info("Получен запрос к эндпоинту: DELETE /users/{}/friends/{} '", id, friendId);
        return userService.deleteFriendById(id, friendId);
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getUserFriends(@PathVariable int id) {
        log.info("Получен запрос к эндпоинту: GET /users/{}/friends '", id);
        return userService.getAllFriends(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        return userService.getСommonFriends(id, otherId);
    }

    private void validateUser(User user, String method) {
        if ("PUT".equals(method)) {
            Optional<User> userInMemory = userService.getUserById(user.getId());
            if (userInMemory.isEmpty()) {
                log.error("Ошибка в данных запроса к эндпоинту:{} /users ', : '{}'", method, user);
                throw new IncorrectIdException("пользователь стаким id не найден");
            }
        }
        String email = user.getEmail();
        if ((email == null) || email.isBlank() || (!validator.isHasEmailSymbol(email))
            || validator.isHasSpaceSymbol(email)) {
            log.error("Ошибка в данных запроса к эндпоинту:{} /users ', : '{}'", method, user);
            throw new ValidationException("некоректные данные в почте");
        }
        String login = user.getLogin();
        if ((login == null) || login.isBlank() || validator.isHasSpaceSymbol(login)) {
            log.error("Ошибка в данных запроса к эндпоинту:{} /users ', : '{}'", method, user);
            throw new ValidationException("логин должен одно слово, не может быть пустым");
        }
        LocalDate date = user.getBirthday();
        if ((date != null) && (!validator.isDateUserOk(date))) {
            log.error("Ошибка в данных запроса к эндпоинту:{} /users ', : '{}'", method, user);
            throw new ValidationException("дата рождения не может быть больше текущей даты");
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