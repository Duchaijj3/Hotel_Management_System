package java.com.hotel.model;

// File: src/main/java/com/hotel/model/Invoice.java

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Invoice entity - billing document for a reservation
 */
public class Invoice {
    private long invoiceId;
    private long reservationId;
    private long customerId;
    private long issuedByUserId;           // Staff who issued
    private String invoiceNumber;         // Unique invoice code
    private LocalDateTime issuedAt;
    private String currencyCode;          // VND, USD, etc.
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private String statusCode;            // DRAFT, ISSUED, PARTIALLY_PAID, PAID, CANCELLED
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Invoice() {}

    // Getters and Setters
    public long getInvoiceId() { return invoiceId; }
    public void setInvoiceId(long invoiceId) { this.invoiceId = invoiceId; }

    public long getReservationId() { return reservationId; }
    public void setReservationId(long reservationId) { this.reservationId = reservationId; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getStatusCode() { return statusCode; }
    public void setStatusCode(String statusCode) { this.statusCode = statusCode; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // ... add all other getters/setters
}