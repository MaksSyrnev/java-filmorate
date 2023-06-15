package ru.yandex.practicum.filmorate.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@Service
public class FilmServiceImpl implements FilmService {
    private final FilmStorage storage;
    private final UserStorage userStorage;

    @Autowired
    public FilmServiceImpl(FilmStorage storage, UserStorage userStorage) {
        this.storage = storage;
        this.userStorage = userStorage;
    }

    @Override
    public Film addFilm(Film film) {
        return storage.addFilm(film);
    }

    @Override
    public Film updateFilm(Film film) {
        return storage.updateFilm(film);
    }

    @Override
    public int deleteFilmById(int id) {
        return storage.deleteFilmById(id);
    }

    @Override
    public Optional<Film> getFilmById(int id) {
        return storage.getFilmById(id);
    }

    @Override
    public List<Film> getAllFilms() {
        return storage.getAllFilms();
    }

    @Override
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

    @Override
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

    @Override
    public List<Film> getTopFilms(int count) {
        return storage.getTopFilms(count);
    }

}
