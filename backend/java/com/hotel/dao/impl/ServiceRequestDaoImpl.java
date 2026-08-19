package com.hotel.dao.impl;

import com.hotel.dao.ServiceRequestDao;
import com.hotel.dto.ServiceRequestDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ServiceRequestDaoImpl implements ServiceRequestDao {

    @Override
    public List<ServiceRequestDto> findPending(Connection connection)
            throws SQLException {
        String sql = """
                SELECT sr.*, hs.service_name
                  FROM service_requests sr
                  JOIN hotel_services hs
                    ON hs.hotel_service_id = sr.hotel_service_id
                 WHERE sr.status_code = 'PENDING'
                 ORDER BY sr.requested_for_at ASC, sr.requested_at ASC
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rows = statement.executeQuery()) {
            return mapList(rows);
        }
    }

    @Override
    public List<ServiceRequestDto> findAssignedTo(
            Connection connection,
            long staffId
    ) throws SQLException {
        String sql = """
                SELECT sr.*, hs.service_name
                  FROM service_requests sr
                  JOIN hotel_services hs
                    ON hs.hotel_service_id = sr.hotel_service_id
                 WHERE sr.assigned_staff_user_id = ?
                   AND sr.status_code IN ('ASSIGNED', 'IN_PROGRESS', 'COMPLETED')
                 ORDER BY
                    CASE sr.status_code
                        WHEN 'IN_PROGRESS' THEN 1
                        WHEN 'ASSIGNED' THEN 2
                        WHEN 'COMPLETED' THEN 3
                        ELSE 4
                    END,
                    sr.requested_for_at ASC,
                    sr.requested_at ASC
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, staffId);

            try (ResultSet rows = statement.executeQuery()) {
                return mapList(rows);
            }
        }
    }

    @Override
    public Optional<ServiceRequestDto> findById(
            Connection connection,
            long requestId,
            boolean lock
    ) throws SQLException {
        String lockHint = lock ? " WITH (UPDLOCK, HOLDLOCK)" : "";

        String sql = """
                SELECT sr.*, hs.service_name
                  FROM service_requests sr%s
                  JOIN hotel_services hs
                    ON hs.hotel_service_id = sr.hotel_service_id
                 WHERE sr.service_request_id = ?
                """.formatted(lockHint);

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, requestId);

            try (ResultSet rows = statement.executeQuery()) {
                List<ServiceRequestDto> requests = mapList(rows);
                return requests.isEmpty()
                        ? Optional.empty()
                        : Optional.of(requests.get(0));
            }
        }
    }

    @Override
    public int updateStatusAndStaff(
            Connection connection,
            long requestId,
            String status,
            Long staffId
    ) throws SQLException {
        String sql = """
                UPDATE service_requests
                   SET status_code = ?,
                       assigned_staff_user_id = ?,
                       assigned_at = CASE
                           WHEN ? = 'ASSIGNED' AND assigned_at IS NULL
                           THEN SYSUTCDATETIME()
                           ELSE assigned_at
                       END,
                       started_at = CASE
                           WHEN ? = 'IN_PROGRESS' AND started_at IS NULL
                           THEN SYSUTCDATETIME()
                           ELSE started_at
                       END,
                       completed_at = CASE
                           WHEN ? = 'COMPLETED' AND completed_at IS NULL
                           THEN SYSUTCDATETIME()
                           ELSE completed_at
                       END
                 WHERE service_request_id = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);

            if (staffId == null) {
                statement.setNull(2, Types.BIGINT);
            } else {
                statement.setLong(2, staffId);
            }

            statement.setString(3, status);
            statement.setString(4, status);
            statement.setString(5, status);
            statement.setLong(6, requestId);

            return statement.executeUpdate();
        }
    }

    @Override
    public int cancelRequest(
            Connection connection,
            long requestId,
            String cancellationReason
    ) throws SQLException {
        String sql = """
                UPDATE service_requests
                   SET status_code = 'CANCELLED',
                       cancellation_reason = ?
                 WHERE service_request_id = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, cancellationReason);
            statement.setLong(2, requestId);
            return statement.executeUpdate();
        }
    }

    private List<ServiceRequestDto> mapList(ResultSet rows) throws SQLException {
        List<ServiceRequestDto> requests = new ArrayList<>();

        while (rows.next()) {
            requests.add(new ServiceRequestDto(
                    rows.getLong("service_request_id"),
                    rows.getLong("reservation_id"),
                    rows.getLong("customer_id"),
                    rows.getLong("hotel_service_id"),
                    rows.getString("service_name"),
                    rows.getBigDecimal("quantity"),
                    rows.getBigDecimal("unit_price_snapshot"),
                    rows.getBigDecimal("total_amount"),
                    rows.getString("status_code"),
                    nullableLong(rows, "assigned_staff_user_id"),
                    rows.getString("notes"),
                    rows.getString("cancellation_reason"),
                    localDateTime(rows, "requested_at"),
                    localDateTime(rows, "requested_for_at"),
                    localDateTime(rows, "assigned_at"),
                    localDateTime(rows, "started_at"),
                    localDateTime(rows, "completed_at")
            ));
        }

        return requests;
    }

    private Long nullableLong(ResultSet rows, String column) throws SQLException {
        Object value = rows.getObject(column);
        return value == null ? null : ((Number) value).longValue();
    }

    private LocalDateTime localDateTime(ResultSet rows, String column)
            throws SQLException {
        Timestamp value = rows.getTimestamp(column);
        return value == null ? null : value.toLocalDateTime();
    }
}