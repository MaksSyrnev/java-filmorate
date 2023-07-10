package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreStorage {
    Genre addGenre(Genre genre);

    Optional<Genre> updateGenre(Genre genre);

    Optional<Genre> getGenreById(int id);

    List<Genre> getAllGenres();

    int deleteGenreById(int id);

    int deleteAllGenres();

}
