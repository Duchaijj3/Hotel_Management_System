package com.hotel.dto;

import java.time.LocalDateTime;

public record HousekeepingTaskDto(
        long taskId,
        long roomId,
        String roomNumber,
        Long reservationId,
        String taskType,
        String priorityCode,
        String statusCode,
        Long assignedStaffId,
        LocalDateTime scheduledAt,
        LocalDateTime startedAt,
        LocalDateTime completedAt,
        String notes
) {}