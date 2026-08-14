package com.hotel.service.impl;


import com.hotel.dao.HotelServiceDao;
import com.hotel.dao.ServiceRequestDao;
import com.hotel.dao.impl.HotelServiceDaoImpl;
import com.hotel.dao.impl.ServiceRequestDaoImpl;
import com.hotel.model.HotelService;
import com.hotel.model.ServiceRequest;
import com.hotel.service.ServiceRequestService;

import java.math.BigDecimal;
import java.util.List;

/**
 * Implementation of ServiceRequestService
 * Handles business logic for processing guest service requests and staff workflow
 */


import java.time.LocalDateTime;


/**
 * Implementation of ServiceRequestService
 * Handles business logic for processing guest service requests and staff workflow
 */
public class ServiceRequestServiceImpl implements ServiceRequestService {

    private final ServiceRequestDao serviceRequestDao;
    private final HotelServiceDao hotelServiceDao;

    // Default constructor using JDBC implementations
    public ServiceRequestServiceImpl() {
        this.serviceRequestDao = new ServiceRequestDaoImpl();
        this.hotelServiceDao = new HotelServiceDaoImpl();
    }

    // Constructor injection for testing/flexibility
    public ServiceRequestServiceImpl(ServiceRequestDao serviceRequestDao, HotelServiceDao hotelServiceDao) {
        this.serviceRequestDao = serviceRequestDao;
        this.hotelServiceDao = hotelServiceDao;
    }

    // Guest requests service
    @Override
    public void requestService(long reservationId, long customerId, long hotelServiceId, BigDecimal quantity) throws Exception {
        // 1. Validation
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Số lượng dịch vụ phải lớn hơn 0");
        }

        // 2. Fetch target service to check price and active status
        HotelService service = hotelServiceDao.getServiceById(hotelServiceId);
        if (service == null || !service.isActive()) {
            throw new IllegalArgumentException("Dịch vụ không tồn tại hoặc hiện tại ngừng cung cấp");
        }

        // 3. Calculate total amount = unit_price * quantity
        BigDecimal totalAmount = service.getUnitPrice().multiply(quantity);

        // 4. Create new ServiceRequest entity
        ServiceRequest request = new ServiceRequest();
        request.setReservationId(reservationId);
        request.setCustomerId(customerId);
        request.setHotelServiceId(hotelServiceId);
        request.setQuantity(quantity);
        request.setUnitPrice(service.getUnitPrice()); // Sẽ lưu vào unitPriceSnapshot nhờ helper method
        request.setTotalAmount(totalAmount);
        request.setStatus("PENDING");                 // Sẽ lưu vào statusCode
        request.setRequestedAt(LocalDateTime.now());
        request.setCreatedAt(LocalDateTime.now());

        serviceRequestDao.addServiceRequest(request);
    }

    // Staff management: Get all requests waiting to be assigned or processed
    @Override
    public List<ServiceRequest> getPendingRequests() throws Exception {
        return serviceRequestDao.getPendingRequests();
    }

    // Staff management: Get requests assigned to a specific staff member
    @Override
    public List<ServiceRequest> getAssignedToMe(long staffUserId) throws Exception {
        if (staffUserId <= 0) {
            throw new IllegalArgumentException("Mã nhân viên không hợp lệ");
        }
        return serviceRequestDao.getRequestsByStaffId(staffUserId);
    }

    // Staff management: Assign request to staff member
    @Override
    public void assignServiceToStaff(long serviceRequestId, long staffUserId) throws Exception {
        ServiceRequest request = serviceRequestDao.getServiceRequestById(serviceRequestId);
        if (request == null) {
            throw new IllegalArgumentException("Không tìm thấy yêu cầu dịch vụ với ID: " + serviceRequestId);
        }

        serviceRequestDao.assignStaff(serviceRequestId, staffUserId);
    }

    // Staff management: Start executing the service (PENDING/ASSIGNED -> IN_PROGRESS)
    @Override
    public void startService(long serviceRequestId) throws Exception {
        ServiceRequest request = serviceRequestDao.getServiceRequestById(serviceRequestId);
        if (request == null) {
            throw new IllegalArgumentException("Không tìm thấy yêu cầu dịch vụ với ID: " + serviceRequestId);
        }

        // Status check: Only PENDING or ASSIGNED requests can be started
        String currentStatus = request.getStatus();
        if (!"PENDING".equalsIgnoreCase(currentStatus) && !"ASSIGNED".equalsIgnoreCase(currentStatus)) {
            throw new IllegalArgumentException("Chỉ có thể bắt đầu xử lý yêu cầu đang ở trạng thái 'PENDING' hoặc 'ASSIGNED'");
        }

        serviceRequestDao.updateStatus(serviceRequestId, "IN_PROGRESS");
    }

    // Staff management: Complete the service (IN_PROGRESS -> COMPLETED)
    @Override
    public void completeService(long serviceRequestId) throws Exception {
        ServiceRequest request = serviceRequestDao.getServiceRequestById(serviceRequestId);
        if (request == null) {
            throw new IllegalArgumentException("Không tìm thấy yêu cầu dịch vụ với ID: " + serviceRequestId);
        }

        // Status check: Only IN_PROGRESS requests can be completed
        if (!"IN_PROGRESS".equalsIgnoreCase(request.getStatus())) {
            throw new IllegalArgumentException("Chỉ có thể hoàn thành yêu cầu đang ở trạng thái 'IN_PROGRESS'");
        }

        serviceRequestDao.updateStatus(serviceRequestId, "COMPLETED");
    }

    // Staff management: Cancel request with a reason
    @Override
    public void cancelService(long serviceRequestId, String reason) throws Exception {
        ServiceRequest request = serviceRequestDao.getServiceRequestById(serviceRequestId);
        if (request == null) {
            throw new IllegalArgumentException("Không tìm thấy yêu cầu dịch vụ với ID: " + serviceRequestId);
        }

        if ("COMPLETED".equalsIgnoreCase(request.getStatus())) {
            throw new IllegalArgumentException("Không thể hủy dịch vụ đã hoàn thành");
        }

        serviceRequestDao.cancelRequest(serviceRequestId, reason);
    }

    // Billing integration: Fetch completed services for checkout billing
    @Override
    public List<ServiceRequest> getCompletedServicesForReservation(long reservationId) throws Exception {
        if (reservationId <= 0) {
            throw new IllegalArgumentException("Mã đặt phòng không hợp lệ");
        }
        return serviceRequestDao.getCompletedRequestsByReservationId(reservationId);
    }

    // Billing integration: Sum total costs of all completed services for checkout invoice
    @Override
    public BigDecimal calculateServiceChargesForReservation(long reservationId) throws Exception {
        List<ServiceRequest> completedRequests = getCompletedServicesForReservation(reservationId);
        BigDecimal totalCharges = BigDecimal.ZERO;

        for (ServiceRequest request : completedRequests) {
            if (request.getTotalAmount() != null) {
                totalCharges = totalCharges.add(request.getTotalAmount());
            }
        }

        return totalCharges;
    }
}
