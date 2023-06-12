package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
public class FilmService {
    private final FilmStorage storage;
    private final UserStorage userStorage;
    private final TreeSet<Film> topFilms;

    @Autowired
    public FilmService(FilmStorage storage, UserStorage userStorage) {
        this.storage = storage;
        this.userStorage = userStorage;
        topFilms = new TreeSet<>(comparatorOnLikes);
    }

    public List<Integer> addLike(int idFilm, int idUser) {
        final Optional<Film> film = storage.getFilmById(idFilm);
        final Optional<User> user = userStorage.getUserById(idUser);
        if (film.isEmpty() || user.isEmpty()) {
            throw new RuntimeException("неверный id  фильма");
        }
        film.get().getLikes().add(idUser);
        return (List<Integer>) film.get().getLikes();
    }

    public List<Integer> deleteLike(int idFilm, int idUser) {
        final Optional<Film> film = storage.getFilmById(idFilm);
        final Optional<User> user = userStorage.getUserById(idUser);
        if (film.isEmpty() || user.isEmpty()) {
            throw new RuntimeException("неверный id  фильма");
        }
        film.get().getLikes().remove(idUser);
        return (List<Integer>) film.get().getLikes();
    }

    public List<Film> getTopFilms() {
        List<Film> films = new ArrayList<Film>();
        for(Film f: topFilms) {
            films.add(f);
        }
        return films;
    }

    Comparator<Film> comparatorOnLikes = new Comparator<Film>() {
        @Override
        public int compare(Film film1, Film film2) {
            int size1 = film1.getLikes().size();
            int size2 = film2.getLikes().size();

            if ((size1 == 0) && (size2 != 0)) {
                return 1;
            } else if ((size1 != 0) && (size2 == 0)) {
                return -1;
            } else if((size1 == 0) && (size2 == 0)) {
                return film1.getId() - film2.getId();
            } else {
                return size1 - size2;
            }
        }
    };
}
