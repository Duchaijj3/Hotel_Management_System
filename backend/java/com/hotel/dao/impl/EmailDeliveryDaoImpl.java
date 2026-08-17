package com.hotel.dao.impl;

import com.hotel.dao.EmailDeliveryDao;
import com.hotel.dto.EmailDeliveryDto;
import com.hotel.dto.EmailDeliverySearchCriteria;
import com.hotel.dto.PageResult;
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
import java.util.Optional;

public class EmailDeliveryDaoImpl implements EmailDeliveryDao {
    private static final String SEARCH_WHERE = """
             WHERE (? IS NULL OR status_code = ?)
               AND (? IS NULL OR event_code = ?)
            """;

    @Override
    public PageResult<EmailDeliveryDto> search(EmailDeliverySearchCriteria criteria) {
        String countSql = "SELECT COUNT(*) FROM dbo.email_deliveries" + SEARCH_WHERE;
        String query = """
                SELECT delivery_id, recipient_email, subject, event_code, status_code,
                       error_message, retry_count, sent_at, created_at
                  FROM dbo.email_deliveries
                """ + SEARCH_WHERE + """
                 ORDER BY created_at DESC, delivery_id DESC
                 OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
                """;
        try (Connection connection = DBConnection.getConnection()) {
            long total = count(connection, countSql, criteria);
            List<EmailDeliveryDto> items = new ArrayList<>();
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                int index = bindSearch(statement, criteria);
                statement.setInt(index++, (criteria.page() - 1) * criteria.pageSize());
                statement.setInt(index, criteria.pageSize());
                try (ResultSet rows = statement.executeQuery()) {
                    while (rows.next()) {
                        items.add(map(rows));
                    }
                }
            }
            return new PageResult<>(items, criteria.page(), criteria.pageSize(), total);
        } catch (SQLException exception) {
            throw new DataAccessException("Unable to search email deliveries", exception);
        }
    }

    @Override
    public Optional<EmailDeliveryDto> findById(long id) {
        String sql = """
                SELECT delivery_id, recipient_email, subject, event_code, status_code,
                       error_message, retry_count, sent_at, created_at
                  FROM dbo.email_deliveries
                 WHERE delivery_id = ?
                """;
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet rows = statement.executeQuery()) {
                if (!rows.next()) {
                    return Optional.empty();
                }
                return Optional.of(map(rows));
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Unable to load email delivery", exception);
        }
    }

    @Override
    public long queue(String recipientEmail, String subject, String eventCode, Long templateId) {
        String sql = """
                INSERT INTO dbo.email_deliveries
                    (email_template_id, recipient_email, subject, event_code, status_code,
                     retry_count, created_at)
                VALUES (?, ?, ?, ?, 'PENDING', 0, SYSUTCDATETIME())
                """;
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {
            if (templateId == null) {
                statement.setNull(1, Types.BIGINT);
            } else {
                statement.setLong(1, templateId);
            }
            statement.setString(2, recipientEmail);
            statement.setString(3, subject);
            statement.setString(4, eventCode);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
                throw new SQLException("No generated key");
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Unable to queue email", exception);
        }
    }

    @Override
    public boolean markSent(long deliveryId) {
        String sql = """
                UPDATE dbo.email_deliveries
                   SET status_code = 'SENT', sent_at = SYSUTCDATETIME(), error_message = NULL
                 WHERE delivery_id = ?
                """;
        return updateStatus(sql, deliveryId, null);
    }

    @Override
    public boolean markFailed(long deliveryId, String errorMessage) {
        String sql = """
                UPDATE dbo.email_deliveries
                   SET status_code = 'FAILED', error_message = ?
                 WHERE delivery_id = ?
                """;
        return updateStatus(sql, deliveryId, errorMessage);
    }

    @Override
    public boolean incrementRetry(long deliveryId) {
        String sql = """
                UPDATE dbo.email_deliveries
                   SET retry_count = retry_count + 1, status_code = 'PENDING', error_message = NULL
                 WHERE delivery_id = ?
                """;
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, deliveryId);
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            throw new DataAccessException("Unable to increment retry", exception);
        }
    }

    private boolean updateStatus(String sql, long deliveryId, String errorMessage) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            if (errorMessage == null) {
                statement.setLong(1, deliveryId);
            } else {
                statement.setString(1, errorMessage);
                statement.setLong(2, deliveryId);
            }
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            throw new DataAccessException("Unable to update delivery status", exception);
        }
    }

    private long count(Connection connection, String sql,
                       EmailDeliverySearchCriteria criteria) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            bindSearch(statement, criteria);
            try (ResultSet rows = statement.executeQuery()) {
                rows.next();
                return rows.getLong(1);
            }
        }
    }

    private int bindSearch(PreparedStatement statement, EmailDeliverySearchCriteria criteria)
            throws SQLException {
        int index = 1;
        index = bindNullable(statement, index, criteria.statusCode());
        return bindNullable(statement, index, criteria.eventCode());
    }

    private int bindNullable(PreparedStatement statement, int index, String value)
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

    private EmailDeliveryDto map(ResultSet rows) throws SQLException {
        Timestamp sentAt = rows.getTimestamp("sent_at");
        Timestamp createdAt = rows.getTimestamp("created_at");
        return new EmailDeliveryDto(
                rows.getLong("delivery_id"),
                rows.getString("recipient_email"),
                rows.getString("subject"),
                rows.getString("event_code"),
                rows.getString("status_code"),
                rows.getString("error_message"),
                rows.getInt("retry_count"),
                sentAt == null ? null : sentAt.toLocalDateTime(),
                createdAt == null ? null : createdAt.toLocalDateTime());
    }
}
