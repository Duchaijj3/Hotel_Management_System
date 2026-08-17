package com.hotel.dto;

public record EmailDeliverySearchCriteria(String statusCode, String eventCode,
                                          int page, int pageSize) {
    public String getStatusCode() {
        return statusCode;
    }

    public String getEventCode() {
        return eventCode;
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }
}
