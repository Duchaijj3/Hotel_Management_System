package com.hotel.service;
import com.hotel.dto.*;import com.hotel.exception.BusinessException;import java.util.*;
public interface FrontDeskService{
 Optional<AssignRoomPageDto> assignmentPage(long reservationRoomId);
 Optional<CheckInDto> checkInDetails(String identifier);
 List<ReservationSearchDto> searchReservations(String keyword);
 void checkIn(long reservationId,Map<Long,List<Long>> selectedRooms,boolean documentsChecked,long userId)throws BusinessException;
 Optional<RoomChangeDto> changeDetails(long assignmentId);List<AvailableRoomDto> changeCandidates(long assignmentId);
 void changeRoom(long assignmentId,long newRoomId,String reason,long userId)throws BusinessException;
 List<AssignmentHistoryDto> history(String identifier);
}
