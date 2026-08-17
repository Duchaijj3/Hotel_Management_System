package com.hotel.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record RoomTypeView(long id, String typeCode, String typeName, String description,
                           int maxAdults, int maxChildren, String bedType,
                           BigDecimal roomSizeM2, BigDecimal basePrice,
                           List<String> amenities, boolean active,
                           LocalDateTime updatedAt) {
    public long getId() { return id; }
    public String getTypeCode() { return typeCode; }
    public String getTypeName() { return typeName; }
    public String getDescription() { return description; }
    public int getMaxAdults() { return maxAdults; }
    public int getMaxChildren() { return maxChildren; }
    public String getBedType() { return bedType; }
    public BigDecimal getRoomSizeM2() { return roomSizeM2; }
    public BigDecimal getBasePrice() { return basePrice; }
    public List<String> getAmenities() { return amenities; }
    public boolean isActive() { return active; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
