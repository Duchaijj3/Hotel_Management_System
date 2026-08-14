package com.hotel.dto;
import java.time.LocalDateTime;
public record StayHistoryDto(String bookingCode,LocalDateTime actualCheckIn,LocalDateTime actualCheckOut,String roomNumber,String roomType,LocalDateTime assignedAt,LocalDateTime unassignedAt,boolean current){}
