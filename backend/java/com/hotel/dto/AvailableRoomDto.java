package com.hotel.dto;
import java.time.LocalDate;
public record AvailableRoomDto(long roomId,String roomNumber,Integer floor,String typeCode,String typeName,String bedType,int maxAdults,int maxChildren,String operationalStatus,String cleaningStatus,LocalDate checkIn,LocalDate checkOut){}
