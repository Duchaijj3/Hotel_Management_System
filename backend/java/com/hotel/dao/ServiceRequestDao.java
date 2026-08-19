package com.hotel.dao;

import com.hotel.dto.ServiceRequestDto;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ServiceRequestDao {
    List<ServiceRequestDto> findPending(Connection connection) throws SQLException;

    List<ServiceRequestDto> findAssignedTo(Connection connection, long staffId)
            throws SQLException;

    Optional<ServiceRequestDto> findById(
            Connection connection,
            long requestId,
            boolean lock
    ) throws SQLException;

    int updateStatusAndStaff(
            Connection connection,
            long requestId,
            String status,
            Long staffId
    ) throws SQLException;

    int cancelRequest(
            Connection connection,
            long requestId,
            String cancellationReason
    ) throws SQLException;
}