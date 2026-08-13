package com.hotel.dao;

// File: src/main/java/com/hotel/dao/ServiceRequestDao.java

import com.hotel.model.ServiceRequest;
import java.util.List;

/**
 * DAO interface for ServiceRequest - updated with new fields and full staff workflow
 */
public interface ServiceRequestDao {

    // Create new request
    void addServiceRequest(ServiceRequest request) throws Exception;

    // Read operations
    ServiceRequest getServiceRequestById(long serviceRequestId) throws Exception;
    List<ServiceRequest> getPendingRequests() throws Exception;
    List<ServiceRequest> getRequestsByStaffId(long staffUserId) throws Exception;
    List<ServiceRequest> getServiceRequestsByReservation(long reservationId) throws Exception;
    List<ServiceRequest> getCompletedRequestsByReservationId(long reservationId) throws Exception;

    // Staff workflow actions
    void assignStaff(long serviceRequestId, long staffUserId) throws Exception;
    void updateStatus(long serviceRequestId, String statusCode) throws Exception;
    void cancelRequest(long serviceRequestId, String reason) throws Exception;
}
