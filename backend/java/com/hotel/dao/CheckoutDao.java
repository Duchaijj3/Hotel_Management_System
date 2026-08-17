package com.hotel.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface CheckoutDao {
    // 1. Hủy các dịch vụ chưa làm
    int cancelPendingServices(Connection c, long reservationId) throws SQLException;

    // 2. Lấy danh sách ID các dịch vụ đã/đang làm nhưng chưa đưa vào hóa đơn
    List<Long> getUnbilledServices(Connection c, long reservationId) throws SQLException;

    // 3. Đưa dịch vụ vào hóa đơn (invoice_items)
    void addServiceToInvoice(Connection c, long invoiceId, long serviceRequestId, long userId) throws SQLException;

    // 4. Cập nhật trạng thái Booking -> CHECKED_OUT
    int checkoutReservation(Connection c, long reservationId, long userId) throws SQLException;

    // 5. Lấy danh sách phòng đang được gán cho Booking này
    List<Long> getAssignedRooms(Connection c, long reservationId) throws SQLException;

    // 6. Giải phóng phòng và đổi trạng thái thành DIRTY
    void releaseAndDirtyRoom(Connection c, long roomId) throws SQLException;

    // 7. Tạo task dọn phòng (CHECKOUT_CLEANING)
    void createCheckoutCleaningTask(Connection c, long roomId, long reservationId) throws SQLException;
}