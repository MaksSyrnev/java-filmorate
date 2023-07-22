package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaComporator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaComporator comporator;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate, MpaComporator comporator) {
        this.jdbcTemplate = jdbcTemplate;
        this.comporator = comporator;
    }

    @Override
    public Mpa addMpa(Mpa mpa) {
        return null;
    }

    @Override
    public Optional<Mpa> updateMpa(Mpa mpa) {
        return Optional.empty();
    }

    @Override
    public Optional<Mpa> getMpaById(int id) {
        try {
            String sqlQuery = "SELECT id, name FROM mpa WHERE id = ?";
            return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, this::mapRowToMpa, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Mpa> getAllMpa() {
        String sqlQuery = "SELECT id, name FROM mpa ";
        List<Mpa> allMpa = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> mapRowToMpa(rs, rowNum));
        allMpa.sort(comporator);
        return allMpa;
    }

    @Override
    public int deleteMpaById(int id) {
        return 0;
    }

    @Override
    public int deleteAllMpa() {
        return 0;
    }

    private Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        Mpa mpa = new Mpa();
        mpa.setId(resultSet.getInt("id"));
        mpa.setName(resultSet.getString("name"));
        return mpa;
    }
}
