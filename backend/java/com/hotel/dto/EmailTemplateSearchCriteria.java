package com.hotel.dto;

public record EmailTemplateSearchCriteria(String keyword, String eventCode, Boolean active,
                                          int page, int pageSize) {
    public String getKeyword() {
        return keyword;
    }

    public String getEventCode() {
        return eventCode;
    }

    public Boolean getActive() {
        return active;
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }
}
