package com.hotel.dao;

import com.hotel.dto.HousekeepingTaskDto;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HousekeepingTaskDao {
    List<HousekeepingTaskDto> findPending(Connection c) throws SQLException;
    List<HousekeepingTaskDto> findAssignedTo(Connection c, long staffId) throws SQLException;
    Optional<HousekeepingTaskDto> findById(Connection c, long taskId, boolean lock) throws SQLException;

    // Cập nhật trạng thái Task và thời gian
    int updateTaskStatus(Connection c, long taskId, String status, Long staffId, LocalDateTime startedAt, LocalDateTime completedAt) throws SQLException;

    // Cập nhật trạng thái phòng (CLEAN, DIRTY, CLEANING, INSPECTED)
    int updateRoomCleaningStatus(Connection c, long roomId, String cleaningStatus) throws SQLException;
}