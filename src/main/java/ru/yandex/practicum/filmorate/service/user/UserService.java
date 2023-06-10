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
    /*
    Создайте UserService, который будет отвечать за такие операции с пользователями,
    как добавление в друзья, удаление из друзей, вывод списка общих друзей.
    Пока пользователям не надо одобрять заявки в друзья — добавляем сразу.
    То есть если Лена стала другом Саши, то это значит, что Саша теперь друг Лены.
    Убедитесь, что сервисы зависят от интерфейсов классов-хранилищ, а не их реализаций.
     */
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
        Set<Long> userFriends = user.get().getFriends();
        Set<Long> friendFriends = friend.get().getFriends();
        if (!userFriends.contains(friendId)) {
            userFriends.add((long) friendId);
        }
        if (!friendFriends.contains(userId)) {
            friendFriends.add((long) userId);
        }
    }

    public void deleteFriendById(int userId, int friendId) {
        Optional<User> user = storage.getUserById(userId);
        Optional<User> friend = storage.getUserById(userId);
        if (user.isEmpty() || friend.isEmpty()) {
            throw new RuntimeException("Ошибка в данных");
        }
        Set<Long> userFriends = user.get().getFriends();
        Set<Long> friendFriends = friend.get().getFriends();
        if (!userFriends.contains(friendId)) {
            userFriends.remove((long) friendId);
        }
        if (!friendFriends.contains(userId)) {
            friendFriends.remove((long) userId);
        }
    }

    public List<Long> getСommonFriends(int firstUserId, int secondUserId) {
        Optional<User> firstUser = storage.getUserById(firstUserId);
        Optional<User> secondUser = storage.getUserById(secondUserId);
        if (firstUser.isEmpty() || secondUser.isEmpty()) {
            throw new RuntimeException("Ошибка в данных");
        }
        Set<Long> firstFreinds = firstUser.get().getFriends();
        Set<Long> secondFreinds = firstUser.get().getFriends();
        if (firstFreinds.isEmpty()) {
            return new ArrayList<Long>();
        } else if (secondFreinds.isEmpty()) {
            return new ArrayList<Long>();
        } else {
            return new ArrayList<Long>();
        }
    }
}
