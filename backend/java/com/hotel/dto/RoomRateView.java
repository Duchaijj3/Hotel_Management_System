package com.hotel.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RoomRateView(long id, long roomTypeId, String roomTypeName,
                           LocalDate rateDate, BigDecimal nightlyPrice,
                           boolean stopSell) {
    public long getId() { return id; }
    public long getRoomTypeId() { return roomTypeId; }
    public String getRoomTypeName() { return roomTypeName; }
    public LocalDate getRateDate() { return rateDate; }
    public BigDecimal getNightlyPrice() { return nightlyPrice; }
    public boolean isStopSell() { return stopSell; }
}
