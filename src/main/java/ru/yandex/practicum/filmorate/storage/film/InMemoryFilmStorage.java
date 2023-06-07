package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.List;

@Component
public class InMemoryFilmStorage implements FilmStorage{

    private int id = 0;
    private final HashMap<Integer, Film> films = new HashMap<>();

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
