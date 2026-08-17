package com.hotel.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RoomRateForm(long roomTypeId, LocalDate startDate, LocalDate endDate,
                           BigDecimal nightlyPrice, boolean stopSell) {
    public long getRoomTypeId() { return roomTypeId; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public BigDecimal getNightlyPrice() { return nightlyPrice; }
    public boolean isStopSell() { return stopSell; }
}
