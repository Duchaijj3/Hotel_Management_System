package com.hotel.integration;

import com.hotel.dao.impl.UserDaoImpl;
import com.hotel.service.AuthService;
import com.hotel.service.AuthenticationResult;
import com.hotel.util.DBConnection;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ManagerAuthenticationDatabaseIT {
    private static final Clock CLOCK = Clock.fixed(
            Instant.parse("2026-08-17T03:00:00Z"), ZoneOffset.UTC);

    @Test
    void managerLoginLockoutAndResetPersistThroughSqlServer() throws Exception {
        String email = "manager-it-" + System.nanoTime() + "@hotel.local";
        long userId = insertManager(email);
        try {
            AuthService service = new AuthService(new UserDaoImpl(), CLOCK);
            for (int attempt = 1; attempt <= 5; attempt++) {
                AuthenticationResult result = service.login(email, "wrong-password");
                AuthenticationResult.Status expected = attempt == 5
                        ? AuthenticationResult.Status.TEMPORARILY_LOCKED
                        : AuthenticationResult.Status.INVALID_CREDENTIALS;
                assertEquals(expected, result.status());
            }
            assertEquals(5, attempts(userId));

            expireTemporaryLock(userId);
            assertEquals(AuthenticationResult.Status.SUCCESS,
                    service.login(email, "correct-password").status());
            assertEquals(0, attempts(userId));
            assertNull(lockedUntil(userId));
        } finally {
            deleteUser(userId);
        }
    }

    private long insertManager(String email) throws Exception {
        String sql = """
                INSERT INTO dbo.[users]
                    (email, password_hash, full_name, role_code, department_code, status_code)
                VALUES (?, ?, N'Integration Manager', 'MANAGER', NULL, 'ACTIVE')
                """;
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, email);
            statement.setString(2, BCrypt.hashpw("correct-password", BCrypt.gensalt(4)));
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                keys.next();
                return keys.getLong(1);
            }
        }
    }

    private void expireTemporaryLock(long userId) throws Exception {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE dbo.[users] SET locked_until=? WHERE user_id=?")) {
            statement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.of(2026, 8, 17, 2, 59)));
            statement.setLong(2, userId);
            statement.executeUpdate();
        }
    }

    private int attempts(long userId) throws Exception {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT failed_login_attempts FROM dbo.[users] WHERE user_id=?")) {
            statement.setLong(1, userId);
            try (ResultSet rows = statement.executeQuery()) {
                rows.next();
                return rows.getInt(1);
            }
        }
    }

    private LocalDateTime lockedUntil(long userId) throws Exception {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT locked_until FROM dbo.[users] WHERE user_id=?")) {
            statement.setLong(1, userId);
            try (ResultSet rows = statement.executeQuery()) {
                rows.next();
                Timestamp value = rows.getTimestamp(1);
                return value == null ? null : value.toLocalDateTime();
            }
        }
    }

    private void deleteUser(long userId) throws Exception {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "DELETE FROM dbo.[users] WHERE user_id=?")) {
            statement.setLong(1, userId);
            statement.executeUpdate();
        }
    }
}
