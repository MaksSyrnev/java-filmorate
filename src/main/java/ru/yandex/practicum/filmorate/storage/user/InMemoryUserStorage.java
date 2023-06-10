package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
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
        id++;
        user.setId(id);
        String name = user.getName();
        if ((name == null) || name.isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(id, user);
        return users.get(id);
    }

    @Override
    public User updateUser(User user) {
        int userId = user.getId();
        if (users.containsKey(userId)) {
            User currentUser = users.get(userId);
            currentUser.setName(user.getName());
            currentUser.setEmail(user.getEmail());
            currentUser.setBirthday(user.getBirthday());
            currentUser.setLogin(user.getLogin());
            return users.get(userId);
        }
        return null;
    }

    @Override
    public int deleteUserById(int id) {
        if (users.containsKey(id)) {
            users.remove(id);
            return id;
        }
        return 0;
    }

    public User getUserById(int id) {
        if (users.containsKey(id)) {
            return users.get(id);
        }
        return null;
    }

    public List<User> getAllUser() {
        return new ArrayList<>(users.values());
    }
}
