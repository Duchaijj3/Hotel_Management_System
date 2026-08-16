package com.hotel.service.impl;

import com.hotel.dao.ServiceRequestDao;
import com.hotel.dto.ServiceRequestDto;
import com.hotel.exception.BusinessException;
import com.hotel.exception.DataAccessException;
import com.hotel.service.ServiceRequestService;
import com.hotel.util.ConnectionProvider;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ServiceRequestServiceImpl implements ServiceRequestService {

    private final ServiceRequestDao dao;
    private final ConnectionProvider connections;

    // Dependency Injection hoàn chỉnh, không có "new" bậy bạ
    public ServiceRequestServiceImpl(ServiceRequestDao dao, ConnectionProvider connections) {
        this.dao = dao;
        this.connections = connections;
    }

    @Override
    public List<ServiceRequestDto> getPendingRequests() {
        try (Connection c = connections.getConnection()) {
            return dao.findPending(c);
        } catch (SQLException e) {
            throw data("Unable to fetch pending requests", e);
        }
    }

    @Override
    public List<ServiceRequestDto> getMyTasks(long staffId) {
        try (Connection c = connections.getConnection()) {
            return dao.findAssignedTo(c, staffId);
        } catch (SQLException e) {
            throw data("Unable to fetch my tasks", e);
        }
    }

    @Override
    public void acceptTask(long requestId, long staffId) throws BusinessException {
        tx(c -> {
            ServiceRequestDto req = dao.findById(c, requestId, true)
                    .orElseThrow(() -> new BusinessException("Yêu cầu không tồn tại hoặc đã bị xóa."));

            if (!"PENDING".equals(req.status())) {
                throw new BusinessException("Chỉ có thể nhận yêu cầu đang ở trạng thái chờ (PENDING).");
            }
            dao.updateStatusAndStaff(c, requestId, "ASSIGNED", staffId);
            return null;
        });
    }

    @Override
    public void startTask(long requestId, long staffId) throws BusinessException {
        tx(c -> {
            ServiceRequestDto req = dao.findById(c, requestId, true)
                    .orElseThrow(() -> new BusinessException("Yêu cầu không tồn tại."));

            validateOwnership(req, staffId);
            if (!"ASSIGNED".equals(req.status())) {
                throw new BusinessException("Chỉ có thể bắt đầu yêu cầu đã được giao (ASSIGNED).");
            }
            dao.updateStatusAndStaff(c, requestId, "IN_PROGRESS", staffId);
            return null;
        });
    }

    @Override
    public void completeTask(long requestId, long staffId) throws BusinessException {
        tx(c -> {
            ServiceRequestDto req = dao.findById(c, requestId, true)
                    .orElseThrow(() -> new BusinessException("Yêu cầu không tồn tại."));

            validateOwnership(req, staffId);
            if (!"IN_PROGRESS".equals(req.status())) {
                throw new BusinessException("Chỉ có thể hoàn thành yêu cầu đang thực hiện (IN_PROGRESS).");
            }
            dao.updateStatusAndStaff(c, requestId, "COMPLETED", staffId);
            return null;
        });
    }
    @Override
    public void cancelTask(long requestId, long staffId, String reason) throws BusinessException {
        // 1. Validate dữ liệu đầu vào
        if (reason == null || reason.trim().isEmpty()) {
            throw new BusinessException("Lý do hủy là bắt buộc. Vui lòng nhập lý do.");
        }

        tx(c -> {
            // 2. Lấy dữ liệu kèm Lock (Chống tranh chấp)
            ServiceRequestDto req = dao.findById(c, requestId, true)
                    .orElseThrow(() -> new BusinessException("Yêu cầu không tồn tại."));

            // 3. Kiểm tra các điều kiện nghiệp vụ cốt lõi
            if ("COMPLETED".equals(req.status())) {
                throw new BusinessException("Không thể hủy dịch vụ do đã hoàn thành.");
            }
            if ("CANCELLED".equals(req.status())) {
                throw new BusinessException("Yêu cầu này đã bị hủy trước đó.");
            }

            // 4. Kiểm tra quyền sở hữu (Nếu task đã có người nhận, chỉ người đó mới được hủy)
            if (req.assignedStaffId() != null && req.assignedStaffId() != staffId) {
                throw new BusinessException("Bạn không thể hủy yêu cầu đang được thực hiện bởi nhân viên khác.");
            }

            // 5. Cập nhật DB
            dao.cancelRequest(c, requestId, reason.trim());
            return null;
        });
    }

    private void validateOwnership(ServiceRequestDto req, long staffId) throws BusinessException {
        if (req.assignedStaffId() == null || req.assignedStaffId() != staffId) {
            throw new BusinessException("Bạn không có quyền xử lý yêu cầu của người khác.");
        }
    }

    private DataAccessException data(String m, SQLException e) {
        return new DataAccessException(m, e);
    }

    // Helper quản lý Transaction và xử lý Deadlock
    private <T> T tx(Work<T> w) throws BusinessException {
        try (Connection c = connections.getConnection()) {
            boolean a = c.getAutoCommit();
            int i = c.getTransactionIsolation();
            try {
                c.setAutoCommit(false);
                c.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                T x = w.run(c);
                c.commit();
                return x;
            } catch (BusinessException e) {
                rollback(c); throw e;
            } catch (SQLException e) {
                rollback(c); throw data("Transaction failed", e);
            } finally {
                try { c.setTransactionIsolation(i); c.setAutoCommit(a); } catch (SQLException ignored) {}
            }
        } catch (SQLException e) {
            throw data("Unable to connect to database", e);
        }
    }

    private void rollback(Connection c) { try { c.rollback(); } catch (SQLException ignored) {} }

    @FunctionalInterface
    private interface Work<T> { T run(Connection c) throws SQLException, BusinessException; }
}