package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.List;

@Component
public class InMemoryFilmStorage implements FilmStorage{
    private int id;
    private final HashMap<Integer, Film> films;

    public InMemoryFilmStorage() {
        this.id = 0;
        this.films = new HashMap<>();
    }

    @Override
    public Film addFilm(Film film) {
        return null;
    }

    @Override
    public Film updateFilm(Film film) {
        return null;
    }

    @Override
    public int deleteFilmById(int id) {
        return null;
    }

    public Film getFilmById(int id) {
        return null;
    }

    public List<Film> getAllFilms() {
        return null;
    }
}
