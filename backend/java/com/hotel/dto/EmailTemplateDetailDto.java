package com.hotel.dto;

import java.time.LocalDateTime;

public record EmailTemplateDetailDto(long id, String templateCode, String templateName,
                                     String eventCode, String subjectTemplate, String bodyHtml,
                                     String bodyText, boolean active, LocalDateTime createdAt,
                                     LocalDateTime updatedAt) {
    public long getId() {
        return id;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public String getTemplateName() {
        return templateName;
    }

    public String getEventCode() {
        return eventCode;
    }

    public String getSubjectTemplate() {
        return subjectTemplate;
    }

    public String getBodyHtml() {
        return bodyHtml;
    }

    public String getBodyText() {
        return bodyText;
    }

    public boolean isActive() {
        return active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
