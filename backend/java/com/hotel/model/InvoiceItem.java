package com.hotel.model;

// File: src/main/java/com/hotel/model/InvoiceItem.java

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * InvoiceItem entity - line items in an invoice
 * Can be room charges, service charges, or extra items
 */
public class InvoiceItem {
    private long invoiceItemId;
    private long invoiceId;
    private long serviceRequestId;        // If this item is from a service
    private long postedByUserId;          // Staff who posted the charge
    private String itemType;              // ROOM, SERVICE, EXTRA
    private String description;           // Item description
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal amount;            // quantity * unitPrice
    private LocalDateTime postedAt;
    private boolean voided;

    public InvoiceItem() {}

    // Getters and Setters
    public long getInvoiceItemId() { return invoiceItemId; }
    public void setInvoiceItemId(long invoiceItemId) { this.invoiceItemId = invoiceItemId; }

    public long getInvoiceId() { return invoiceId; }
    public void setInvoiceId(long invoiceId) { this.invoiceId = invoiceId; }

    public long getServiceRequestId() { return serviceRequestId; }
    public void setServiceRequestId(long serviceRequestId) { this.serviceRequestId = serviceRequestId; }

    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public boolean isVoided() { return voided; }
    public void setVoided(boolean voided) { this.voided = voided; }

    // ... add all other getters/setters
}
