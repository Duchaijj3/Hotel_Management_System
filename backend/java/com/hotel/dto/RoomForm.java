package com.hotel.dto;

import java.time.LocalDateTime;

public record RoomForm(Long id, long roomTypeId, String roomNumber, Integer floorNumber,
                       String operationalStatus, String notes, boolean active,
                       LocalDateTime version) {
    public Long getId() { return id; }
    public long getRoomTypeId() { return roomTypeId; }
    public String getRoomNumber() { return roomNumber; }
    public Integer getFloorNumber() { return floorNumber; }
    public String getOperationalStatus() { return operationalStatus; }
    public String getNotes() { return notes; }
    public boolean isActive() { return active; }
    public LocalDateTime getVersion() { return version; }
    public LocalDateTime getUpdatedAt() { return version; }
}
