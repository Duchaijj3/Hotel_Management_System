package com.hotel.dto;

public record RoomStateDto(long roomId, long roomTypeId, String roomNumber, Integer floor,
                           String operationalStatus, String cleaningStatus, boolean active,
                           boolean currentlyAssigned) {
    public long getRoomId(){return roomId;} public long getRoomTypeId(){return roomTypeId;}
    public String getRoomNumber(){return roomNumber;} public Integer getFloor(){return floor;}
    public String getOperationalStatus(){return operationalStatus;} public String getCleaningStatus(){return cleaningStatus;}
    public boolean isActive(){return active;} public boolean isCurrentlyAssigned(){return currentlyAssigned;}
}
