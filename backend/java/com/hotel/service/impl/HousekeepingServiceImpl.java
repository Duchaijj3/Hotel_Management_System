package com.hotel.service.impl;

import com.hotel.dao.HousekeepingTaskDao;
import com.hotel.dto.HousekeepingTaskDto;
import com.hotel.exception.BusinessException;
import com.hotel.exception.DataAccessException;
import com.hotel.service.HousekeepingService;
import com.hotel.util.ConnectionProvider;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class HousekeepingServiceImpl implements HousekeepingService {

    private final HousekeepingTaskDao dao;
    private final ConnectionProvider connections;

    public HousekeepingServiceImpl(HousekeepingTaskDao dao, ConnectionProvider connections) {
        this.dao = dao;
        this.connections = connections;
    }

    @Override
    public List<HousekeepingTaskDto> getPendingTasks() {
        try (Connection c = connections.getConnection()) {
            return dao.findPending(c);
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi tải danh sách phòng cần dọn.", e);
        }
    }

    @Override
    public List<HousekeepingTaskDto> getMyTasks(long staffId) {
        try (Connection c = connections.getConnection()) {
            return dao.findAssignedTo(c, staffId);
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi tải công việc của bạn.", e);
        }
    }

    @Override
    public void acceptTask(long taskId, long staffId) throws BusinessException {
        tx(c -> {
            HousekeepingTaskDto task = dao.findById(c, taskId, true)
                    .orElseThrow(() -> new BusinessException("Công việc không tồn tại."));
            if (!"PENDING".equals(task.statusCode())) {
                throw new BusinessException("Phòng này đã có người nhận hoặc đã dọn xong.");
            }
            dao.updateTaskStatus(c, taskId, "ASSIGNED", staffId, null, null);
            return null;
        });
    }

    @Override
    public void startTask(long taskId, long staffId) throws BusinessException {
        tx(c -> {
            HousekeepingTaskDto task = dao.findById(c, taskId, true)
                    .orElseThrow(() -> new BusinessException("Công việc không tồn tại."));

            validateOwnership(task, staffId);
            if (!"ASSIGNED".equals(task.statusCode())) {
                throw new BusinessException("Chỉ có thể bắt đầu công việc đang ở trạng thái đã nhận (ASSIGNED).");
            }

            // 1. Cập nhật Task -> IN_PROGRESS, ghi nhận giờ bắt đầu
            dao.updateTaskStatus(c, taskId, "IN_PROGRESS", staffId, LocalDateTime.now(), null);
            // 2. Cập nhật Phòng -> CLEANING (Đang dọn)
            dao.updateRoomCleaningStatus(c, task.roomId(), "CLEANING");

            return null;
        });
    }

    @Override
    public void completeTask(long taskId, long staffId) throws BusinessException {
        tx(c -> {
            HousekeepingTaskDto task = dao.findById(c, taskId, true)
                    .orElseThrow(() -> new BusinessException("Công việc không tồn tại."));

            validateOwnership(task, staffId);
            if (!"IN_PROGRESS".equals(task.statusCode())) {
                throw new BusinessException("Chỉ có thể hoàn thành công việc đang thực hiện (IN_PROGRESS).");
            }

            // 1. Cập nhật Task -> COMPLETED, ghi nhận giờ kết thúc
            dao.updateTaskStatus(c, taskId, "COMPLETED", staffId, null, LocalDateTime.now());
            // 2. Cập nhật Phòng -> CLEAN (Đã sạch, sẵn sàng đón khách)
            dao.updateRoomCleaningStatus(c, task.roomId(), "CLEAN");

            return null;
        });
    }

    private void validateOwnership(HousekeepingTaskDto task, long staffId) throws BusinessException {
        if (task.assignedStaffId() == null || task.assignedStaffId() != staffId) {
            throw new BusinessException("Bạn không có quyền thao tác trên công việc của người khác.");
        }
    }

    // Tái sử dụng lại helper tx(...) quản lý Transaction giống như ServiceRequestServiceImpl
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
                try { c.rollback(); } catch (SQLException ignored) {} throw e;
            } catch (SQLException e) {
                try { c.rollback(); } catch (SQLException ignored) {} throw new DataAccessException("Lỗi giao dịch CSDL", e);
            } finally {
                try { c.setTransactionIsolation(i); c.setAutoCommit(a); } catch (SQLException ignored) {}
            }
        } catch (SQLException e) {
            throw new DataAccessException("Không thể kết nối CSDL", e);
        }
    }

    @FunctionalInterface
    private interface Work<T> { T run(Connection c) throws SQLException, BusinessException; }
}