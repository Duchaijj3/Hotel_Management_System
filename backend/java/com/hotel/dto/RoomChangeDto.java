package com.hotel.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record RoomChangeDto(long roomAssignmentId,long reservationId,long reservationRoomId,
 String bookingCode,String bookingHolderName,String reservationStatus,LocalDate checkInDate,LocalDate checkOutDate,
 long roomTypeId,String roomTypeName,long oldRoomId,String oldRoomNumber,Integer oldFloor,LocalDateTime assignedAt){
 public long getRoomAssignmentId(){return roomAssignmentId;}public long getReservationId(){return reservationId;}public long getReservationRoomId(){return reservationRoomId;}
 public String getBookingCode(){return bookingCode;}public String getBookingHolderName(){return bookingHolderName;}public String getReservationStatus(){return reservationStatus;}
 public LocalDate getCheckInDate(){return checkInDate;}public LocalDate getCheckOutDate(){return checkOutDate;}public long getRoomTypeId(){return roomTypeId;}
 public String getRoomTypeName(){return roomTypeName;}public long getOldRoomId(){return oldRoomId;}public String getOldRoomNumber(){return oldRoomNumber;}
 public Integer getOldFloor(){return oldFloor;}public LocalDateTime getAssignedAt(){return assignedAt;}
}
