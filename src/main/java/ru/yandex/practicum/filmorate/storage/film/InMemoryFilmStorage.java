package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

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
        id++;
        film.setId(id);
        films.put(id,film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        int filmId = film.getId();
        if (films.containsKey(filmId)) {
            Film currentFilm = films.get(filmId);
            currentFilm.setName(film.getName());
            currentFilm.setDescription(film.getDescription());
            currentFilm.setReleaseDate(film.getReleaseDate());
            currentFilm.setDuration(film.getDuration());
            return films.get(filmId);
        }
        return null;
    }

    @Override
    public int deleteFilmById(int id) {
        if(films.containsKey(id)) {
            films.remove(id);
            return id;
        }
        return 0;
    }

    @Override
    public Optional<Film> getFilmById(int id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }
}
