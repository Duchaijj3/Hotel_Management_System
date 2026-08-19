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

    public HousekeepingServiceImpl(
            HousekeepingTaskDao dao,
            ConnectionProvider connections
    ) {
        this.dao = dao;
        this.connections = connections;
    }

    @Override
    public List<HousekeepingTaskDto> getPendingTasks() {
        try (Connection connection = connections.getConnection()) {
            return dao.findPending(connection);
        } catch (SQLException exception) {
            throw new DataAccessException(
                    "Không thể tải danh sách phòng cần dọn.",
                    exception
            );
        }
    }

    @Override
    public List<HousekeepingTaskDto> getMyTasks(long staffId) {
        try (Connection connection = connections.getConnection()) {
            return dao.findAssignedTo(connection, staffId);
        } catch (SQLException exception) {
            throw new DataAccessException(
                    "Không thể tải danh sách công việc dọn phòng của bạn.",
                    exception
            );
        }
    }

    @Override
    public void acceptTask(long taskId, long staffId)
            throws BusinessException {
        tx(connection -> {
            HousekeepingTaskDto task = findForUpdate(connection, taskId);

            if (!"PENDING".equals(task.statusCode())) {
                throw new BusinessException(
                        "Công việc này đã được nhận hoặc đã hoàn thành."
                );
            }

            if (!"DIRTY".equals(task.cleaningStatus())) {
                throw new BusinessException(
                        "Phòng không ở trạng thái DIRTY nên không thể nhận việc dọn phòng."
                );
            }

            int updatedTask = dao.updateTaskStatus(
                    connection,
                    taskId,
                    "IN_PROGRESS",
                    staffId,
                    LocalDateTime.now(),
                    null
            );

            ensureUpdated(
                    updatedTask,
                    "Không thể tiếp nhận công việc dọn phòng."
            );

            int updatedRoom = dao.updateRoomCleaningStatus(
                    connection,
                    task.roomId(),
                    "CLEANING"
            );

            ensureUpdated(
                    updatedRoom,
                    "Không thể cập nhật trạng thái phòng."
            );

            return null;
        });
    }

    @Override
    public void completeTask(long taskId, long staffId)
            throws BusinessException {
        tx(connection -> {
            HousekeepingTaskDto task = findForUpdate(connection, taskId);

            validateOwnership(task, staffId);

            if (!"IN_PROGRESS".equals(task.statusCode())) {
                throw new BusinessException(
                        "Chỉ có thể hoàn thành công việc đang thực hiện."
                );
            }

            int updatedTask = dao.updateTaskStatus(
                    connection,
                    taskId,
                    "COMPLETED",
                    staffId,
                    null,
                    LocalDateTime.now()
            );

            ensureUpdated(
                    updatedTask,
                    "Không thể hoàn thành công việc dọn phòng."
            );

            int updatedRoom = dao.updateRoomCleaningStatus(
                    connection,
                    task.roomId(),
                    "CLEAN"
            );

            ensureUpdated(
                    updatedRoom,
                    "Không thể cập nhật trạng thái sạch của phòng."
            );

            return null;
        });
    }

    private HousekeepingTaskDto findForUpdate(
            Connection connection,
            long taskId
    ) throws SQLException, BusinessException {
        return dao.findById(connection, taskId, true)
                .orElseThrow(() -> new BusinessException(
                        "Công việc dọn phòng không tồn tại."
                ));
    }

    private void validateOwnership(
            HousekeepingTaskDto task,
            long staffId
    ) throws BusinessException {
        if (task.assignedStaffId() == null
                || task.assignedStaffId() != staffId) {
            throw new BusinessException(
                    "Bạn không có quyền thao tác trên công việc của nhân viên khác."
            );
        }
    }

    private void ensureUpdated(int affectedRows, String message)
            throws BusinessException {
        if (affectedRows != 1) {
            throw new BusinessException(message);
        }
    }

    private <T> T tx(Work<T> work) throws BusinessException {
        try (Connection connection = connections.getConnection()) {
            boolean autoCommit = connection.getAutoCommit();
            int isolation = connection.getTransactionIsolation();

            try {
                connection.setAutoCommit(false);
                connection.setTransactionIsolation(
                        Connection.TRANSACTION_SERIALIZABLE
                );

                T result = work.run(connection);
                connection.commit();
                return result;
            } catch (BusinessException exception) {
                rollback(connection);
                throw exception;
            } catch (SQLException exception) {
                rollback(connection);
                throw new DataAccessException(
                        "Lỗi giao dịch cơ sở dữ liệu.",
                        exception
                );
            } finally {
                try {
                    connection.setTransactionIsolation(isolation);
                    connection.setAutoCommit(autoCommit);
                } catch (SQLException ignored) {
                    // Connection sẽ được đóng bởi try-with-resources.
                }
            }
        } catch (SQLException exception) {
            throw new DataAccessException(
                    "Không thể kết nối cơ sở dữ liệu.",
                    exception
            );
        }
    }

    private void rollback(Connection connection) {
        try {
            connection.rollback();
        } catch (SQLException ignored) {
            // Giữ lỗi gốc.
        }
    }

    @FunctionalInterface
    private interface Work<T> {
        T run(Connection connection)
                throws SQLException, BusinessException;
    }
}