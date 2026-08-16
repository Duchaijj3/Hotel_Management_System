package com.hotel.dto;
import java.time.LocalDateTime;
public record AssignmentHistoryDto(long assignmentId,long reservationId,long reservationRoomId,String bookingCode,String bookingHolderName,
 String reservationStatus,String roomTypeName,String roomNumber,Integer floor,String assignedBy,LocalDateTime assignedAt,
 LocalDateTime unassignedAt,String unassignedReason,boolean current){
 public long getAssignmentId(){return assignmentId;}public long getReservationId(){return reservationId;}public long getReservationRoomId(){return reservationRoomId;}
 public String getBookingCode(){return bookingCode;}public String getBookingHolderName(){return bookingHolderName;}public String getReservationStatus(){return reservationStatus;}
 public String getRoomTypeName(){return roomTypeName;}public String getRoomNumber(){return roomNumber;}public Integer getFloor(){return floor;}
 public String getAssignedBy(){return assignedBy;}public LocalDateTime getAssignedAt(){return assignedAt;}public LocalDateTime getUnassignedAt(){return unassignedAt;}
 public String getUnassignedReason(){return unassignedReason;}public boolean isCurrent(){return current;}
}
