package com.hotel.dto;

import java.time.LocalDateTime;

public record HousekeepingTaskDto(
        long taskId,
        long roomId,
        String roomNumber,
        String cleaningStatus,
        Long reservationId,
        String taskType,
        String priorityCode,
        String statusCode,
        Long assignedStaffId,
        LocalDateTime scheduledAt,
        LocalDateTime startedAt,
        LocalDateTime completedAt,
        String notes
) {
    public long getTaskId() {
        return taskId;
    }

    public long getRoomId() {
        return roomId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getCleaningStatus() {
        return cleaningStatus;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public String getTaskType() {
        return taskType;
    }

    public String getPriorityCode() {
        return priorityCode;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public Long getAssignedStaffId() {
        return assignedStaffId;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public String getNotes() {
        return notes;
    }
}