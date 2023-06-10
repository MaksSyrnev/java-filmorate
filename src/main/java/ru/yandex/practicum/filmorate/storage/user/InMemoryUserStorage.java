package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;

@Component
public class InMemoryUserStorage implements UserStorage{
    private int id;
    private final HashMap<Integer,User> users;

    public InMemoryUserStorage() {
        this.id = 0;
        this.users = new HashMap<>();
    }

    @Override
    public User addUser(User user) {
        return null;
    }

    @Override
    public User updateUser(User user) {
        return null;
    }

    @Override
    public int deleteUserById(int id) {
        return 0;
    }

    public User getUserById(int id) {
        return null;
    }

    public List<User> getAllUser() {

    }
}
