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

    public ServiceRequestServiceImpl(
            ServiceRequestDao dao,
            ConnectionProvider connections
    ) {
        this.dao = dao;
        this.connections = connections;
    }

    @Override
    public List<ServiceRequestDto> getPendingRequests() {
        try (Connection connection = connections.getConnection()) {
            return dao.findPending(connection);
        } catch (SQLException exception) {
            throw data("Không thể tải danh sách yêu cầu dịch vụ chờ nhận.", exception);
        }
    }

    @Override
    public List<ServiceRequestDto> getMyRequests(long staffId) {
        try (Connection connection = connections.getConnection()) {
            return dao.findAssignedTo(connection, staffId);
        } catch (SQLException exception) {
            throw data("Không thể tải danh sách yêu cầu dịch vụ của bạn.", exception);
        }
    }

    @Override
    public void acceptRequest(long requestId, long staffId)
            throws BusinessException {
        tx(connection -> {
            ServiceRequestDto request = findForUpdate(connection, requestId);

            if (!"PENDING".equals(request.status())) {
                throw new BusinessException(
                        "Chỉ có thể nhận yêu cầu đang chờ tiếp nhận."
                );
            }

            ensureUpdated(dao.updateStatusAndStaff(
                    connection,
                    requestId,
                    "ASSIGNED",
                    staffId
            ));

            return null;
        });
    }

    @Override
    public void startRequest(long requestId, long staffId)
            throws BusinessException {
        tx(connection -> {
            ServiceRequestDto request = findForUpdate(connection, requestId);
            validateOwnership(request, staffId);

            if (!"ASSIGNED".equals(request.status())) {
                throw new BusinessException(
                        "Chỉ có thể bắt đầu yêu cầu đã được nhận."
                );
            }

            ensureUpdated(dao.updateStatusAndStaff(
                    connection,
                    requestId,
                    "IN_PROGRESS",
                    staffId
            ));

            return null;
        });
    }

    @Override
    public void completeRequest(long requestId, long staffId)
            throws BusinessException {
        tx(connection -> {
            ServiceRequestDto request = findForUpdate(connection, requestId);
            validateOwnership(request, staffId);

            if (!"IN_PROGRESS".equals(request.status())) {
                throw new BusinessException(
                        "Chỉ có thể hoàn thành yêu cầu đang thực hiện."
                );
            }

            ensureUpdated(dao.updateStatusAndStaff(
                    connection,
                    requestId,
                    "COMPLETED",
                    staffId
            ));

            return null;
        });
    }

    @Override
    public void cancelRequest(
            long requestId,
            long staffId,
            String cancellationReason
    ) throws BusinessException {
        if (cancellationReason == null || cancellationReason.isBlank()) {
            throw new BusinessException("Lý do hủy là bắt buộc.");
        }

        tx(connection -> {
            ServiceRequestDto request = findForUpdate(connection, requestId);

            if ("COMPLETED".equals(request.status())) {
                throw new BusinessException(
                        "Không thể hủy yêu cầu đã hoàn thành."
                );
            }

            if ("CANCELLED".equals(request.status())) {
                throw new BusinessException("Yêu cầu này đã được hủy.");
            }

            if (request.assignedStaffId() != null
                    && request.assignedStaffId() != staffId) {
                throw new BusinessException(
                        "Bạn không thể hủy yêu cầu do nhân viên khác xử lý."
                );
            }

            ensureUpdated(dao.cancelRequest(
                    connection,
                    requestId,
                    cancellationReason.trim()
            ));

            return null;
        });
    }

    private ServiceRequestDto findForUpdate(
            Connection connection,
            long requestId
    ) throws SQLException, BusinessException {
        return dao.findById(connection, requestId, true)
                .orElseThrow(() -> new BusinessException(
                        "Yêu cầu dịch vụ không tồn tại."
                ));
    }

    private void validateOwnership(ServiceRequestDto request, long staffId)
            throws BusinessException {
        if (request.assignedStaffId() == null
                || request.assignedStaffId() != staffId) {
            throw new BusinessException(
                    "Bạn không có quyền xử lý yêu cầu của nhân viên khác."
            );
        }
    }

    private void ensureUpdated(int affectedRows) throws BusinessException {
        if (affectedRows != 1) {
            throw new BusinessException(
                    "Không thể cập nhật yêu cầu dịch vụ. Vui lòng thử lại."
            );
        }
    }

    private DataAccessException data(String message, SQLException exception) {
        return new DataAccessException(message, exception);
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
                throw data("Lỗi giao dịch cơ sở dữ liệu.", exception);
            } finally {
                try {
                    connection.setTransactionIsolation(isolation);
                    connection.setAutoCommit(autoCommit);
                } catch (SQLException ignored) {
                    // Connection sẽ được đóng bởi try-with-resources.
                }
            }
        } catch (SQLException exception) {
            throw data("Không thể kết nối cơ sở dữ liệu.", exception);
        }
    }

    private void rollback(Connection connection) {
        try {
            connection.rollback();
        } catch (SQLException ignored) {
            // Giữ exception gốc.
        }
    }

    @FunctionalInterface
    private interface Work<T> {
        T run(Connection connection) throws SQLException, BusinessException;
    }
}