package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component("FilmDbStorage")
public class FilmDbStorage implements FilmStorage{
    private final JdbcTemplate jdbcTemplate;
    private final FilmLikeComparator comporator;

    public FilmDbStorage(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
        comporator = new FilmLikeComparator();
    }

    @Override
    public Film addFilm(Film film) {
        String sqlQuery = "insert into films(name, description, duration, release_date, rating_id) " +
                "values (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setLong(3, film.getDuration());
            stmt.setString(4, film.getReleaseDate().toString());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        int i = keyHolder.getKey().intValue();
        film.setId(i);
        return film;
    }

    @Override
    public Optional<Film> updateFilm(Film film) {
        String sqlQuery = "update films set " +
                "name = ?, description = ?, duration = ?, release_date = ?, rating_id = ? " +
                "where id = ?";
        int i = jdbcTemplate.update(sqlQuery
                , film.getName()
                , film.getDescription()
                , film.getDuration()
                , film.getReleaseDate()
                , film.getMpa().getId()
                , film.getId());
        if (i > 0) {
            return Optional.of(film);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public int deleteFilmById(int id) {
        String sql = "delete from films where id = ?";
        return jdbcTemplate.update(sql, id);
    }

    @Override
    public Optional<Film> getFilmById(int id) {
        String sqlQuery = "SELECT f.id AS id, f.name AS name, f.description AS description, " +
                "f.duration AS duration, f.release_date AS release_date, f.rating_id AS rating_id, " +
                "r.name AS mpa_name " +
                "FROM films AS f INNER JOIN rating_mpa AS r ON f.RATING_ID = r.ID WHERE f.id = ?";
        return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilms, id));
    }

    @Override
    public List<Film> getAllFilms() {
        String sqlQuery = "SELECT f.id AS id, f.name AS name, f.description AS description, " +
                "f.duration AS duration, f.release_date AS release_date, f.rating_id AS rating_id, " +
                "r.name AS mpa_name " +
                "FROM films AS f INNER JOIN rating_mpa AS r ON f.RATING_ID = r.ID ";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> mapRowToFilms(rs, rowNum));
    }

    @Override
    public List<Film> getTopFilms(int count) {
        return null;
    }

    @Override
    public int deleteAllFilms() {
        String sql = "delete from films";
        return jdbcTemplate.update(sql);
    }

    private Film mapRowToFilms(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getInt("id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setDuration(resultSet.getInt("duration"));
        film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
        film.setMpa(new Mpa(resultSet.getInt("rating_id"), resultSet.getString("mpa_name")));
        return film;
    }
}
