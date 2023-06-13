package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
public class FilmService {
    private final FilmStorage storage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage storage, UserStorage userStorage) {
        this.storage = storage;
        this.userStorage = userStorage;
    }

    public Film addFilm(Film film) {
        return storage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return storage.updateFilm(film);
    }

    public int deleteFilmById(int id) {
        return storage.deleteFilmById(id);
    }

    public Optional<Film> getFilmById(int id) {
        return storage.getFilmById(id);
    }

    public List<Film> getAllFilms() {
        return storage.getAllFilms();
    }

    public List<Integer> addLike(int idFilm, int idUser) {
        final Optional<Film> film = storage.getFilmById(idFilm);
        final Optional<User> user = userStorage.getUserById(idUser);
        if (film.isEmpty() || user.isEmpty()) {
            throw new IncorrectIdException("неверный id  фильма или пользователя");
        }
        film.get().getLikes().add(idUser);
        storage.updateFilm(film.get());
        return new ArrayList<>(film.get().getLikes());
    }

    public List<Integer> deleteLike(int idFilm, int idUser) {
        final Optional<Film> film = storage.getFilmById(idFilm);
        final Optional<User> user = userStorage.getUserById(idUser);
        if (film.isEmpty() || user.isEmpty()) {
            throw new IncorrectIdException("неверный id  фильма");
        }
        film.get().getLikes().remove(idUser);
        storage.updateFilm(film.get());
        return new ArrayList<>(film.get().getLikes());
    }

    public List<Film> getTopFilms(int count) {
        return storage.getTopFilms(count);
    }
}
