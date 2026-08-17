package com.hotel.dao.impl;

import com.hotel.dao.UserDao;
import com.hotel.exception.DataAccessException;
import com.hotel.model.User;
import com.hotel.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

public class UserDaoImpl implements UserDao {
    @Override
    public Optional<User> findByEmail(String email) {
        String sql = """
                SELECT user_id, email, password_hash, full_name, role_code, status_code,
                       failed_login_attempts, locked_until
                  FROM dbo.[users]
                 WHERE email = ?
                """;
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    return Optional.empty();
                }
                User user = new User();
                user.setUserId(result.getLong("user_id"));
                user.setEmail(result.getString("email"));
                user.setPasswordHash(result.getString("password_hash"));
                user.setFullName(result.getString("full_name"));
                user.setRoleCode(result.getString("role_code"));
                user.setStatusCode(result.getString("status_code"));
                user.setFailedLoginAttempts(result.getInt("failed_login_attempts"));
                Timestamp lockedUntil = result.getTimestamp("locked_until");
                user.setLockedUntil(lockedUntil == null ? null : lockedUntil.toLocalDateTime());
                return Optional.of(user);
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Unable to load account", exception);
        }
    }

    @Override
    public void updateLoginFailure(long userId, int failedAttempts,
                                   LocalDateTime lockedUntil) {
        String sql = """
                UPDATE dbo.[users]
                   SET failed_login_attempts = ?, locked_until = ?,
                       updated_at = SYSUTCDATETIME()
                 WHERE user_id = ?
                """;
        updateAttempt(sql, userId, failedAttempts, lockedUntil);
    }

    @Override
    public void updateLoginSuccess(long userId, LocalDateTime loggedInAt) {
        String sql = """
                UPDATE dbo.[users]
                   SET failed_login_attempts = 0, locked_until = NULL,
                       last_login_at = ?, updated_at = SYSUTCDATETIME()
                 WHERE user_id = ?
                """;
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setTimestamp(1, Timestamp.valueOf(loggedInAt));
            statement.setLong(2, userId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new DataAccessException("Unable to update successful login", exception);
        }
    }

    private void updateAttempt(String sql, long userId, int failedAttempts,
                               LocalDateTime lockedUntil) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, failedAttempts);
            if (lockedUntil == null) {
                statement.setNull(2, java.sql.Types.TIMESTAMP);
            } else {
                statement.setTimestamp(2, Timestamp.valueOf(lockedUntil));
            }
            statement.setLong(3, userId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new DataAccessException("Unable to update failed login", exception);
        }
    }
}
