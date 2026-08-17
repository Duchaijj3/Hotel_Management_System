package com.hotel.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record RoomTypeForm(Long id, String typeCode, String typeName, String description,
                           int maxAdults, int maxChildren, String bedType,
                           BigDecimal roomSizeM2, BigDecimal basePrice,
                           List<String> amenities, LocalDateTime version) {
    public RoomTypeForm {
        amenities = amenities == null ? List.of() : List.copyOf(amenities);
    }

    public Long getId() { return id; }
    public String getTypeCode() { return typeCode; }
    public String getTypeName() { return typeName; }
    public String getDescription() { return description; }
    public int getMaxAdults() { return maxAdults; }
    public int getMaxChildren() { return maxChildren; }
    public String getBedType() { return bedType; }
    public BigDecimal getRoomSizeM2() { return roomSizeM2; }
    public BigDecimal getBasePrice() { return basePrice; }
    public List<String> getAmenities() { return amenities; }
    public LocalDateTime getVersion() { return version; }
    public LocalDateTime getUpdatedAt() { return version; }
}
