package com.hotel.dao;

import com.hotel.dto.RoomTypeForm;
import com.hotel.dto.RoomTypeView;
import com.hotel.dto.RoomForm;
import com.hotel.dto.RoomView;
import com.hotel.dto.RoomRateView;
import com.hotel.dto.PageResult;
import com.hotel.dto.RoomSearchCriteria;
import com.hotel.dto.RoomTypeSearchCriteria;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ManagerRoomDao {
    boolean roomTypeCodeExists(String typeCode, Long excludedId);

    long createRoomType(RoomTypeForm form, String amenitiesJson);

    Optional<RoomTypeView> findRoomType(long id);

    boolean setRoomTypeActive(long id, boolean active);

    boolean updateRoomType(RoomTypeForm form, String amenitiesJson);

    boolean roomNumberExists(String roomNumber, Long excludedId);

    long createRoom(RoomForm form);

    Optional<RoomView> findRoom(long id);

    boolean updateRoom(RoomForm form);

    boolean changeRoomOperationalStatus(long id, String status, boolean active);

    void upsertRoomRates(List<RoomRateView> rates);

    Optional<RoomRateView> findRoomRate(long roomTypeId, LocalDate rateDate);

    default PageResult<RoomTypeView> searchRoomTypes(RoomTypeSearchCriteria criteria) {
        return new PageResult<>(List.of(), criteria.page(), criteria.pageSize(), 0);
    }

    default PageResult<RoomView> searchRooms(RoomSearchCriteria criteria) {
        return new PageResult<>(List.of(), criteria.page(), criteria.pageSize(), 0);
    }

    default List<RoomRateView> searchRoomRates(long roomTypeId, LocalDate startDate,
                                               LocalDate endDate) {
        return List.of();
    }

    default PageResult<RoomRateView> searchRoomRates(long roomTypeId,
                                                     LocalDate startDate,
                                                     LocalDate endDate,
                                                     int page, int pageSize) {
        List<RoomRateView> all = searchRoomRates(roomTypeId, startDate, endDate);
        int safePage = Math.max(1, page);
        int safePageSize = pageSize <= 0 ? 25 : Math.min(pageSize, 100);
        int fromIndex = Math.min((safePage - 1) * safePageSize, all.size());
        int toIndex = Math.min(fromIndex + safePageSize, all.size());
        return new PageResult<>(all.subList(fromIndex, toIndex), safePage,
                safePageSize, all.size());
    }
}
