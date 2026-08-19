package com.hotel.dao;

import com.hotel.dto.HousekeepingTaskDto;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HousekeepingTaskDao {

    List<HousekeepingTaskDto> findPending(Connection connection)
            throws SQLException;

    List<HousekeepingTaskDto> findAssignedTo(
            Connection connection,
            long staffId
    ) throws SQLException;

    Optional<HousekeepingTaskDto> findById(
            Connection connection,
            long taskId,
            boolean lock
    ) throws SQLException;

    int updateTaskStatus(
            Connection connection,
            long taskId,
            String status,
            Long staffId,
            LocalDateTime startedAt,
            LocalDateTime completedAt
    ) throws SQLException;

    int updateRoomCleaningStatus(
            Connection connection,
            long roomId,
            String cleaningStatus
    ) throws SQLException;
}