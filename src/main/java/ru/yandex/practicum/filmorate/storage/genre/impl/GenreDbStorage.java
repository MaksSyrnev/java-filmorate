package ru.yandex.practicum.filmorate.storage.genre.impl;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final String SELECT_GENRE_ID = "SELECT id, name FROM genres WHERE id = ?";
    private static final String SELECT_ALL_GENRES = "SELECT id, name FROM genres ";

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Genre> getGenreById(int id) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(SELECT_GENRE_ID, this::mapRowToGenre, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Genre> getAllGenres() {
        List<Genre> genres = jdbcTemplate.query(SELECT_ALL_GENRES, (rs, rowNum) -> mapRowToGenre(rs, rowNum));
        genres.sort(Comparator.comparing(Genre::getId));
        return genres;
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        Genre genre = new Genre();
        genre.setId(resultSet.getInt("id"));
        genre.setName(resultSet.getString("name"));
        return genre;
    }
}
