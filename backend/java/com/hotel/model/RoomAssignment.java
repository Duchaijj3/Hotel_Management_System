package com.hotel.model;

// File: src/main/java/com/hotel/model/RoomAssignment.java

import java.time.LocalDateTime;

/**
 * RoomAssignment entity - tracks physical room assignment during stay
 * Allows reassignment of rooms during a reservation
 */
public class RoomAssignment {
    private long roomAssignmentId;
    private long reservationRoomId;        // References reservation_rooms
    private long roomId;                   // Physical room assigned
    private long assignedByUserId;         // Staff who made assignment
    private LocalDateTime assignedAt;
    private LocalDateTime unassignedAt;    // When reassigned or checkout
    private String unassignedReason;       // E.g., "Guest requested upgrade", "Maintenance issue"
    private boolean current;               // Is this the current assignment?

    public RoomAssignment() {}

    // Getters and Setters
    public long getRoomAssignmentId() { return roomAssignmentId; }
    public void setRoomAssignmentId(long roomAssignmentId) { this.roomAssignmentId = roomAssignmentId; }

    public long getReservationRoomId() { return reservationRoomId; }
    public void setReservationRoomId(long reservationRoomId) { this.reservationRoomId = reservationRoomId; }

    public long getRoomId() { return roomId; }
    public void setRoomId(long roomId) { this.roomId = roomId; }

    public long getAssignedByUserId() { return assignedByUserId; }
    public void setAssignedByUserId(long assignedByUserId) { this.assignedByUserId = assignedByUserId; }

    public LocalDateTime getAssignedAt() { return assignedAt; }
    public void setAssignedAt(LocalDateTime assignedAt) { this.assignedAt = assignedAt; }

    public LocalDateTime getUnassignedAt() { return unassignedAt; }
    public void setUnassignedAt(LocalDateTime unassignedAt) { this.unassignedAt = unassignedAt; }

    public String getUnassignedReason() { return unassignedReason; }
    public void setUnassignedReason(String unassignedReason) { this.unassignedReason = unassignedReason; }

    public boolean isCurrent() { return current; }
    public void setCurrent(boolean current) { this.current = current; }
}
