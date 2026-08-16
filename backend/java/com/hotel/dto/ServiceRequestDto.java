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
        String cancellationReason,
        LocalDateTime requestedAt
) {}