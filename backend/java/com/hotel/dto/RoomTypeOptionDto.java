package com.hotel.dto;
public record RoomTypeOptionDto(long roomTypeId,String typeCode,String typeName,int maxAdults,int maxChildren){
    public long getRoomTypeId(){return roomTypeId;}public String getTypeCode(){return typeCode;}public String getTypeName(){return typeName;}public int getMaxAdults(){return maxAdults;}public int getMaxChildren(){return maxChildren;}
}