package ru.yandex.practicum.filmorate.storage.film.impl;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;
    private static final String INSERT_NEW_FIlM = "insert into films(name, description, duration, " +
            "release_date, rating_id) " +
            "values (?, ?, ?, ?, ?)";
    private static final String INSERT_FIMLM_GENRE = "insert into films_genres(film_id, genre_id) " +
            "values (?, ?)";
    private static final String UPDATE_FILM = "update films set " +
            "name = ?, description = ?, duration = ?, release_date = ?, rating_id = ? " +
            "where id = ?";
    private static final String DELETE_FILM_GENRE = "delete from films_genres WHERE film_id = ? ";
    private static final String DELETE_FILM = "delete from films where id = ?";
    private static final String SELECT_FILM_ID = "SELECT f.id AS id, f.name AS name, f.description AS description, " +
            "f.duration AS duration, f.release_date AS release_date, f.rating_id AS rating_id, " +
            "r.name AS mpa_name " +
            "FROM films AS f INNER JOIN mpa AS r ON f.RATING_ID = r.ID WHERE f.id = ?";
    private static final String SELECT_ALL_FILM = "SELECT f.id AS id, f.name AS name, f.description AS description, " +
            "f.duration AS duration, f.release_date AS release_date, f.rating_id AS rating_id, " +
            "r.name AS mpa_name " +
            "FROM films AS f INNER JOIN mpa AS r ON f.RATING_ID = r.ID ";
    private static final String SELECT_TOP_FILM = "select f.id AS id, f.name AS name, f.description AS description, " +
            "f.duration AS duration, f.release_date AS release_date, f.rating_id AS rating_id, " +
            "r.name AS mpa_name " +
            "FROM films AS f INNER JOIN mpa AS r ON f.RATING_ID = r.ID " +
            "LEFT OUTER JOIN FILMS_LIKE_USERS l ON f.ID = l.FILM_ID " +
            "GROUP BY f.ID " +
            "ORDER BY count(l.USER_ID) DESC, f.ID ASC " +
            "limit ?";
    private static final String DELETE_ALL_FILM = "delete from films";
    private static final String INSERT_LIKE = "insert into films_like_users(film_id, user_id) values (?, ?)";
    private static final String DELETE_LIKE = "delete from films_like_users where film_id = ? and user_id = ? ";
    private static final String SELECT_GENRES_FILM = "select genre_id FROM films_genres WHERE film_id = ? ";
    private static final String SELECT_LIKES_FILM = "select user_id FROM films_like_users WHERE film_id = ? ";

    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreStorage genreStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreStorage = genreStorage;
    }

    @Override
    public Film addFilm(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(INSERT_NEW_FIlM, new String[]{"id"});
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
        for (Genre currentG: genres) {
            int genreId = currentG.getId();
            Optional<Genre> genre = genreStorage.getGenreById(genreId);
            if (genre.isPresent()) {
                jdbcTemplate.update(INSERT_FIMLM_GENRE, filmId, genreId);
                currentG.setName(genre.get().getName());
            }
        }
        return film;
    }

    @Override
    public Optional<Film> updateFilm(Film film) {
        int filmId = film.getId();
        int i = jdbcTemplate.update(UPDATE_FILM,
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
        List<Genre> genresOld = getAllGenreByFilmId(filmId);
        if (genresOld.isEmpty() && genresNew.isEmpty()) {
            return Optional.of(film);
        } else if (genresNew.isEmpty()) {
            int iDel = jdbcTemplate.update(DELETE_FILM_GENRE, filmId);
        } else {
            int iDel = jdbcTemplate.update(DELETE_FILM_GENRE, filmId);
            for (Genre currentG: genresNew) {
                int genreId = currentG.getId();
                Optional<Genre> genre = genreStorage.getGenreById(genreId);
                if (genre.isPresent()) {
                    jdbcTemplate.update(INSERT_FIMLM_GENRE, filmId, genreId);
                    currentG.setName(genre.get().getName());
                }
            }
            genresNew.sort(Comparator.comparing(Genre::getId));
            film.getGenres().clear();
            film.getGenres().addAll(genresNew);
        }
        return Optional.of(film);
    }

    @Override
    public int deleteFilmById(int id) {
        return jdbcTemplate.update(DELETE_FILM, id);
    }

    @Override
    public Optional<Film> getFilmById(int id) {
        try {
            Optional<Film> film = Optional.ofNullable(jdbcTemplate.queryForObject(SELECT_FILM_ID, this::mapRowToFilms, id));
            if (film.isPresent()) {
                List<Genre> genres = getAllGenreByFilmId(id);
                for (Genre currentG: genres) {
                    int genreId = currentG.getId();
                    Optional<Genre> genre = genreStorage.getGenreById(genreId);
                    if (genre.isPresent()) {
                        currentG.setName(genre.get().getName());
                    }
                }
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
        List<Film> films = jdbcTemplate.query(SELECT_ALL_FILM, (rs, rowNum) -> mapRowToFilms(rs, rowNum));
        for (Film f: films) {
            List<Genre> genres = getAllGenreByFilmId(f.getId());
            for (Genre currentG: genres) {
                int genreId = currentG.getId();
                Optional<Genre> genre = genreStorage.getGenreById(genreId);
                if (genre.isPresent()) {
                    currentG.setName(genre.get().getName());
                }
            }
            f.getGenres().addAll(genres);
            f.getLikes().addAll(getIdLikeOfFilm(f.getId()));
        }
        return films;
    }

    @Override
    public List<Film> getTopFilms(int count) {
        List<Film> films = jdbcTemplate.query(SELECT_TOP_FILM, (rs, rowNum) -> mapRowToFilms(rs, rowNum), count);
        for (Film f: films) {
            List<Genre> genres = getAllGenreByFilmId(f.getId());
            f.getGenres().addAll(genres);
            f.getLikes().addAll(getIdLikeOfFilm(f.getId()));
        }
        return films;
    }

    @Override
    public int deleteAllFilms() {
        return jdbcTemplate.update(DELETE_ALL_FILM);
    }

    @Override
    public int addLikeFilm(int filmId, int userId) {
        return jdbcTemplate.update(INSERT_LIKE, filmId, userId);
    }

    @Override
    public int deleteLikeFilm(int filmId, int userId) {
        return jdbcTemplate.update(DELETE_LIKE, filmId, userId);
    }

    private List<Genre> getAllGenreByFilmId(int id) {
        return jdbcTemplate.query(SELECT_GENRES_FILM, (rs, rowNum) -> mapRowToIdGenre(rs, rowNum), id);
    }

    private List<Integer> getIdLikeOfFilm(int id) {
        return jdbcTemplate.query(SELECT_LIKES_FILM, (rs, rowNum) -> mapRowToInteger(rs, rowNum), id);
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
