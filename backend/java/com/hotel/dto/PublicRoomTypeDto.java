package com.hotel.dto;

import java.math.BigDecimal;
import java.util.List;

public record PublicRoomTypeDto(
        long roomTypeId,
        String typeCode,
        String typeName,
        String description,
        int maxAdults,
        int maxChildren,
        String bedType,
        Double roomSizeM2,
        BigDecimal basePrice,
        List<String> amenities,
        List<String> images,
        int availableRoomsCount) {
    public long getRoomTypeId() {
        return roomTypeId;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getDescription() {
        return description;
    }

    public int getMaxAdults() {
        return maxAdults;
    }

    public int getMaxChildren() {
        return maxChildren;
    }

    public String getBedType() {
        return bedType;
    }

    public Double getRoomSizeM2() {
        return roomSizeM2;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public List<String> getAmenities() {
        return amenities;
    }

    public List<String> getImages() {
        return images;
    }

    public int getAvailableRoomsCount() {
        return availableRoomsCount;
    }
}
