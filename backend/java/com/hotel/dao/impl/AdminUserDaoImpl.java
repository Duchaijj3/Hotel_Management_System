package com.hotel.dao.impl;

import com.hotel.dao.AdminUserDao;
import com.hotel.dto.PageResult;
import com.hotel.dto.ProfileFormDto;
import com.hotel.dto.UserDetailDto;
import com.hotel.dto.UserFormDto;
import com.hotel.dto.UserSearchCriteria;
import com.hotel.dto.UserSummaryDto;
import com.hotel.exception.DataAccessException;
import com.hotel.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class AdminUserDaoImpl implements AdminUserDao {
    private static final String SEARCH_WHERE = """
             WHERE (? IS NULL OR role_code = ?)
               AND (? IS NULL OR status_code = ?)
               AND (? IS NULL OR LOWER(email) LIKE LOWER(?)
                    OR LOWER(full_name) LIKE LOWER(?)
                    OR LOWER(COALESCE(phone, '')) LIKE LOWER(?))
            """;

    @Override
    public PageResult<UserSummaryDto> search(UserSearchCriteria criteria) {
        String countSql = "SELECT COUNT(*) FROM dbo.[users]" + SEARCH_WHERE;
        String query = """
                SELECT user_id, email, full_name, phone, role_code, department_code, status_code
                  FROM dbo.[users]
                """ + SEARCH_WHERE + """
                 ORDER BY updated_at DESC, user_id DESC
                 OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
                """;
        String keyword = blankKeyword(criteria.keyword());
        try (Connection connection = DBConnection.getConnection()) {
            long total = count(connection, countSql, criteria, keyword);
            List<UserSummaryDto> items = new ArrayList<>();
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                int index = bindSearch(statement, criteria, keyword);
                statement.setInt(index++, (criteria.page() - 1) * criteria.pageSize());
                statement.setInt(index, criteria.pageSize());
                try (ResultSet rows = statement.executeQuery()) {
                    while (rows.next()) {
                        items.add(mapSummary(rows));
                    }
                }
            }
            return new PageResult<>(items, criteria.page(), criteria.pageSize(), total);
        } catch (SQLException exception) {
            throw new DataAccessException("Unable to search users", exception);
        }
    }

    @Override
    public Optional<UserDetailDto> findById(long id) {
        return findDetail("user_id = ?", id);
    }

    @Override
    public Optional<UserDetailDto> findProfile(long userId) {
        return findDetail("user_id = ?", userId);
    }

    @Override
    public boolean emailExists(String email, Long excludeId) {
        String sql = excludeId == null
                ? "SELECT 1 FROM dbo.[users] WHERE email = ?"
                : "SELECT 1 FROM dbo.[users] WHERE email = ? AND user_id <> ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            if (excludeId != null) {
                statement.setLong(2, excludeId);
            }
            try (ResultSet rows = statement.executeQuery()) {
                return rows.next();
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Unable to check email", exception);
        }
    }

    @Override
    public long create(UserFormDto form, String passwordHash, long actorId) {
        String sql = """
                INSERT INTO dbo.[users]
                    (email, password_hash, full_name, phone, role_code, department_code,
                     status_code, plain_password, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, SYSUTCDATETIME(), SYSUTCDATETIME())
                """;
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {
            bindUser(statement, form, passwordHash);
            statement.setString(8, form.password());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
                throw new SQLException("No generated key");
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Unable to create user", exception);
        }
    }

    @Override
    public boolean update(UserFormDto form, long actorId) {
        String sql = """
                UPDATE dbo.[users]
                   SET full_name = ?, phone = ?, role_code = ?, department_code = ?,
                       status_code = ?, updated_at = SYSUTCDATETIME()
                 WHERE user_id = ?
                """;
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, form.fullName());
            setNullable(statement, 2, form.phone());
            statement.setString(3, form.roleCode());
            setNullable(statement, 4, form.departmentCode());
            statement.setString(5, form.statusCode());
            statement.setLong(6, form.id());
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            throw new DataAccessException("Unable to update user", exception);
        }
    }

    @Override
    public boolean updateProfile(long userId, ProfileFormDto form) {
        String sql = """
                UPDATE dbo.[users]
                   SET full_name = ?, phone = ?, updated_at = SYSUTCDATETIME()
                 WHERE user_id = ?
                """;
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, form.fullName());
            setNullable(statement, 2, form.phone());
            statement.setLong(3, userId);
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            throw new DataAccessException("Unable to update profile", exception);
        }
    }

    @Override
    public boolean updatePassword(long userId, String passwordHash, String plainPassword) {
        String sql = """
                UPDATE dbo.[users]
                   SET password_hash = ?, plain_password = ?, failed_login_attempts = 0, locked_until = NULL,
                       updated_at = SYSUTCDATETIME()
                 WHERE user_id = ?
                """;
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, passwordHash);
            statement.setString(2, plainPassword);
            statement.setLong(3, userId);
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            throw new DataAccessException("Unable to update password", exception);
        }
    }

    @Override
    public boolean setStatus(long userId, String statusCode, long actorId) {
        String sql = """
                UPDATE dbo.[users]
                   SET status_code = ?, updated_at = SYSUTCDATETIME()
                 WHERE user_id = ?
                """;
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, statusCode);
            statement.setLong(2, userId);
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            throw new DataAccessException("Unable to update user status", exception);
        }
    }

    @Override
    public boolean resetLockout(long userId, long actorId) {
        String sql = """
                UPDATE dbo.[users]
                   SET failed_login_attempts = 0, locked_until = NULL,
                       updated_at = SYSUTCDATETIME()
                 WHERE user_id = ?
                """;
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            throw new DataAccessException("Unable to reset lockout", exception);
        }
    }

    @Override
    public Optional<String> findPasswordHash(long userId) {
        String sql = "SELECT password_hash FROM dbo.[users] WHERE user_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            try (ResultSet rows = statement.executeQuery()) {
                if (!rows.next()) {
                    return Optional.empty();
                }
                return Optional.ofNullable(rows.getString("password_hash"));
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Unable to load password hash", exception);
        }
    }

    private Optional<UserDetailDto> findDetail(String where, long id) {
        String sql = """
                SELECT user_id, email, full_name, phone, role_code, department_code, status_code,
                       failed_login_attempts, locked_until, last_login_at, created_at, updated_at, plain_password
                  FROM dbo.[users]
                 WHERE
                """ + where;
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet rows = statement.executeQuery()) {
                if (!rows.next()) {
                    return Optional.empty();
                }
                return Optional.of(mapDetail(rows));
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Unable to load user", exception);
        }
    }

    private long count(Connection connection, String sql, UserSearchCriteria criteria,
                       String keyword) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            bindSearch(statement, criteria, keyword);
            try (ResultSet rows = statement.executeQuery()) {
                rows.next();
                return rows.getLong(1);
            }
        }
    }

    private int bindSearch(PreparedStatement statement, UserSearchCriteria criteria,
                           String keyword) throws SQLException {
        int index = 1;
        
        // Bind roleCode (2 parameters: check and value)
        if (criteria.roleCode() == null || criteria.roleCode().isBlank()) {
            statement.setNull(index++, Types.NVARCHAR);
            statement.setNull(index++, Types.NVARCHAR);
        } else {
            statement.setString(index++, criteria.roleCode());
            statement.setString(index++, criteria.roleCode());
        }

        // Bind statusCode (2 parameters: check and value)
        if (criteria.statusCode() == null || criteria.statusCode().isBlank()) {
            statement.setNull(index++, Types.NVARCHAR);
            statement.setNull(index++, Types.NVARCHAR);
        } else {
            statement.setString(index++, criteria.statusCode());
            statement.setString(index++, criteria.statusCode());
        }

        // Bind keyword (4 parameters: check and 3 search targets)
        if (keyword == null) {
            statement.setNull(index++, Types.NVARCHAR);
            statement.setNull(index++, Types.NVARCHAR);
            statement.setNull(index++, Types.NVARCHAR);
            statement.setNull(index++, Types.NVARCHAR);
        } else {
            statement.setString(index++, keyword);
            statement.setString(index++, keyword);
            statement.setString(index++, keyword);
            statement.setString(index++, keyword);
        }
        return index;
    }

    private void bindUser(PreparedStatement statement, UserFormDto form, String passwordHash)
            throws SQLException {
        statement.setString(1, form.email().trim().toLowerCase(Locale.ROOT));
        statement.setString(2, passwordHash);
        statement.setString(3, form.fullName());
        setNullable(statement, 4, form.phone());
        statement.setString(5, form.roleCode());
        setNullable(statement, 6, form.departmentCode());
        statement.setString(7, form.statusCode());
    }

    private UserSummaryDto mapSummary(ResultSet rows) throws SQLException {
        return new UserSummaryDto(
                rows.getLong("user_id"),
                rows.getString("email"),
                rows.getString("full_name"),
                rows.getString("phone"),
                rows.getString("role_code"),
                rows.getString("department_code"),
                rows.getString("status_code"));
    }

    private UserDetailDto mapDetail(ResultSet rows) throws SQLException {
        return new UserDetailDto(
                rows.getLong("user_id"),
                rows.getString("email"),
                rows.getString("full_name"),
                rows.getString("phone"),
                rows.getString("role_code"),
                rows.getString("department_code"),
                rows.getString("status_code"),
                rows.getInt("failed_login_attempts"),
                toLocalDateTime(rows.getTimestamp("locked_until")),
                toLocalDateTime(rows.getTimestamp("last_login_at")),
                toLocalDateTime(rows.getTimestamp("created_at")),
                toLocalDateTime(rows.getTimestamp("updated_at")),
                rows.getString("plain_password"));
    }

    private static String blankKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        return "%" + keyword.trim() + "%";
    }

    private static int bindNullable(PreparedStatement statement, int index, String value)
            throws SQLException {
        if (value == null || value.isBlank()) {
            statement.setNull(index++, Types.NVARCHAR);
            statement.setNull(index++, Types.NVARCHAR);
        } else {
            statement.setString(index++, value);
            statement.setString(index++, value);
        }
        return index;
    }

    private static void setNullable(PreparedStatement statement, int index, String value)
            throws SQLException {
        if (value == null || value.isBlank()) {
            statement.setNull(index, Types.NVARCHAR);
        } else {
            statement.setString(index, value);
        }
    }

    private static java.time.LocalDateTime toLocalDateTime(Timestamp value) {
        return value == null ? null : value.toLocalDateTime();
    }
}
