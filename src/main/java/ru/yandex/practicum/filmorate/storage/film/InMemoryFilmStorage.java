package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

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
    public Film updateFilm(Film film) {
        int filmId = film.getId();
        if (films.containsKey(filmId)) {
            Film currentFilm = films.get(filmId);
            topFilms.clear();
            currentFilm.setName(film.getName());
            currentFilm.setDescription(film.getDescription());
            currentFilm.setReleaseDate(film.getReleaseDate());
            currentFilm.setDuration(film.getDuration());
            topFilms.addAll(films.values());
            return films.get(filmId);
        }
        return null;
    }

    @Override
    public int deleteFilmById(int id) {
        if (films.containsKey(id)) {
            topFilms.remove(films.get(id));
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

    @Override
    public List<Film> getTopFilms(int count) {
        List<Film> films = new ArrayList<Film>();
        for (Film f: topFilms) {
            films.add(f);
        }
        if (films.size() > count) {
            return films.subList(0, count);
        } else {
            return films;
        }
    }

    Comparator<Film> comparatorOnLikes = new Comparator<Film>() {
        @Override
        public int compare(Film film1, Film film2) {
            int size1 = film1.getLikes().size();
            int size2 = film2.getLikes().size();

            if (film1.getId() == film2.getId()) {
                return 0;
            }
            if ((size1 == 0) && (size2 != 0)) {
                return 1;
            } else if ((size1 != 0) && (size2 == 0)) {
                return -1;
            } else if ((size1 == 0) && (size2 == 0)) {
                return film1.getId() - film2.getId();
            } else {
                return size2 - size1;
            }
        }
    };

}
