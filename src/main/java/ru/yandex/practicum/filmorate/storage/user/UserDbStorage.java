package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component("UserDbStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }

    @Override
    public User addUser(User user) {
        String sqlQuery = "insert into users(login, name, email, birthday) " +
                "values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, user.getLogin());
            stmt.setString(2, user.getName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getBirthday().toString());
            return stmt;
        }, keyHolder);
        int i = keyHolder.getKey().intValue();
        user.setId(i);
        return user;
    }

    @Override
    public Optional<User> updateUser(User user) {
        String sqlQuery = "update users set " +
                "login = ?, name = ?, email = ?, birthday = ? " +
                "where id = ?";
        int i = jdbcTemplate.update(sqlQuery
                , user.getLogin()
                , user.getName()
                , user.getEmail()
                , user.getBirthday()
                , user.getId());
        if (i > 0) {
            return Optional.of(user);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public int deleteUserById(int id) {
        String sql = "delete from users where id = ?";
        return jdbcTemplate.update(sql, id);
    }

    @Override
    public Optional<User> getUserById(int id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users where id = ?", id);
        if(userRows.next()) {
            User user = new User();
            user.setId(userRows.getInt("id"));
            user.setEmail(userRows.getString("email"));
            user.setLogin(userRows.getString("login"));
            user.setName(userRows.getString("name"));
            user.setBirthday(userRows.getDate("birthday").toLocalDate());
            return Optional.of(user);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<User> getAllUser() {
        String sqlQuery = "select * from users ";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> mapRowToUser(rs, rowNum));
    }

    @Override
    public int deleteAllUsers() {
        String sql = "delete from users";
        return jdbcTemplate.update(sql);
    }

    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());
        return user;
    }

}
