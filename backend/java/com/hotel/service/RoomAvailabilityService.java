package com.hotel.service;
import com.hotel.dto.*;import com.hotel.exception.BusinessException;import java.time.LocalDate;import java.util.*;
public interface RoomAvailabilityService{
 List<RoomTypeOptionDto> roomTypes();
 List<AvailableRoomDto> search(LocalDate checkIn,LocalDate checkOut,Long roomTypeId,int adults,int children,Integer floor)throws BusinessException;
}
