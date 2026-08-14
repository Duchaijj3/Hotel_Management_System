package com.hotel.model;

// File: src/main/java/com/hotel/model/EmailTemplate.java

import java.time.LocalDateTime;

/**
 * EmailTemplate entity - template for automated email notifications
 */
public class EmailTemplate {
    private long emailTemplateId;
    private String templateCode;          // Unique identifier
    private String templateName;
    private String eventCode;             // ACCOUNT_VERIFICATION, PASSWORD_RESET, RESERVATION_CONFIRMED, etc.
    private String subjectTemplate;       // Email subject with placeholders
    private String bodyHtml;              // HTML email body with placeholders
    private String bodyText;              // Plain text fallback
    private boolean active;
    private long createdByUserId;
    private long updatedByUserId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public EmailTemplate() {}

    // Getters and Setters
    public long getEmailTemplateId() { return emailTemplateId; }
    public void setEmailTemplateId(long emailTemplateId) { this.emailTemplateId = emailTemplateId; }

    public String getTemplateCode() { return templateCode; }
    public void setTemplateCode(String templateCode) { this.templateCode = templateCode; }

    public String getEventCode() { return eventCode; }
    public void setEventCode(String eventCode) { this.eventCode = eventCode; }

    public String getSubjectTemplate() { return subjectTemplate; }
    public void setSubjectTemplate(String subjectTemplate) { this.subjectTemplate = subjectTemplate; }

    public String getBodyHtml() { return bodyHtml; }
    public void setBodyHtml(String bodyHtml) { this.bodyHtml = bodyHtml; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    // ... add all other getters/setters
}
