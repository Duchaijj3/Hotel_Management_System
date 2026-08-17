package com.hotel.dto;

public record RoomTypeSearchCriteria(String keyword, Boolean active, int page, int pageSize) {
    public RoomTypeSearchCriteria {
        keyword = normalize(keyword);
        page = Math.max(1, page);
        pageSize = pageSize <= 0 ? 20 : Math.min(pageSize, 100);
    }

    private static String normalize(String value) {
        return value == null || value.isBlank() ? null
                : value.trim().replaceAll("\\s+", " ");
    }

    public String getKeyword() { return keyword; }
    public Boolean getActive() { return active; }
    public int getPage() { return page; }
    public int getPageSize() { return pageSize; }
}
