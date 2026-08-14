package com.hotel.dto;
import java.math.BigDecimal; import java.time.*;
public record ReservationHistoryDto(String bookingCode,LocalDate checkIn,LocalDate checkOut,String status,int adults,int children,BigDecimal total,LocalDateTime bookedAt){}
