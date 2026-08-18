package com.hotel.dto;

import java.util.List;

public record RoomSelectionDto(long reservationRoomId, long roomTypeId, String roomTypeName,
                               int requiredQuantity, List<AvailableRoomDto> availableRooms) {
    public long getReservationRoomId(){return reservationRoomId;} public long getRoomTypeId(){return roomTypeId;}
    public String getRoomTypeName(){return roomTypeName;} public int getRequiredQuantity(){return requiredQuantity;}
    public List<AvailableRoomDto> getAvailableRooms(){return availableRooms;}
}