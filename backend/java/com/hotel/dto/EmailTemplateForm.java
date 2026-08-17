package com.hotel.dto;

public record EmailTemplateForm(Long id, String templateCode, String templateName,
                                String eventCode, String subjectTemplate, String bodyHtml,
                                String bodyText, boolean active) {
    public Long getId() {
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
}
