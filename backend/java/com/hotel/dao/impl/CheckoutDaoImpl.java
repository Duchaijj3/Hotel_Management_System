package com.hotel.dao.impl;

import com.hotel.dao.CheckoutDao;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CheckoutDaoImpl implements CheckoutDao {

    @Override
    public int cancelPendingServices(Connection c, long reservationId) throws SQLException {
        // Chuyển PENDING thành CANCELLED[cite: 39]
        String sql = "UPDATE service_requests SET status_code = 'CANCELLED' WHERE reservation_id = ? AND status_code = 'PENDING'";
        try(PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, reservationId);
            return p.executeUpdate();
        }
    }

    @Override
    public List<Long> getUnbilledServices(Connection c, long reservationId) throws SQLException {
        // Tìm các request IN_PROGRESS hoặc COMPLETED chưa có trong invoice_items[cite: 39]
        String sql = "SELECT sr.service_request_id FROM service_requests sr " +
                "WHERE sr.reservation_id = ? AND sr.status_code IN ('IN_PROGRESS', 'COMPLETED') " +
                "AND NOT EXISTS (SELECT 1 FROM invoice_items ii WHERE ii.service_request_id = sr.service_request_id)";
        List<Long> ids = new ArrayList<>();
        try(PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, reservationId);
            try(ResultSet rs = p.executeQuery()) {
                while(rs.next()) ids.add(rs.getLong(1));
            }
        }
        return ids;
    }

    @Override
    public void addServiceToInvoice(Connection c, long invoiceId, long serviceRequestId, long userId) throws SQLException {
        // Đẩy dịch vụ vào chi tiết hóa đơn[cite: 39]
        String sql = "INSERT INTO invoice_items (invoice_id, service_request_id, posted_by_user_id, item_type, description, quantity, unit_price, amount) " +
                "SELECT ?, sr.service_request_id, ?, 'SERVICE', hs.service_name, sr.quantity, sr.unit_price_snapshot, sr.total_amount " +
                "FROM service_requests sr JOIN hotel_services hs ON sr.hotel_service_id = hs.hotel_service_id " +
                "WHERE sr.service_request_id = ?";
        try(PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, invoiceId);
            p.setLong(2, userId);
            p.setLong(3, serviceRequestId);
            p.executeUpdate();
        }
    }

    @Override
    public int checkoutReservation(Connection c, long reservationId, long userId) throws SQLException {
        // Cập nhật reservation[cite: 39]
        String sql = "UPDATE reservations SET status_code = 'CHECKED_OUT', checked_out_by_user_id = ?, actual_check_out_at = SYSUTCDATETIME(), updated_at = SYSUTCDATETIME() WHERE reservation_id = ?";
        try(PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, userId);
            p.setLong(2, reservationId);
            return p.executeUpdate();
        }
    }

    @Override
    public List<Long> getAssignedRooms(Connection c, long reservationId) throws SQLException {
        String sql = "SELECT ra.room_id FROM room_assignments ra " +
                "JOIN reservation_rooms rr ON ra.reservation_room_id = rr.reservation_room_id " +
                "WHERE rr.reservation_id = ? AND ra.is_current = 1";
        List<Long> ids = new ArrayList<>();
        try(PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, reservationId);
            try(ResultSet rs = p.executeQuery()) {
                while(rs.next()) ids.add(rs.getLong(1));
            }
        }
        return ids;
    }

    @Override
    public void releaseAndDirtyRoom(Connection c, long roomId) throws SQLException {
        // 1. Bỏ gán phòng hiện tại[cite: 39]
        try(PreparedStatement p1 = c.prepareStatement("UPDATE room_assignments SET is_current = 0, unassigned_at = SYSUTCDATETIME() WHERE room_id = ? AND is_current = 1")) {
            p1.setLong(1, roomId);
            p1.executeUpdate();
        }
        // 2. Chuyển trạng thái phòng sang DIRTY[cite: 39]
        try(PreparedStatement p2 = c.prepareStatement("UPDATE rooms SET cleaning_status = 'DIRTY', updated_at = SYSUTCDATETIME() WHERE room_id = ?")) {
            p2.setLong(1, roomId);
            p2.executeUpdate();
        }
    }

    @Override
    public void createCheckoutCleaningTask(Connection c, long roomId, long reservationId) throws SQLException {
        // Sinh task dọn phòng[cite: 39]
        String sql = "INSERT INTO housekeeping_tasks (room_id, reservation_id, task_type, priority_code, status_code) VALUES (?, ?, 'CHECKOUT_CLEANING', 'HIGH', 'PENDING')";
        try(PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, roomId);
            p.setLong(2, reservationId);
            p.executeUpdate();
        }
    }
}