package com.hotel.service;

import com.hotel.dto.RoomTypeForm;
import com.hotel.dto.RoomTypeView;
import com.hotel.dto.RoomForm;
import com.hotel.dto.RoomView;
import com.hotel.dto.RoomRateForm;
import com.hotel.dto.ResolvedRoomRate;
import com.hotel.dto.PageResult;
import com.hotel.dto.RoomSearchCriteria;
import com.hotel.dto.RoomTypeSearchCriteria;
import com.hotel.dto.RoomRateView;
import com.hotel.exception.ValidationException;

import java.util.Optional;
import java.time.LocalDate;
import java.util.List;

public interface ManagerRoomService {
    long createRoomType(RoomTypeForm form) throws ValidationException;

    Optional<RoomTypeView> getRoomType(long id);

    void setRoomTypeActive(long id, boolean active) throws ValidationException;

    void updateRoomType(RoomTypeForm form) throws ValidationException;

    long createRoom(RoomForm form) throws ValidationException;

    Optional<RoomView> getRoom(long id);

    void updateRoom(RoomForm form) throws ValidationException;

    void changeRoomOperationalStatus(long id, String status, boolean active)
            throws ValidationException;

    void setRoomRateRange(RoomRateForm form) throws ValidationException;

    ResolvedRoomRate resolveRoomRate(long roomTypeId, LocalDate rateDate)
            throws ValidationException;

    PageResult<RoomTypeView> searchRoomTypes(RoomTypeSearchCriteria criteria);

    PageResult<RoomView> searchRooms(RoomSearchCriteria criteria);

    List<RoomRateView> searchRoomRates(long roomTypeId, LocalDate startDate,
                                       LocalDate endDate);

    PageResult<RoomRateView> searchRoomRates(long roomTypeId, LocalDate startDate,
                                             LocalDate endDate, int page,
                                             int pageSize);
}
