package com.hotel.dto;

import java.util.Locale;
import java.util.Set;

public record RoomSearchCriteria(String keyword, Long roomTypeId,
                                 String operationalStatus, Boolean active,
                                 Integer floorNumber, int page, int pageSize) {
    private static final Set<String> STATUSES = Set.of(
            "AVAILABLE", "OCCUPIED", "MAINTENANCE", "OUT_OF_SERVICE");

    public RoomSearchCriteria {
        keyword = keyword == null || keyword.isBlank() ? null : keyword.trim();
        roomTypeId = roomTypeId != null && roomTypeId > 0 ? roomTypeId : null;
        operationalStatus = operationalStatus == null || operationalStatus.isBlank()
                ? null : operationalStatus.trim().toUpperCase(Locale.ROOT);
        operationalStatus = operationalStatus != null && STATUSES.contains(operationalStatus)
                ? operationalStatus : null;
        page = Math.max(1, page);
        pageSize = pageSize <= 0 ? 20 : Math.min(pageSize, 100);
    }

    public String getKeyword() { return keyword; }
    public Long getRoomTypeId() { return roomTypeId; }
    public String getOperationalStatus() { return operationalStatus; }
    public Boolean getActive() { return active; }
    public Integer getFloorNumber() { return floorNumber; }
    public int getPage() { return page; }
    public int getPageSize() { return pageSize; }
}
