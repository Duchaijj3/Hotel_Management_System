package com.hotel.dto;

import java.time.LocalDateTime;

public record EmailDeliveryDto(long id, String recipientEmail, String subject, String eventCode,
                               String statusCode, String errorMessage, int retryCount,
                               LocalDateTime sentAt, LocalDateTime createdAt) {
    public long getId() {
        return id;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public String getSubject() {
        return subject;
    }

    public String getEventCode() {
        return eventCode;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
