package com.hotel.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ServiceRequestDto(
        long requestId,
        long reservationId,
        long customerId,
        long hotelServiceId,
        String serviceName,
        BigDecimal quantity,
        BigDecimal unitPrice,
        BigDecimal totalAmount,
        String status,
        Long assignedStaffId,
        String notes,
        String cancellationReason,
        LocalDateTime requestedAt,
        LocalDateTime requestedForAt,
        LocalDateTime assignedAt,
        LocalDateTime startedAt,
        LocalDateTime completedAt
) {
    public long getRequestId() { return requestId; }
    public long getReservationId() { return reservationId; }
    public long getCustomerId() { return customerId; }
    public long getHotelServiceId() { return hotelServiceId; }
    public String getServiceName() { return serviceName; }
    public BigDecimal getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    public Long getAssignedStaffId() { return assignedStaffId; }
    public String getNotes() { return notes; }
    public String getCancellationReason() { return cancellationReason; }
    public LocalDateTime getRequestedAt() { return requestedAt; }
    public LocalDateTime getRequestedForAt() { return requestedForAt; }
    public LocalDateTime getAssignedAt() { return assignedAt; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
}