package ru.yandex.practicum.filmorate.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
    private final UserStorage storage;

    @Autowired
    public UserService(UserStorage storage) {
        this.storage = storage;
    }

    public void addFriendById(int userId, int friendId) {
        Optional<User> user = storage.getUserById(userId);
        Optional<User> friend = storage.getUserById(userId);
        if (user.isEmpty() || friend.isEmpty()) {
            throw new RuntimeException("Ошибка в данных");
        }
        Set<Integer> userFriends = user.get().getFriends();
        Set<Integer> friendFriends = friend.get().getFriends();
        userFriends.add(friendId);
        friendFriends.add(userId);
    }

    public void deleteFriendById(int userId, int friendId) {
        Optional<User> user = storage.getUserById(userId);
        Optional<User> friend = storage.getUserById(userId);
        if (user.isEmpty() || friend.isEmpty()) {
            throw new RuntimeException("Ошибка в данных");
        }
        Set<Integer> userFriends = user.get().getFriends();
        Set<Integer> friendFriends = friend.get().getFriends();
        userFriends.remove(friendId);
        friendFriends.remove(userId);
    }

    public List<Integer> getСommonFriends(int firstUserId, int secondUserId) {
        ArrayList<Integer> commonFriends = new ArrayList<>();
        Optional<User> firstUser = storage.getUserById(firstUserId);
        Optional<User> secondUser = storage.getUserById(secondUserId);
        if (firstUser.isEmpty() || secondUser.isEmpty()) {
            throw new RuntimeException("Ошибка в данных");
        }
        Set<Integer> firstFreinds = firstUser.get().getFriends();
        Set<Integer> secondFreinds = firstUser.get().getFriends();
        if (firstFreinds.isEmpty()) {
            return commonFriends;
        } else if (secondFreinds.isEmpty()) {
            return commonFriends;
        } else {
            for (Integer i : firstFreinds) {
                if (secondFreinds.contains(i)) {
                    commonFriends.add(i);
                }
            }
            return commonFriends;
        }
    }
}
