package com.hotel.service;

import com.hotel.dto.PageResult;
import com.hotel.dto.PublicRoomTypeDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PublicRoomService {
    PageResult<PublicRoomTypeDto> search(
            String keyword,
            LocalDate checkIn,
            LocalDate checkOut,
            Integer guests,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            List<String> typeCodes,
            List<String> amenities,
            int page,
            int pageSize);

    List<PublicRoomTypeDto> search(
            String keyword,
            LocalDate checkIn,
            LocalDate checkOut,
            Integer guests,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            List<String> typeCodes,
            List<String> amenities);

    Optional<PublicRoomTypeDto> detail(long roomTypeId);
}