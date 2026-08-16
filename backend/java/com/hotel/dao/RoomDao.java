package com.hotel.dao;
import com.hotel.dto.*;import java.time.LocalDate;import java.util.*;
public interface RoomDao{
 List<RoomTypeOptionDto> findActiveRoomTypes();
 List<AvailableRoomDto> searchAvailable(LocalDate checkIn,LocalDate checkOut,Long roomTypeId,int adults,int children,Integer floor);
}
