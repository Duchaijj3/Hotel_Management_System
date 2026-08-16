package com.hotel.dto;

import java.time.LocalDate;

public record AssignmentTargetDto(long reservationRoomId, long reservationId, String bookingCode,
                                  String customerName, String statusCode, long roomTypeId,
                                  String roomTypeName, int quantity, int assignedCount,
                                  LocalDate checkInDate, LocalDate checkOutDate) {
    public long getReservationRoomId(){return reservationRoomId;} public long getReservationId(){return reservationId;}
    public String getBookingCode(){return bookingCode;} public String getCustomerName(){return customerName;}
    public String getStatusCode(){return statusCode;} public long getRoomTypeId(){return roomTypeId;}
    public String getRoomTypeName(){return roomTypeName;} public int getQuantity(){return quantity;}
    public int getAssignedCount(){return assignedCount;} public LocalDate getCheckInDate(){return checkInDate;}
    public LocalDate getCheckOutDate(){return checkOutDate;} public boolean isComplete(){return assignedCount>=quantity;}
}
