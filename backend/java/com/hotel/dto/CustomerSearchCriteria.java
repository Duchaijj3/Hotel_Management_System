package com.hotel.dto;

import java.util.Locale;
import java.util.Set;

public record CustomerSearchCriteria(String keyword, String status, int page, int pageSize) {
    private static final Set<String> VALID_STATUSES = Set.of("ACTIVE", "INACTIVE");

    public CustomerSearchCriteria {
        keyword = normalize(keyword);
        if (keyword != null && keyword.length() > 255) {
            keyword = keyword.substring(0, 255);
        }
        status = normalize(status);
        status = status == null ? null : status.toUpperCase(Locale.ROOT);
        if (status != null && !VALID_STATUSES.contains(status)) {
            status = null;
        }
        page = Math.max(1, page);
        pageSize = pageSize <= 0 ? 20 : Math.min(pageSize, 100);
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim().replaceAll("\\s+", " ");
    }

    public String getKeyword(){return keyword;}
    public String getStatus(){return status;}
    public int getPage(){return page;}
    public int getPageSize(){return pageSize;}
}
