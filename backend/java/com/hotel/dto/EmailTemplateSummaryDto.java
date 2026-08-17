package com.hotel.dto;

public record EmailTemplateSummaryDto(long id, String templateCode, String templateName,
                                      String eventCode, boolean active) {
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

    public boolean isActive() {
        return active;
    }
}
