package com.hotel.model;

// File: src/main/java/com/hotel/model/Reservation.java

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Reservation entity (replaces Booking)
 * Represents a customer's stay reservation with full lifecycle tracking
 */
public class Reservation {
    private long reservationId;
    private long customerId;
    private long createdByUserId;          // Receptionist/system user who created it
    private long checkedInByUserId;        // Staff who performed check-in
    private long checkedOutByUserId;       // Staff who performed check-out
    private String bookingCode;            // E.g., "RES-2024-001234"
    private String sourceCode;             // ONLINE, RECEPTIONIST, WALK_IN
    private String statusCode;             // PENDING, CONFIRMED, CHECKED_IN, CHECKED_OUT, CANCELLED, NO_SHOW
    private LocalDateTime bookedAt;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private LocalDateTime actualCheckInAt;
    private LocalDateTime actualCheckOutAt;
    private int adultCount;
    private int childCount;
    private BigDecimal roomSubtotal;       // Total room charges
    private BigDecimal serviceTotal;       // Total service charges
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private BigDecimal depositRequired;
    private String specialRequests;
    private String cancellationReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Reservation() {}

    // Getters and Setters (abbreviated - you'll add full set)
    public long getReservationId() { return reservationId; }
    public void setReservationId(long reservationId) { this.reservationId = reservationId; }

    public long getCustomerId() { return customerId; }
    public void setCustomerId(long customerId) { this.customerId = customerId; }

    public String getBookingCode() { return bookingCode; }
    public void setBookingCode(String bookingCode) { this.bookingCode = bookingCode; }

    public String getStatusCode() { return statusCode; }
    public void setStatusCode(String statusCode) { this.statusCode = statusCode; }

    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }

    public LocalDate getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }

    public LocalDateTime getActualCheckInAt() { return actualCheckInAt; }
    public void setActualCheckInAt(LocalDateTime actualCheckInAt) { this.actualCheckInAt = actualCheckInAt; }

    public LocalDateTime getActualCheckOutAt() { return actualCheckOutAt; }
    public void setActualCheckOutAt(LocalDateTime actualCheckOutAt) { this.actualCheckOutAt = actualCheckOutAt; }

    public int getAdultCount() { return adultCount; }
    public void setAdultCount(int adultCount) { this.adultCount = adultCount; }

    public int getChildCount() { return childCount; }
    public void setChildCount(int childCount) { this.childCount = childCount; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // ... add all other getters/setters
}
