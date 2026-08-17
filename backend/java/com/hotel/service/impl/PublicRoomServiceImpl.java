package com.hotel.service.impl;

import com.hotel.dao.PublicRoomDao;
import com.hotel.dao.impl.PublicRoomDaoImpl;
import com.hotel.dto.PageResult;
import com.hotel.dto.PublicRoomTypeDto;
import com.hotel.service.PublicRoomService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class PublicRoomServiceImpl implements PublicRoomService {
    private final PublicRoomDao dao;

    public PublicRoomServiceImpl() {
        this(new PublicRoomDaoImpl());
    }

    public PublicRoomServiceImpl(PublicRoomDao dao) {
        this.dao = dao;
    }

    @Override
    public PageResult<PublicRoomTypeDto> search(
            String keyword,
            LocalDate checkIn,
            LocalDate checkOut,
            Integer guests,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            List<String> typeCodes,
            List<String> amenities,
            int page,
            int pageSize) {
        return dao.searchRoomTypes(keyword, checkIn, checkOut, guests, minPrice, maxPrice, typeCodes, amenities, page,
                pageSize);
    }

    @Override
    public List<PublicRoomTypeDto> search(
            String keyword,
            LocalDate checkIn,
            LocalDate checkOut,
            Integer guests,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            List<String> typeCodes,
            List<String> amenities) {
        return dao.searchRoomTypes(keyword, checkIn, checkOut, guests, minPrice, maxPrice, typeCodes, amenities);
    }

    @Override
    public Optional<PublicRoomTypeDto> detail(long roomTypeId) {
        if (roomTypeId <= 0)
            return Optional.empty();
        return dao.findById(roomTypeId);
    }
}
