package com.hotel.dto;

import java.util.List;

public record AssignRoomPageDto(AssignmentTargetDto target, List<AvailableRoomDto> rooms) {
    public AssignmentTargetDto getTarget(){return target;} public List<AvailableRoomDto> getRooms(){return rooms;}
}