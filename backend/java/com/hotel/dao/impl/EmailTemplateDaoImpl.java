package com.hotel.dao.impl;

import com.hotel.dao.EmailTemplateDao;
import com.hotel.dto.EmailTemplateDetailDto;
import com.hotel.dto.EmailTemplateForm;
import com.hotel.dto.EmailTemplateSearchCriteria;
import com.hotel.dto.EmailTemplateSummaryDto;
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

public class EmailTemplateDaoImpl implements EmailTemplateDao {
    private static final String SEARCH_WHERE = """
             WHERE (? IS NULL OR event_code = ?)
               AND (? IS NULL OR is_active = ?)
               AND (? IS NULL OR LOWER(template_code) LIKE LOWER(?)
                    OR LOWER(template_name) LIKE LOWER(?))
            """;

    @Override
    public PageResult<EmailTemplateSummaryDto> search(EmailTemplateSearchCriteria criteria) {
        String countSql = "SELECT COUNT(*) FROM dbo.email_templates" + SEARCH_WHERE;
        String query = """
                SELECT email_template_id, template_code, template_name, event_code, is_active
                  FROM dbo.email_templates
                """ + SEARCH_WHERE + """
                 ORDER BY updated_at DESC, email_template_id DESC
                 OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
                """;
        String keyword = blankKeyword(criteria.keyword());
        try (Connection connection = DBConnection.getConnection()) {
            long total = count(connection, countSql, criteria, keyword);
            List<EmailTemplateSummaryDto> items = new ArrayList<>();
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                int index = bindSearch(statement, criteria, keyword);
                statement.setInt(index++, (criteria.page() - 1) * criteria.pageSize());
                statement.setInt(index, criteria.pageSize());
                try (ResultSet rows = statement.executeQuery()) {
                    while (rows.next()) {
                        items.add(new EmailTemplateSummaryDto(
                                rows.getLong("email_template_id"),
                                rows.getString("template_code"),
                                rows.getString("template_name"),
                                rows.getString("event_code"),
                                rows.getBoolean("is_active")));
                    }
                }
            }
            return new PageResult<>(items, criteria.page(), criteria.pageSize(), total);
        } catch (SQLException exception) {
            throw new DataAccessException("Unable to search email templates", exception);
        }
    }

    @Override
    public Optional<EmailTemplateDetailDto> findById(long id) {
        return findDetail("email_template_id = ?", id);
    }

    @Override
    public Optional<EmailTemplateDetailDto> findByEventCode(String eventCode) {
        return findDetail("event_code = ? AND is_active = 1", eventCode);
    }

    @Override
    public boolean codeExists(String templateCode, Long excludeId) {
        String sql = excludeId == null
                ? "SELECT 1 FROM dbo.email_templates WHERE template_code = ?"
                : "SELECT 1 FROM dbo.email_templates WHERE template_code = ?"
                + " AND email_template_id <> ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, templateCode);
            if (excludeId != null) {
                statement.setLong(2, excludeId);
            }
            try (ResultSet rows = statement.executeQuery()) {
                return rows.next();
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Unable to check template code", exception);
        }
    }

    @Override
    public long create(EmailTemplateForm form, long actorId) {
        String sql = """
                INSERT INTO dbo.email_templates
                    (template_code, template_name, event_code, subject_template, body_html,
                     body_text, is_active, created_by_user_id, updated_by_user_id,
                     created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, SYSUTCDATETIME(), SYSUTCDATETIME())
                """;
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {
            bindForm(statement, form, actorId, true);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
                throw new SQLException("No generated key");
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Unable to create email template", exception);
        }
    }

    @Override
    public boolean update(EmailTemplateForm form, long actorId) {
        String sql = """
                UPDATE dbo.email_templates
                   SET template_code = ?, template_name = ?, event_code = ?,
                       subject_template = ?, body_html = ?, body_text = ?, is_active = ?,
                       updated_by_user_id = ?, updated_at = SYSUTCDATETIME()
                 WHERE email_template_id = ?
                """;
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bindForm(statement, form, actorId, false);
            statement.setLong(9, form.id());
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            throw new DataAccessException("Unable to update email template", exception);
        }
    }

    @Override
    public boolean setActive(long id, boolean active, long actorId) {
        String sql = """
                UPDATE dbo.email_templates
                   SET is_active = ?, updated_by_user_id = ?, updated_at = SYSUTCDATETIME()
                 WHERE email_template_id = ?
                """;
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBoolean(1, active);
            statement.setLong(2, actorId);
            statement.setLong(3, id);
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            throw new DataAccessException("Unable to update template status", exception);
        }
    }

    @Override
    public boolean delete(long id) {
        String updateDeliveriesSql = "UPDATE dbo.email_deliveries SET email_template_id = NULL WHERE email_template_id = ?";
        String deleteTemplateSql = "DELETE FROM dbo.email_templates WHERE email_template_id = ?";
        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement updateStmt = connection.prepareStatement(updateDeliveriesSql)) {
                    updateStmt.setLong(1, id);
                    updateStmt.executeUpdate();
                }
                int deletedRows;
                try (PreparedStatement deleteStmt = connection.prepareStatement(deleteTemplateSql)) {
                    deleteStmt.setLong(1, id);
                    deletedRows = deleteStmt.executeUpdate();
                }
                connection.commit();
                return deletedRows == 1;
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Unable to delete email template", exception);
        }
    }

    private Optional<EmailTemplateDetailDto> findDetail(String where, Object value) {
        String sql = """
                SELECT email_template_id, template_code, template_name, event_code,
                       subject_template, body_html, body_text, is_active, created_at, updated_at
                  FROM dbo.email_templates
                 WHERE
                """ + where;
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            if (value instanceof Long longValue) {
                statement.setLong(1, longValue);
            } else {
                statement.setString(1, String.valueOf(value));
            }
            try (ResultSet rows = statement.executeQuery()) {
                if (!rows.next()) {
                    return Optional.empty();
                }
                return Optional.of(new EmailTemplateDetailDto(
                        rows.getLong("email_template_id"),
                        rows.getString("template_code"),
                        rows.getString("template_name"),
                        rows.getString("event_code"),
                        rows.getString("subject_template"),
                        rows.getString("body_html"),
                        rows.getString("body_text"),
                        rows.getBoolean("is_active"),
                        toLocalDateTime(rows.getTimestamp("created_at")),
                        toLocalDateTime(rows.getTimestamp("updated_at"))));
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Unable to load email template", exception);
        }
    }

    private long count(Connection connection, String sql, EmailTemplateSearchCriteria criteria,
                       String keyword) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            bindSearch(statement, criteria, keyword);
            try (ResultSet rows = statement.executeQuery()) {
                rows.next();
                return rows.getLong(1);
            }
        }
    }

    private int bindSearch(PreparedStatement statement, EmailTemplateSearchCriteria criteria,
                           String keyword) throws SQLException {
        int index = 1;
        
        // Bind eventCode (2 parameters: check and value)
        if (criteria.eventCode() == null || criteria.eventCode().isBlank()) {
            statement.setNull(index++, Types.NVARCHAR);
            statement.setNull(index++, Types.NVARCHAR);
        } else {
            statement.setString(index++, criteria.eventCode());
            statement.setString(index++, criteria.eventCode());
        }

        // Bind active (2 parameters: check and value)
        if (criteria.active() == null) {
            statement.setNull(index++, Types.BIT);
            statement.setNull(index++, Types.BIT);
        } else {
            statement.setString(index++, "x");
            statement.setBoolean(index++, criteria.active());
        }

        // Bind keyword (3 parameters: check and 2 search targets)
        if (keyword == null) {
            statement.setNull(index++, Types.NVARCHAR);
            statement.setNull(index++, Types.NVARCHAR);
            statement.setNull(index++, Types.NVARCHAR);
        } else {
            statement.setString(index++, keyword);
            statement.setString(index++, keyword);
            statement.setString(index++, keyword);
        }
        return index;
    }

    private void bindForm(PreparedStatement statement, EmailTemplateForm form, long actorId,
                        boolean create) throws SQLException {
        statement.setString(1, form.templateCode());
        statement.setString(2, form.templateName());
        statement.setString(3, form.eventCode());
        statement.setString(4, form.subjectTemplate());
        statement.setString(5, form.bodyHtml());
        setNullable(statement, 6, form.bodyText());
        statement.setBoolean(7, form.active());
        if (create) {
            statement.setLong(8, actorId);
        }
        statement.setLong(create ? 9 : 8, actorId);
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
