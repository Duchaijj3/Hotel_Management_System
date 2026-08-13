package java.com.hotel.model;


// File: src/main/java/com/hotel/model/ServiceRequest.java

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ServiceRequest entity - guest service requests during stay
 * Includes detailed timing, staff assignment, and helper methods for standard compatibility.
 */
public class ServiceRequest {
    private long serviceRequestId;
    private long reservationId;
    private long customerId;
    private long hotelServiceId;
    private Long assignedStaffUserId;      // Staff assigned to perform service
    private BigDecimal quantity;           // E.g., 2.5 kg, 1 hour
    private BigDecimal unitPriceSnapshot;  // Price at time of request
    private BigDecimal totalAmount;
    private String statusCode;             // PENDING, ASSIGNED, IN_PROGRESS, COMPLETED, CANCELLED
    private String cancelReason;
    private LocalDateTime requestedAt;
    private LocalDateTime assignedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String notes;

    // For display / relation
    private HotelService service;

    public ServiceRequest() {}

    // ==========================================
    // Primary Getters and Setters
    // ==========================================

    public long getServiceRequestId() { return serviceRequestId; }
    public void setServiceRequestId(long serviceRequestId) { this.serviceRequestId = serviceRequestId; }

    public long getReservationId() { return reservationId; }
    public void setReservationId(long reservationId) { this.reservationId = reservationId; }

    public long getCustomerId() { return customerId; }
    public void setCustomerId(long customerId) { this.customerId = customerId; }

    public long getHotelServiceId() { return hotelServiceId; }
    public void setHotelServiceId(long hotelServiceId) { this.hotelServiceId = hotelServiceId; }

    public Long getAssignedStaffUserId() { return assignedStaffUserId; }
    public void setAssignedStaffUserId(Long assignedStaffUserId) { this.assignedStaffUserId = assignedStaffUserId; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPriceSnapshot() { return unitPriceSnapshot; }
    public void setUnitPriceSnapshot(BigDecimal unitPriceSnapshot) { this.unitPriceSnapshot = unitPriceSnapshot; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getStatusCode() { return statusCode; }
    public void setStatusCode(String statusCode) { this.statusCode = statusCode; }

    public String getCancelReason() { return cancelReason; }
    public void setCancelReason(String cancelReason) { this.cancelReason = cancelReason; }

    public LocalDateTime getRequestedAt() { return requestedAt; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }

    public LocalDateTime getAssignedAt() { return assignedAt; }
    public void setAssignedAt(LocalDateTime assignedAt) { this.assignedAt = assignedAt; }

    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public HotelService getService() { return service; }
    public void setService(HotelService service) { this.service = service; }

    // ==========================================
    // Compatibility Aliases (Tránh lỗi Build với code cũ)
    // ==========================================

    /** Map request.getId() / request.setId() -> serviceRequestId */
    public long getId() { return serviceRequestId; }
    public void setId(long id) { this.serviceRequestId = id; }

    /** Map request.getRequestId() -> serviceRequestId */
    public long getRequestId() { return serviceRequestId; }
    public void setRequestId(long requestId) { this.serviceRequestId = requestId; }

    /** Map request.getUnitPrice() / setUnitPrice() -> unitPriceSnapshot */
    public BigDecimal getUnitPrice() { return unitPriceSnapshot; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPriceSnapshot = unitPrice; }

    /** Map request.getStatus() / setStatus() -> statusCode */
    public String getStatus() { return statusCode; }
    public void setStatus(String status) { this.statusCode = status; }

    /** Map request.getAssignedStaffId() -> assignedStaffUserId */
    public Long getAssignedStaffId() { return assignedStaffUserId; }
    public void setAssignedStaffId(Long assignedStaffId) { this.assignedStaffUserId = assignedStaffId; }
}