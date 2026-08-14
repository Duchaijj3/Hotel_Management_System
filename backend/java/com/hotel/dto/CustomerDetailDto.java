package com.hotel.dto;
import java.time.*; import java.util.List;
public record CustomerDetailDto(long id,String code,String fullName,String email,String phone,LocalDate dateOfBirth,String idType,String idNumber,String nationality,String address,String status,LocalDateTime createdAt,LocalDateTime updatedAt,List<ReservationHistoryDto> reservations,List<StayHistoryDto> stays){}
