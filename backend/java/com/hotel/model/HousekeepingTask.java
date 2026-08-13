package com.hotel.model;

// File: src/main/java/com/hotel/model/HousekeepingTask.java

import java.time.LocalDateTime;

/**
 * HousekeepingTask entity - tracks room cleaning tasks
 */
public class HousekeepingTask {
    private long housekeepingTaskId;
    private long roomId;
    private long reservationId;           // Associated reservation (optional)
    private long assignedStaffUserId;     // Housekeeper assigned
    private long createdByUserId;         // Manager/supervisor who created
    private String taskType;              // CHECKOUT_CLEANING, STAYOVER_CLEANING, DEEP_CLEANING, INSPECTION
    private String priorityCode;          // LOW, NORMAL, HIGH, URGENT
    private String statusCode;            // PENDING, ASSIGNED, IN_PROGRESS, COMPLETED, CANCELLED
    private LocalDateTime scheduledAt;    // When task should start
    private LocalDateTime startedAt;      // When housekeeper started
    private LocalDateTime completedAt;    // When housekeeper finished
    private String notes;
    private LocalDateTime createdAt;

    public HousekeepingTask() {}

    // Getters and Setters
    public long getHousekeepingTaskId() { return housekeepingTaskId; }
    public void setHousekeepingTaskId(long housekeepingTaskId) { this.housekeepingTaskId = housekeepingTaskId; }

    public long getRoomId() { return roomId; }
    public void setRoomId(long roomId) { this.roomId = roomId; }

    public long getAssignedStaffUserId() { return assignedStaffUserId; }
    public void setAssignedStaffUserId(long assignedStaffUserId) { this.assignedStaffUserId = assignedStaffUserId; }

    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }

    public String getStatusCode() { return statusCode; }
    public void setStatusCode(String statusCode) { this.statusCode = statusCode; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    // ... add all other getters/setters
}
