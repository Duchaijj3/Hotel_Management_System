package com.hotel.dao.impl;

import com.hotel.dao.HousekeepingTaskDao;
import com.hotel.dto.HousekeepingTaskDto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

public class HousekeepingTaskDaoImpl implements HousekeepingTaskDao {

    @Override
    public List<HousekeepingTaskDto> findPending(Connection c) throws SQLException {
        String sql = "SELECT t.*, r.room_number FROM housekeeping_tasks t JOIN rooms r ON t.room_id = r.room_id WHERE t.status_code = 'PENDING' ORDER BY t.priority_code DESC, t.created_at ASC";
        try(PreparedStatement p = c.prepareStatement(sql)) {
            return mapList(p.executeQuery());
        }
    }

    @Override
    public List<HousekeepingTaskDto> findAssignedTo(Connection c, long staffId) throws SQLException {
        String sql = "SELECT t.*, r.room_number FROM housekeeping_tasks t JOIN rooms r ON t.room_id = r.room_id WHERE t.assigned_staff_user_id = ? AND t.status_code IN ('ASSIGNED', 'IN_PROGRESS') ORDER BY t.scheduled_at ASC";
        try(PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, staffId);
            return mapList(p.executeQuery());
        }
    }

    @Override
    public Optional<HousekeepingTaskDto> findById(Connection c, long taskId, boolean lock) throws SQLException {
        // Lock dòng để tránh 2 nhân viên cùng nhận 1 phòng
        String sql = "SELECT t.*, r.room_number FROM housekeeping_tasks t JOIN rooms r ON t.room_id = r.room_id WHERE t.housekeeping_task_id = ?" + (lock ? " WITH (UPDLOCK, HOLDLOCK)" : "");
        try(PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, taskId);
            List<HousekeepingTaskDto> list = mapList(p.executeQuery());
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        }
    }

    @Override
    public int updateTaskStatus(Connection c, long taskId, String status, Long staffId, LocalDateTime startedAt, LocalDateTime completedAt) throws SQLException {
        String sql = "UPDATE housekeeping_tasks SET status_code = ?, assigned_staff_user_id = ?, started_at = COALESCE(?, started_at), completed_at = COALESCE(?, completed_at) WHERE housekeeping_task_id = ?";
        try(PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, status);
            if (staffId != null) p.setLong(2, staffId); else p.setNull(2, Types.BIGINT);
            if (startedAt != null) p.setTimestamp(3, Timestamp.valueOf(startedAt)); else p.setNull(3, Types.TIMESTAMP);
            if (completedAt != null) p.setTimestamp(4, Timestamp.valueOf(completedAt)); else p.setNull(4, Types.TIMESTAMP);
            p.setLong(5, taskId);
            return p.executeUpdate();
        }
    }

    @Override
    public int updateRoomCleaningStatus(Connection c, long roomId, String cleaningStatus) throws SQLException {
        String sql = "UPDATE rooms SET cleaning_status = ?, updated_at = SYSUTCDATETIME() WHERE room_id = ?";
        try(PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, cleaningStatus);
            p.setLong(2, roomId);
            return p.executeUpdate();
        }
    }

    private List<HousekeepingTaskDto> mapList(ResultSet rs) throws SQLException {
        List<HousekeepingTaskDto> list = new ArrayList<>();
        while(rs.next()) {
            list.add(new HousekeepingTaskDto(
                    rs.getLong("housekeeping_task_id"), rs.getLong("room_id"), rs.getString("room_number"),
                    rs.getObject("reservation_id") != null ? rs.getLong("reservation_id") : null,
                    rs.getString("task_type"), rs.getString("priority_code"), rs.getString("status_code"),
                    rs.getObject("assigned_staff_user_id") != null ? rs.getLong("assigned_staff_user_id") : null,
                    rs.getTimestamp("scheduled_at") != null ? rs.getTimestamp("scheduled_at").toLocalDateTime() : null,
                    rs.getTimestamp("started_at") != null ? rs.getTimestamp("started_at").toLocalDateTime() : null,
                    rs.getTimestamp("completed_at") != null ? rs.getTimestamp("completed_at").toLocalDateTime() : null,
                    rs.getString("notes")
            ));
        }
        return list;
    }
}