package com.hotel.dao;

import com.hotel.dto.ServiceRequestDto;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ServiceRequestDao {
    List<ServiceRequestDto> findPending(Connection c) throws SQLException;
    List<ServiceRequestDto> findAssignedTo(Connection c, long staffId) throws SQLException;
    Optional<ServiceRequestDto> findById(Connection c, long requestId, boolean lock) throws SQLException;
    int updateStatusAndStaff(Connection c, long requestId, String status, Long staffId) throws SQLException;
    int cancelRequest(Connection c, long requestId, String reason) throws SQLException;
}