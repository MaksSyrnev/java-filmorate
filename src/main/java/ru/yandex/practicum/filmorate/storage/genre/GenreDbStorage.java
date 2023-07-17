package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
public class GenreDbStorage implements GenreStorage{
    private final JdbcTemplate jdbcTemplate;
    private final GenreComporator comporator;

    public GenreDbStorage(JdbcTemplate jdbcTemplate, GenreComporator comporator) {
        this.jdbcTemplate = jdbcTemplate;
        this.comporator = comporator;
    }

    @Override
    public Genre addGenre(Genre genre) {
        return null;
    }

    @Override
    public Optional<Genre> updateGenre(Genre genre) {
        return Optional.empty();
    }

    @Override
    public Optional<Genre> getGenreById(int id) {
        try {
            String sqlQuery = "SELECT id, name FROM genres WHERE id = ?";
            return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, this::mapRowToGenre, id));
        } catch (EmptyResultDataAccessException e){
            return Optional.empty();
        }
    }

    @Override
    public List<Genre> getAllGenres() {
        String sqlQuery = "SELECT id, name FROM genres ";
        List<Genre> genres = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> mapRowToGenre(rs, rowNum));
        genres.sort(comporator);
        return genres;
    }

    @Override
    public int deleteGenreById(int id) {
        return 0;
    }

    @Override
    public int deleteAllGenres() {
        return 0;
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        Genre genre = new Genre();
        genre.setId(resultSet.getInt("id"));
        genre.setName(resultSet.getString("name"));
        return genre;
    }
}
