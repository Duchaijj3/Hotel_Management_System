package com.hotel.dto;

import java.time.LocalDateTime;

public record RoomView(long id, long roomTypeId, String typeCode, String typeName,
                       String roomNumber, Integer floorNumber, String operationalStatus,
                       String cleaningStatus, String notes, boolean active,
                       LocalDateTime updatedAt) {
    public long getId() { return id; }
    public long getRoomTypeId() { return roomTypeId; }
    public String getTypeCode() { return typeCode; }
    public String getTypeName() { return typeName; }
    public String getRoomNumber() { return roomNumber; }
    public Integer getFloorNumber() { return floorNumber; }
    public String getOperationalStatus() { return operationalStatus; }
    public String getCleaningStatus() { return cleaningStatus; }
    public String getNotes() { return notes; }
    public boolean isActive() { return active; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
