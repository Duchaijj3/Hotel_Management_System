package com.hotel.dao;
import com.hotel.dto.*;import java.sql.*;import java.util.*;
public interface FrontDeskDao{
 Optional<AssignmentTargetDto> findAssignmentTarget(Connection c,long id,boolean lock)throws SQLException;
 Optional<RoomStateDto> findRoomState(Connection c,long id,boolean lock)throws SQLException;
 List<AvailableRoomDto> findAvailableRooms(Connection c,long reservationRoomId)throws SQLException;
 void insertAssignment(Connection c,long reservationRoomId,long roomId,long userId)throws SQLException;
 Optional<CheckInDto> findCheckIn(Connection c,String identifier,boolean lock)throws SQLException;
 List<ReservationSearchDto> searchReservations(Connection c,String keyword)throws SQLException;
 int markCheckedIn(Connection c,long reservationId,long userId)throws SQLException;
 void ensureDraftInvoice(Connection c,long reservationId)throws SQLException;
 Optional<RoomChangeDto> findRoomChange(Connection c,long assignmentId,boolean lock)throws SQLException;
 int closeAssignment(Connection c,long assignmentId,String reason)throws SQLException;
 int updateRoomStatus(Connection c,long roomId,String operational,String cleaning)throws SQLException;
 List<AssignmentHistoryDto> findHistory(Connection c,String identifier)throws SQLException;
}
