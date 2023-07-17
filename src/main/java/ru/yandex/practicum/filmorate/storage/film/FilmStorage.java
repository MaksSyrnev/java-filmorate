package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Film addFilm(Film film);

    Optional<Film> updateFilm(Film film);

    int deleteFilmById(int id);

    Optional<Film> getFilmById(int id);

    List<Film> getAllFilms();

    List<Film> getTopFilms(int count);

    int deleteAllFilms();

    int addLikeFilm(int film_id, int user_id);

    int deleteLikeFilm(int film_id, int user_id);

}
