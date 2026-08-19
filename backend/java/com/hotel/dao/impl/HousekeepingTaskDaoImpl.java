package com.hotel.dao.impl;

import com.hotel.dao.HousekeepingTaskDao;
import com.hotel.dto.HousekeepingTaskDto;

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

public class HousekeepingTaskDaoImpl implements HousekeepingTaskDao {

    @Override
    public List<HousekeepingTaskDto> findPending(Connection connection)
            throws SQLException {
        String sql = """
                SELECT t.*, r.room_number, r.cleaning_status
                  FROM housekeeping_tasks t
                  JOIN rooms r ON r.room_id = t.room_id
                 WHERE t.status_code = 'PENDING'
                 ORDER BY
                    CASE t.priority_code
                        WHEN 'URGENT' THEN 1
                        WHEN 'HIGH' THEN 2
                        WHEN 'NORMAL' THEN 3
                        WHEN 'LOW' THEN 4
                        ELSE 5
                    END,
                    t.created_at ASC
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rows = statement.executeQuery()) {
            return mapList(rows);
        }
    }

    @Override
    public List<HousekeepingTaskDto> findAssignedTo(
            Connection connection,
            long staffId
    ) throws SQLException {
        String sql = """
                SELECT t.*, r.room_number, r.cleaning_status
                  FROM housekeeping_tasks t
                  JOIN rooms r ON r.room_id = t.room_id
                 WHERE t.assigned_staff_user_id = ?
                   AND t.status_code IN ('IN_PROGRESS', 'COMPLETED')
                 ORDER BY
                    CASE t.status_code
                        WHEN 'IN_PROGRESS' THEN 1
                        WHEN 'COMPLETED' THEN 2
                        ELSE 3
                    END,
                    t.scheduled_at ASC,
                    t.created_at ASC
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, staffId);

            try (ResultSet rows = statement.executeQuery()) {
                return mapList(rows);
            }
        }
    }

    @Override
    public Optional<HousekeepingTaskDto> findById(
            Connection connection,
            long taskId,
            boolean lock
    ) throws SQLException {
        String lockHint = lock ? " WITH (UPDLOCK, HOLDLOCK)" : "";

        String sql = """
                SELECT t.*, r.room_number, r.cleaning_status
                  FROM housekeeping_tasks t%s
                  JOIN rooms r ON r.room_id = t.room_id
                 WHERE t.housekeeping_task_id = ?
                """.formatted(lockHint);

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, taskId);

            try (ResultSet rows = statement.executeQuery()) {
                List<HousekeepingTaskDto> tasks = mapList(rows);
                return tasks.isEmpty()
                        ? Optional.empty()
                        : Optional.of(tasks.get(0));
            }
        }
    }

    @Override
    public int updateTaskStatus(
            Connection connection,
            long taskId,
            String status,
            Long staffId,
            LocalDateTime startedAt,
            LocalDateTime completedAt
    ) throws SQLException {
        String sql = """
                UPDATE housekeeping_tasks
                   SET status_code = ?,
                       assigned_staff_user_id = ?,
                       started_at = COALESCE(?, started_at),
                       completed_at = COALESCE(?, completed_at)
                 WHERE housekeeping_task_id = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);

            if (staffId == null) {
                statement.setNull(2, Types.BIGINT);
            } else {
                statement.setLong(2, staffId);
            }

            if (startedAt == null) {
                statement.setNull(3, Types.TIMESTAMP);
            } else {
                statement.setTimestamp(3, Timestamp.valueOf(startedAt));
            }

            if (completedAt == null) {
                statement.setNull(4, Types.TIMESTAMP);
            } else {
                statement.setTimestamp(4, Timestamp.valueOf(completedAt));
            }

            statement.setLong(5, taskId);

            return statement.executeUpdate();
        }
    }

    @Override
    public int updateRoomCleaningStatus(
            Connection connection,
            long roomId,
            String cleaningStatus
    ) throws SQLException {
        String sql = """
                UPDATE rooms
                   SET cleaning_status = ?,
                       updated_at = SYSUTCDATETIME()
                 WHERE room_id = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, cleaningStatus);
            statement.setLong(2, roomId);

            return statement.executeUpdate();
        }
    }

    private List<HousekeepingTaskDto> mapList(ResultSet rows)
            throws SQLException {
        List<HousekeepingTaskDto> tasks = new ArrayList<>();

        while (rows.next()) {
            tasks.add(new HousekeepingTaskDto(
                    rows.getLong("housekeeping_task_id"),
                    rows.getLong("room_id"),
                    rows.getString("room_number"),
                    rows.getString("cleaning_status"),
                    nullableLong(rows, "reservation_id"),
                    rows.getString("task_type"),
                    rows.getString("priority_code"),
                    rows.getString("status_code"),
                    nullableLong(rows, "assigned_staff_user_id"),
                    localDateTime(rows, "scheduled_at"),
                    localDateTime(rows, "started_at"),
                    localDateTime(rows, "completed_at"),
                    rows.getString("notes")
            ));
        }

        return tasks;
    }

    private Long nullableLong(ResultSet rows, String column)
            throws SQLException {
        Object value = rows.getObject(column);
        return value == null ? null : ((Number) value).longValue();
    }

    private LocalDateTime localDateTime(ResultSet rows, String column)
            throws SQLException {
        Timestamp value = rows.getTimestamp(column);
        return value == null ? null : value.toLocalDateTime();
    }
}