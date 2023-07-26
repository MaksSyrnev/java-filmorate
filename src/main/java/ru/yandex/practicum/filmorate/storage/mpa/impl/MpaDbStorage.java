package ru.yandex.practicum.filmorate.storage.mpa.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;
    private final static String SELECT_MPA_ID = "SELECT id, name FROM mpa WHERE id = ?";
    private final static String SELECT_ALL_MPA = "SELECT id, name FROM mpa ";

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Mpa> getMpaById(int id) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(SELECT_MPA_ID, this::mapRowToMpa, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Mpa> getAllMpa() {
        List<Mpa> allMpa = jdbcTemplate.query(SELECT_ALL_MPA, (rs, rowNum) -> mapRowToMpa(rs, rowNum));
        allMpa.sort(Comparator.comparing(Mpa::getId));
        return allMpa;
    }

    private Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        Mpa mpa = new Mpa();
        mpa.setId(resultSet.getInt("id"));
        mpa.setName(resultSet.getString("name"));
        return mpa;
    }
}
