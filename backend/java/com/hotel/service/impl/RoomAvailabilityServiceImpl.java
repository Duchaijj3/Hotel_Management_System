package com.hotel.service.impl;
import com.hotel.dao.RoomDao;import com.hotel.dto.*;import com.hotel.exception.BusinessException;import com.hotel.service.RoomAvailabilityService;import java.time.LocalDate;import java.util.*;
public class RoomAvailabilityServiceImpl implements RoomAvailabilityService{
 private final RoomDao dao;public RoomAvailabilityServiceImpl(RoomDao dao){this.dao=dao;}
 public List<RoomTypeOptionDto> roomTypes(){return dao.findActiveRoomTypes();}
 public List<AvailableRoomDto> search(LocalDate checkIn,LocalDate checkOut,Long roomTypeId,int adults,int children,Integer floor)throws BusinessException{
  if(checkIn==null||checkOut==null)throw new BusinessException("Vui lòng chọn ngày nhận và ngày trả phòng.");
  if(!checkOut.isAfter(checkIn))throw new BusinessException("Ngày trả phòng phải sau ngày nhận phòng.");
  if(roomTypeId!=null&&roomTypeId<=0)throw new BusinessException("Loại phòng không hợp lệ.");
  if(adults<1||adults>20||children<0||children>20)throw new BusinessException("Số lượng khách không hợp lệ.");
  return dao.searchAvailable(checkIn,checkOut,roomTypeId,adults,children,floor);
 }
}
