package java.com.hotel.dao.impl;

// File: src/main/java/com/hotel/dao/impl/ServiceRequestDaoImpl.java


import java.com.hotel.dao.ServiceRequestDao;
import java.com.hotel.model.HotelService;
import java.com.hotel.model.ServiceRequest;
import java.com.hotel.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation of ServiceRequestDao interface
 * Interacts with dbo.service_requests and dbo.hotel_services
 */
public class ServiceRequestDaoImpl implements ServiceRequestDao {

    // Common SELECT clause with JOIN to populate embedded HotelService object
    private static final String SELECT_JOIN_SQL =
            "SELECT sr.service_request_id, sr.reservation_id, sr.customer_id, sr.hotel_service_id, " +
                    "       sr.assigned_staff_user_id, sr.quantity, sr.unit_price_snapshot, sr.total_amount, " +
                    "       sr.status_code, sr.requested_at, sr.assigned_at, sr.started_at, sr.completed_at, sr.notes, " +
                    "       hs.service_code, hs.service_name, hs.description AS service_desc, hs.unit_name, hs.unit_price, hs.is_active " +
                    "FROM dbo.service_requests sr " +
                    "JOIN dbo.hotel_services hs ON sr.hotel_service_id = hs.hotel_service_id ";

    // Create new service request from guest
    @Override
    public void addServiceRequest(ServiceRequest request) throws Exception {
        String sql = "INSERT INTO dbo.service_requests " +
                "(reservation_id, customer_id, hotel_service_id, quantity, unit_price_snapshot, total_amount, status_code, requested_at, notes) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, SYSUTCDATETIME(), ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, request.getReservationId());
            stmt.setLong(2, request.getCustomerId());
            stmt.setLong(3, request.getHotelServiceId());
            stmt.setBigDecimal(4, request.getQuantity());
//            stmt.setBigDecimal(5, request.getUnitPriceSnapshot() != null ? request.getUnitPriceSnapshot() : BigDecimal.ZERO);
            stmt.setBigDecimal(6, request.getTotalAmount());
            stmt.setString(7, request.getStatusCode() != null ? request.getStatusCode() : "PENDING");
            stmt.setString(8, request.getNotes());

            stmt.executeUpdate();
        }
    }

    // Get service request by ID
    @Override
    public ServiceRequest getServiceRequestById(long serviceRequestId) throws Exception {
        String sql = SELECT_JOIN_SQL + "WHERE sr.service_request_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, serviceRequestId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToServiceRequest(rs);
                }
            }
        }
        return null;
    }

    // Get all pending requests for Staff/Admin board
    @Override
    public List<ServiceRequest> getPendingRequests() throws Exception {
        String sql = SELECT_JOIN_SQL +
                "WHERE sr.status_code = 'PENDING' " +
                "ORDER BY sr.requested_at ASC";
        List<ServiceRequest> requests = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                requests.add(mapRowToServiceRequest(rs));
            }
        }
        return requests;
    }

    // Get service requests assigned to a specific staff user
    @Override
    public List<ServiceRequest> getRequestsByStaffId(long staffUserId) throws Exception {
        String sql = SELECT_JOIN_SQL +
                "WHERE sr.assigned_staff_user_id = ? " +
                "ORDER BY sr.requested_at DESC";
        List<ServiceRequest> requests = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, staffUserId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapRowToServiceRequest(rs));
                }
            }
        }
        return requests;
    }

    // Get all service requests for a specific reservation
    @Override
    public List<ServiceRequest> getServiceRequestsByReservation(long reservationId) throws Exception {
        String sql = SELECT_JOIN_SQL +
                "WHERE sr.reservation_id = ? " +
                "ORDER BY sr.requested_at DESC";
        List<ServiceRequest> requests = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, reservationId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapRowToServiceRequest(rs));
                }
            }
        }
        return requests;
    }

    // Get COMPLETED requests for a reservation (for checkout billing)
    @Override
    public List<ServiceRequest> getCompletedRequestsByReservationId(long reservationId) throws Exception {
        String sql = SELECT_JOIN_SQL +
                "WHERE sr.reservation_id = ? AND sr.status_code = 'COMPLETED' " +
                "ORDER BY sr.completed_at ASC";
        List<ServiceRequest> requests = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, reservationId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapRowToServiceRequest(rs));
                }
            }
        }
        return requests;
    }

    // Assign a staff member to handle the request
    @Override
    public void assignStaff(long serviceRequestId, long staffUserId) throws Exception {
        String sql = "UPDATE dbo.service_requests " +
                "SET assigned_staff_user_id = ?, status_code = 'ASSIGNED', assigned_at = SYSUTCDATETIME() " +
                "WHERE service_request_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, staffUserId);
            stmt.setLong(2, serviceRequestId);

            stmt.executeUpdate();
        }
    }

    // Update status code (with conditional timestamp updates)
    @Override
    public void updateStatus(long serviceRequestId, String statusCode) throws Exception {
        String sql;
        if ("IN_PROGRESS".equalsIgnoreCase(statusCode)) {
            sql = "UPDATE dbo.service_requests SET status_code = ?, started_at = SYSUTCDATETIME() WHERE service_request_id = ?";
        } else if ("COMPLETED".equalsIgnoreCase(statusCode)) {
            sql = "UPDATE dbo.service_requests SET status_code = ?, completed_at = SYSUTCDATETIME() WHERE service_request_id = ?";
        } else {
            sql = "UPDATE dbo.service_requests SET status_code = ? WHERE service_request_id = ?";
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, statusCode);
            stmt.setLong(2, serviceRequestId);

            stmt.executeUpdate();
        }
    }

    // Cancel service request with reason/notes
    @Override
    public void cancelRequest(long serviceRequestId, String reason) throws Exception {
        String sql = "UPDATE dbo.service_requests " +
                "SET status_code = 'CANCELLED', notes = ISNULL(notes, '') + ? " +
                "WHERE service_request_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, reason != null ? " [Lý do hủy: " + reason + "]" : " [Đã hủy]");
            stmt.setLong(2, serviceRequestId);

            stmt.executeUpdate();
        }
    }

    // Helper method to map ResultSet row to ServiceRequest entity
    private ServiceRequest mapRowToServiceRequest(ResultSet rs) throws SQLException {
        ServiceRequest request = new ServiceRequest();

        request.setServiceRequestId(rs.getLong("service_request_id"));
        request.setReservationId(rs.getLong("reservation_id"));
        request.setCustomerId(rs.getLong("customer_id"));
        request.setHotelServiceId(rs.getLong("hotel_service_id"));

        // Handle Nullable assigned_staff_user_id
        long staffId = rs.getLong("assigned_staff_user_id");
        if (!rs.wasNull()) {
            request.setAssignedStaffUserId(staffId);
        } else {
//            request.setAssignedStaffUserId(null);
        }

        request.setQuantity(rs.getBigDecimal("quantity"));
//        request.setUnitPriceSnapshot(rs.getBigDecimal("unit_price_snapshot"));
        request.setTotalAmount(rs.getBigDecimal("total_amount"));
        request.setStatusCode(rs.getString("status_code"));
        request.setNotes(rs.getString("notes"));

        // Timestamps mapping
        Timestamp reqAt = rs.getTimestamp("requested_at");
        if (reqAt != null) request.setRequestedAt(reqAt.toLocalDateTime());

//        Timestamp assAt = rs.getTimestamp("assigned_at");
//        if (assAt != null) request.setAssignedAt(assAt.toLocalDateTime());
//
//        Timestamp strAt = rs.getTimestamp("started_at");
//        if (strAt != null) request.setStartedAt(strAt.toLocalDateTime());

        Timestamp cmpAt = rs.getTimestamp("completed_at");
        if (cmpAt != null) request.setCompletedAt(cmpAt.toLocalDateTime());

        // Embedded HotelService object mapping
        HotelService service = new HotelService();
        service.setHotelServiceId(rs.getLong("hotel_service_id"));
        service.setServiceCode(rs.getString("service_code"));
        service.setServiceName(rs.getString("service_name"));
        service.setDescription(rs.getString("service_desc"));
        service.setUnitName(rs.getString("unit_name"));
        service.setUnitPrice(rs.getBigDecimal("unit_price"));
        service.setActive(rs.getBoolean("is_active"));

        request.setService(service);

        return request;
    }
}