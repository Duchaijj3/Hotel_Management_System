package java.com.hotel.model;

// File: src/main/java/com/hotel/model/Room.java

import java.time.LocalDateTime;

/**
 * Room entity with separate operational and cleaning status
 */
public class Room {
    private long roomId;
    private long roomTypeId;
    private String roomNumber;              // E.g., "101", "202"
    private int floorNumber;
    private String operationalStatus;       // AVAILABLE, OCCUPIED, MAINTENANCE, OUT_OF_SERVICE
    private String cleaningStatus;          // CLEAN, DIRTY, CLEANING, INSPECTED
    private String notes;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // For display purposes
//    private RoomType roomType;

    public Room() {}

    // Getters and Setters
    public long getRoomId() { return roomId; }
    public void setRoomId(long roomId) { this.roomId = roomId; }

    public long getRoomTypeId() { return roomTypeId; }
    public void setRoomTypeId(long roomTypeId) { this.roomTypeId = roomTypeId; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public int getFloorNumber() { return floorNumber; }
    public void setFloorNumber(int floorNumber) { this.floorNumber = floorNumber; }

    public String getOperationalStatus() { return operationalStatus; }
    public void setOperationalStatus(String operationalStatus) { this.operationalStatus = operationalStatus; }

    public String getCleaningStatus() { return cleaningStatus; }
    public void setCleaningStatus(String cleaningStatus) { this.cleaningStatus = cleaningStatus; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

//    public RoomType getRoomType() { return roomType; }
//    public void setRoomType(RoomType roomType) { this.roomType = roomType; }
}