package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Component("FilmDbStorage")
public class FilmDbStorage implements FilmStorage{
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
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
        int filmId = keyHolder.getKey().intValue();
        film.setId(filmId);
        List<Genre> genres = new ArrayList<>(film.getGenres());
        if (genres.isEmpty()) {
            return film;
        }
        String sqlQueryGenres = "insert into films_genres(film_id, genre_id) " +
                "values (?, ?)";
        for (Genre currentG: genres ) {
            int genreId = currentG.getId();
            jdbcTemplate.update(sqlQueryGenres, filmId, genreId);
        }
        return film;
    }

    @Override
    public Optional<Film> updateFilm(Film film) {
        int filmId = film.getId();
        String sqlQuery = "update films set " +
                "name = ?, description = ?, duration = ?, release_date = ?, rating_id = ? " +
                "where id = ?";
        int i = jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getDuration(),
                film.getReleaseDate(),
                film.getMpa().getId(),
                filmId);
        if (i <= 0) {
            return Optional.empty();
        }
        List<Genre> genresNew = new ArrayList<>(film.getGenres());
        List<Genre> genresOld = getIdGenreFilm(filmId);
        if (genresOld.isEmpty() && genresNew.isEmpty()) {
            return Optional.of(film);
        } else if (genresNew.isEmpty()) {
            String sqlDel = "delete from films_genres WHERE film_id = ? ";
            int iDel = jdbcTemplate.update(sqlDel, filmId);
        } else {
            String sqlDel = "delete from films_genres WHERE film_id = ? ";
            int iDel = jdbcTemplate.update(sqlDel, filmId);
            String sqlAddGenres = "insert into films_genres(film_id, genre_id) " +
                    "values (?, ?)";
            for (Genre currentG: genresNew ) {
                int genreId = currentG.getId();
                jdbcTemplate.update(sqlAddGenres, filmId, genreId);
            }
        }
        return Optional.of(film);
    }

    @Override
    public int deleteFilmById(int id) {
        String sql = "delete from films where id = ?";
        return jdbcTemplate.update(sql, id);
    }

    @Override
    public Optional<Film> getFilmById(int id) {
        try {
            String sqlQuery = "SELECT f.id AS id, f.name AS name, f.description AS description, " +
                "f.duration AS duration, f.release_date AS release_date, f.rating_id AS rating_id, " +
                "r.name AS mpa_name " +
                "FROM films AS f INNER JOIN mpa AS r ON f.RATING_ID = r.ID WHERE f.id = ?";
            Optional<Film> film = Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilms, id));
            if (film.isPresent()) {
                List<Genre> genres = getIdGenreFilm(id);
                film.get().getGenres().addAll(genres);
                film.get().getLikes().addAll(getIdLikeOfFilm(id));
            }
            return film;
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Film> getAllFilms() {
        String sqlQuery = "SELECT f.id AS id, f.name AS name, f.description AS description, " +
                "f.duration AS duration, f.release_date AS release_date, f.rating_id AS rating_id, " +
                "r.name AS mpa_name " +
                "FROM films AS f INNER JOIN mpa AS r ON f.RATING_ID = r.ID ";
        List<Film> films = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> mapRowToFilms(rs, rowNum));
        for (Film f: films) {
            List<Genre> genres = getIdGenreFilm(f.getId());
            f.getGenres().addAll(genres);
            f.getLikes().addAll(getIdLikeOfFilm(f.getId()));
        }
        return films;
    }

    @Override
    public List<Film> getTopFilms(int count) {
        String sql = "SELECT f.id AS id, f.name AS name, f.description AS description, " +
                "f.duration AS duration, f.release_date AS release_date, f.rating_id AS rating_id, " +
                "r.name AS mpa_name " +
                "FROM films AS f INNER JOIN mpa AS r ON f.RATING_ID = r.ID "+
                "LEFT OUTER JOIN FILMS_LIKE_USERS l ON f.ID = l.FILM_ID " +
                "GROUP BY f.ID " +
                "ORDER BY count(l.USER_ID) DESC, f.ID asc " +
                "limit ?";
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToFilms(rs, rowNum), count);
        for (Film f: films) {
            List<Genre> genres = getIdGenreFilm(f.getId());
            f.getGenres().addAll(genres);
            f.getLikes().addAll(getIdLikeOfFilm(f.getId()));
        }
        return films;
    }

    @Override
    public int deleteAllFilms() {
        String sql = "delete from films";
        return jdbcTemplate.update(sql);
    }

    @Override
    public int addLikeFilm(int filmId, int userId) {
        String sqlQuery = "insert into films_like_users(film_id, user_id) values (?, ?)";
        return jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public int deleteLikeFilm(int filmId, int userId) {
        String sqlQuery = "delete from films_like_users where film_id = ? and user_id = ? ";
        return jdbcTemplate.update(sqlQuery, filmId, userId);
    };

    private List<Genre> getIdGenreFilm(int id) {
        String sqlQuery = "SELECT genre_id FROM films_genres WHERE film_id = ? ";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> mapRowToIdGenre(rs, rowNum), id);
    }

    private List<Integer> getIdLikeOfFilm(int id) {
        String sqlQuery = "SELECT user_id FROM films_like_users WHERE film_id = ? ";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> mapRowToInteger(rs, rowNum), id);
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

    private Genre mapRowToIdGenre(ResultSet resultSet, int rowNum) throws SQLException {
        Genre genre = new Genre();
        genre.setId(resultSet.getInt("genre_id"));
        return genre;
    }

    private int mapRowToInteger(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getInt("user_id");
    }
}
