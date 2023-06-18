package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.TreeSet;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private int id;
    private final HashMap<Integer, Film> films;
    private final TreeSet<Film> topFilms;

    public InMemoryFilmStorage() {
        id = 0;
        films = new HashMap<>();
        topFilms = new TreeSet<>(comparatorOnLikes);
    }

    @Override
    public Film addFilm(Film film) {
        id++;
        film.setId(id);
        films.put(id,film);
        topFilms.add(film);
        return film;
    }

    @Override
    public Optional<Film> updateFilm(Film film) {
        int filmId = film.getId();
        if (films.containsKey(filmId)) {
            Film currentFilm = films.get(filmId);
            topFilms.clear();
            currentFilm.setName(film.getName());
            currentFilm.setDescription(film.getDescription());
            currentFilm.setReleaseDate(film.getReleaseDate());
            currentFilm.setDuration(film.getDuration());
            topFilms.addAll(films.values());
            return Optional.of(films.get(filmId));
        }
        return Optional.empty();
    }

    @Override
    public int deleteFilmById(int id) {
        if (films.containsKey(id)) {
            topFilms.clear();
            films.remove(id);
            topFilms.addAll(films.values());
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

    @Override
    public int deleteAllFilms() {
        topFilms.clear();
        films.clear();
        if ((films.size() > 0) || (topFilms.size() > 0)) {
            return 0;
        }
        return 1;
    }

    @Override
    public List<Film> getTopFilms(int count) {
        List<Film> films = new ArrayList<Film>();
        for (Film film: topFilms) {
            films.add(film);
        }
        if (films.size() > count) {
            return films.subList(0, count);
        } else {
            return films;
        }
    }

    Comparator<Film> comparatorOnLikes = new Comparator<Film>() {
        @Override
        public int compare(Film firstFilm, Film secondFilm) {
            int sizeLikesFirstFilm = firstFilm.getLikes().size();
            int sizeLikesSecondFilms = secondFilm.getLikes().size();

            if (firstFilm.getId() == secondFilm.getId()) {
                return 0;
            }
            if ((sizeLikesFirstFilm == 0) && (sizeLikesSecondFilms != 0)) {
                return 1;
            } else if ((sizeLikesFirstFilm != 0) && (sizeLikesSecondFilms == 0)) {
                return -1;
            } else if ((sizeLikesFirstFilm == 0) && (sizeLikesSecondFilms == 0)) {
                return firstFilm.getId() - secondFilm.getId();
            } else {
                return sizeLikesSecondFilms - sizeLikesFirstFilm;
            }
        }
    };

}
