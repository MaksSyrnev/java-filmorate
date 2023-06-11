package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;

@Service
public class FilmService {
    /*
    добавление и удаление лайка, вывод 10 наиболее популярных фильмов по количеству лайков.
    Пусть пока каждый пользователь может поставить лайк фильму только один раз.
     */
    private final FilmStorage storage;

    @Autowired
    public FilmService(FilmStorage storage) {
        this.storage = storage;
    }

    public List<Long> addLike(int idFilm) {

    }

    public List<Long> deleteLike(int idFilm) {

    }

    public List<Film> popularFilms() {

    }
}
