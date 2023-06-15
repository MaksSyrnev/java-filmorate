package ru.yandex.practicum.filmorate.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {
    private final UserStorage storage;

    @Autowired
    public UserServiceImpl(UserStorage storage) {
        this.storage = storage;
    }

    @Override
    public User addUser(User user) {
        return storage.addUser(user);
    }

    @Override
    public User updateUser(User user) {
        return storage.updateUser(user);
    }

    @Override
    public int deleteUserById(int id) {
        return storage.deleteUserById(id);
    }

    @Override
    public Optional<User> getUserById(int id) {
        return storage.getUserById(id);
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
}
