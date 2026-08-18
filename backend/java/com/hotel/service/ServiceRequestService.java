package java.com.hotel.service;

// File: src/main/java/com/hotel/service/ServiceRequestService.java
import java.com.hotel.model.ServiceRequest;
import java.util.List;

/**
 * Service interface cho service requests - cập nhật với tính năng mới
 */
public interface ServiceRequestService {
    // Guest requests service
    void requestService(long reservationId, long customerId, long hotelServiceId,
                        java.math.BigDecimal quantity) throws Exception;

    // Staff management
    List<ServiceRequest> getPendingRequests() throws Exception;
    List<ServiceRequest> getAssignedToMe(long staffUserId) throws Exception;
    void assignServiceToStaff(long serviceRequestId, long staffUserId) throws Exception;
    void startService(long serviceRequestId) throws Exception;
    void completeService(long serviceRequestId) throws Exception;
    void cancelService(long serviceRequestId, String reason) throws Exception;

    // Billing integration
    List<ServiceRequest> getCompletedServicesForReservation(long reservationId) throws Exception;
    java.math.BigDecimal calculateServiceChargesForReservation(long reservationId) throws Exception;
}