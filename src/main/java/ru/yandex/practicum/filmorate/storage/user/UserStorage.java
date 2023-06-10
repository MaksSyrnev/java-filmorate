package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

public interface UserStorage {

    User addUser(User user);

    User updateUser(User user);

    int deleteUserById(int id);

}
