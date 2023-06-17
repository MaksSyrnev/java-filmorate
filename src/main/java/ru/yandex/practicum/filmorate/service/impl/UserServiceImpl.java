package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.IncorrectIdException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.service.validation.Validation;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserStorage storage;
    private final Validation validator;

    @Autowired
    public UserServiceImpl(UserStorage storage, Validation validator) {
        this.storage = storage;
        this.validator = validator;
    }

    @Override
    public User addUser(User user) {
        validateUser(user, "POST");
        return storage.addUser(user);
    }

    @Override
    public User updateUser(User user) {
        validateUser(user, "PUT");
        Optional<User> updUser = storage.updateUser(user);
        if (updUser.isEmpty()) {
            log.error("Ошибка в данных, Пользователь не найден: ', user - '{}' ", user.getId());
            throw new IncorrectIdException("нет пользователя с таким id");
        }
        return updUser.get();
    }

    @Override
    public int deleteUserById(int id) {
        return storage.deleteUserById(id);
    }

    @Override
    public User getUserById(int id) {
        Optional<User> user = storage.getUserById(id);
        if (user.isEmpty()) {
            log.error("Ошибка в данных, Пользователь не найден: ', user - '{}' ", id);
            throw new IncorrectIdException("нет пользователя с таким id"); // ??
        }
        return user.get();
    }

    @Override
    public List<User> getAllUser() {
        return storage.getAllUser();
    }

    @Override
    public User addFriendById(int userId, int friendId) {
        Optional<User> user = storage.getUserById(userId);
        Optional<User> friend = storage.getUserById(friendId);
        if (user.isEmpty() || friend.isEmpty()) {
            log.error("Ошибка в данных, Пользователь не найден: ', user - '{}',  friend - '{}'",
                    userId, friendId);
            throw new IncorrectIdException("Пользователь не найден");
        }
        Set<Integer> userFriends = user.get().getFriends();
        Set<Integer> friendFriends = friend.get().getFriends();
        userFriends.add(friendId);
        friendFriends.add(userId);
        return user.get();
    }

    @Override
    public User deleteFriendById(int userId, int friendId) {
        Optional<User> user = storage.getUserById(userId);
        Optional<User> friend = storage.getUserById(friendId);
        if (user.isEmpty() || friend.isEmpty()) {
            log.error("Ошибка в данных, Пользователь не найден: ', user - '{}',  friend - '{}'",
                    userId, friendId);
            throw new IncorrectIdException("Пользователь не найден");
        }
        Set<Integer> userFriends = user.get().getFriends();
        Set<Integer> friendFriends = friend.get().getFriends();
        userFriends.remove(friendId);
        friendFriends.remove(userId);
        return user.get();
    }

    @Override
    public List<User> getAllFriends(int id) {
        Optional<User> user = storage.getUserById(id);
        if (user.isEmpty()) {
            log.error("Ошибка в данных, Пользователь не найден: ', user - '{}' ", id);
            throw new IncorrectIdException("Пользователь не найден");
        }
        Set<Integer> freinds = user.get().getFriends();
        ArrayList<User> userFriends = new ArrayList<>();
        for (Integer i : freinds) {
            Optional<User> currentUser = storage.getUserById(i);
            if (currentUser.isPresent()) {
                userFriends.add(currentUser.get());
            }
        }
        return userFriends;
    }

    @Override
    public List<User> getСommonFriends(int firstUserId, int secondUserId) {
        ArrayList<Integer> friends = new ArrayList<>();
        ArrayList<User> commonFriends = new ArrayList<>();
        Optional<User> firstUser = storage.getUserById(firstUserId);
        Optional<User> secondUser = storage.getUserById(secondUserId);
        if (firstUser.isEmpty() || secondUser.isEmpty()) {
            log.error("Ошибка в данных, Пользователь не найден: ', user - '{}',  friend - '{}'",
                    firstUserId, secondUserId);
            throw new IncorrectIdException("Пользователь не найден");
        }
        Set<Integer> firstFreinds = firstUser.get().getFriends();
        Set<Integer> secondFreinds = secondUser.get().getFriends();
        if (firstFreinds.isEmpty()) {
            return commonFriends;
        } else if (secondFreinds.isEmpty()) {
            return commonFriends;
        } else {
            for (Integer i : firstFreinds) {
                if (secondFreinds.contains(i)) {
                    friends.add(i);
                }
            }
            for (Integer j: friends) {
                Optional<User> user = storage.getUserById(j);
                if (user.isPresent()) {
                    commonFriends.add(user.get());
                }
            }
            return commonFriends;
        }
    }

    private void validateUser(User user, String method) {
        if ("PUT".equals(method)) {
            Optional<User> userInMemory = storage.getUserById(user.getId());
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
}
