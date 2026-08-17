package com.hotel.dto;

public record UserSearchCriteria(String keyword, String roleCode, String statusCode,
                                 int page, int pageSize) {
    public String getKeyword() {
        return keyword;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }
}
