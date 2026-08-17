package com.hotel.dao.impl;

import com.hotel.dao.ServiceRequestDao;
import com.hotel.dto.ServiceRequestDto;
import java.sql.*;
import java.util.*;

public class ServiceRequestDaoImpl implements ServiceRequestDao {

    public List<ServiceRequestDto> findPending(Connection c) throws SQLException {
        String sql = "SELECT r.*, s.service_name FROM service_requests r JOIN hotel_services s ON r.hotel_service_id = s.service_id WHERE r.status = 'PENDING' ORDER BY r.requested_at ASC";
        try(PreparedStatement p = c.prepareStatement(sql)) {
            return mapList(p.executeQuery());
        }
    }

    public List<ServiceRequestDto> findAssignedTo(Connection c, long staffId) throws SQLException {
        String sql = "SELECT r.*, s.service_name FROM service_requests r JOIN hotel_services s ON r.hotel_service_id = s.service_id WHERE r.assigned_staff_id = ? AND r.status IN ('ASSIGNED', 'IN_PROGRESS') ORDER BY r.requested_at ASC";
        try(PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, staffId);
            return mapList(p.executeQuery());
        }
    }

    public Optional<ServiceRequestDto> findById(Connection c, long requestId, boolean lock) throws SQLException {
        // Tối ưu khóa dòng SQL Server để chống xung đột (concurrency)
        String sql = "SELECT r.*, s.service_name FROM service_requests r JOIN hotel_services s ON r.hotel_service_id = s.service_id WHERE r.service_request_id = ?" + (lock ? " WITH (UPDLOCK, HOLDLOCK)" : "");
        try(PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, requestId);
            List<ServiceRequestDto> list = mapList(p.executeQuery());
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        }
    }

    public int updateStatusAndStaff(Connection c, long requestId, String status, Long staffId) throws SQLException {
        String sql = "UPDATE service_requests SET status = ?, assigned_staff_id = ? WHERE service_request_id = ?";
        try(PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, status);
            if (staffId != null) p.setLong(2, staffId); else p.setNull(2, Types.BIGINT);
            p.setLong(3, requestId);
            return p.executeUpdate();
        }
    }
    @Override
    public int cancelRequest(Connection c, long requestId, String reason) throws SQLException {
        // Cập nhật trạng thái và ghi nhận lý do hủy (giả định cột cancellation_reason có tồn tại trong DB)
        String sql = "UPDATE service_requests SET status = 'CANCELLED', cancellation_reason = ? WHERE service_request_id = ?";
        try(PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, reason);
            p.setLong(2, requestId);
            return p.executeUpdate();
        }
    }

    private List<ServiceRequestDto> mapList(ResultSet rs) throws SQLException {
        List<ServiceRequestDto> list = new ArrayList<>();
        while(rs.next()) {
            list.add(new ServiceRequestDto(
                    rs.getLong("service_request_id"), rs.getLong("reservation_id"), rs.getLong("customer_id"),
                    rs.getLong("hotel_service_id"), rs.getString("service_name"), rs.getBigDecimal("quantity"),
                    rs.getBigDecimal("unit_price"), rs.getBigDecimal("total_amount"), rs.getString("status"),
                    rs.getObject("assigned_staff_id") != null ? rs.getLong("assigned_staff_id") : null,
                    rs.getTimestamp("requested_at") != null ? rs.getTimestamp("requested_at").toLocalDateTime() : null
            ));
        }
        return list;
    }
}