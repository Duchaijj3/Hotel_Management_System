package com.hotel.service.impl;

import com.hotel.dao.ManagerRoomDao;
import com.hotel.dto.RoomTypeForm;
import com.hotel.dto.RoomTypeView;
import com.hotel.dto.RoomForm;
import com.hotel.dto.RoomView;
import com.hotel.dto.RoomRateForm;
import com.hotel.dto.RoomRateView;
import com.hotel.dto.ResolvedRoomRate;
import com.hotel.dto.PageResult;
import com.hotel.dto.RoomSearchCriteria;
import com.hotel.dto.RoomTypeSearchCriteria;
import com.hotel.exception.ValidationException;
import com.hotel.service.ManagerRoomService;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Locale;
import java.util.Set;

public class ManagerRoomServiceImpl implements ManagerRoomService {
    private static final Set<String> OPERATIONAL_STATUSES = Set.of(
            "AVAILABLE", "OCCUPIED", "MAINTENANCE", "OUT_OF_SERVICE");
    private final ManagerRoomDao rooms;
    private final Clock clock;

    public ManagerRoomServiceImpl(ManagerRoomDao rooms) {
        this(rooms, Clock.systemUTC());
    }

    public ManagerRoomServiceImpl(ManagerRoomDao rooms, Clock clock) {
        this.rooms = rooms;
        this.clock = clock;
    }

    @Override
    public long createRoomType(RoomTypeForm form) throws ValidationException {
        validateRoomType(form);
        return rooms.createRoomType(form, amenitiesJson(form));
    }

    @Override
    public Optional<RoomTypeView> getRoomType(long id) {
        return id > 0 ? rooms.findRoomType(id) : Optional.empty();
    }

    @Override
    public void setRoomTypeActive(long id, boolean active) throws ValidationException {
        if (id <= 0 || !rooms.setRoomTypeActive(id, active)) {
            throw new ValidationException(Map.of("general", "Room type not found."));
        }
    }

    @Override
    public void updateRoomType(RoomTypeForm form) throws ValidationException {
        validateRoomType(form);
        if (form.id() == null || form.id() <= 0 || form.version() == null) {
            throw new ValidationException(Map.of(
                    "general", "Room type update version is missing."));
        }
        if (!rooms.updateRoomType(form, amenitiesJson(form))) {
            throw new ValidationException(Map.of(
                    "general", "Room type was changed by another user. Reload and try again."));
        }
    }

    @Override
    public long createRoom(RoomForm form) throws ValidationException {
        validateRoom(form, false);
        return rooms.createRoom(form);
    }

    @Override
    public Optional<RoomView> getRoom(long id) {
        return id > 0 ? rooms.findRoom(id) : Optional.empty();
    }

    @Override
    public void updateRoom(RoomForm form) throws ValidationException {
        validateRoom(form, true);
        if (form.id() == null || form.id() <= 0 || !rooms.updateRoom(form)) {
            throw new ValidationException(Map.of(
                    "general", "Room was changed by another user. Reload and try again."));
        }
    }

    @Override
    public void changeRoomOperationalStatus(long id, String status, boolean active)
            throws ValidationException {
        if (!OPERATIONAL_STATUSES.contains(status)) {
            throw new ValidationException(Map.of(
                    "operationalStatus", "Operational status is invalid."));
        }
        Optional<RoomView> room = rooms.findRoom(id);
        if (id <= 0 || room.isEmpty()) {
            throw new ValidationException(Map.of("general", "Room not found."));
        }
        RoomTypeView type = rooms.findRoomType(room.orElseThrow().roomTypeId())
                .orElseThrow(() -> new ValidationException(
                        Map.of("general", "Room type not found.")));
        String enforcedStatus = !type.active() && !"OCCUPIED".equals(status)
                ? "OUT_OF_SERVICE" : status;
        if (!rooms.changeRoomOperationalStatus(id, enforcedStatus, active)) {
            throw new ValidationException(Map.of("general", "Room not found."));
        }
    }

    @Override
    public void setRoomRateRange(RoomRateForm form) throws ValidationException {
        Map<String, String> errors = new LinkedHashMap<>();
        Optional<RoomTypeView> roomType = rooms.findRoomType(form.roomTypeId());
        if (form.roomTypeId() <= 0 || roomType.isEmpty()) {
            errors.put("roomTypeId", "Room type is required.");
        }
        if (form.startDate() == null || form.endDate() == null
                || form.endDate().isBefore(form.startDate())) {
            errors.put("dateRange", "End date must not be before start date.");
        }
        if (form.nightlyPrice() == null
                || form.nightlyPrice().compareTo(BigDecimal.ZERO) < 0) {
            errors.put("nightlyPrice", "Nightly price cannot be negative.");
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
        List<RoomRateView> rates = new ArrayList<>();
        for (LocalDate date = form.startDate(); !date.isAfter(form.endDate());
             date = date.plusDays(1)) {
            rates.add(new RoomRateView(0, form.roomTypeId(),
                    roomType.orElseThrow().typeName(), date,
                    form.nightlyPrice(), form.stopSell()));
        }
        rooms.upsertRoomRates(rates);
    }

    @Override
    public ResolvedRoomRate resolveRoomRate(long roomTypeId, LocalDate rateDate)
            throws ValidationException {
        Optional<RoomTypeView> roomType = rooms.findRoomType(roomTypeId);
        if (roomType.isEmpty() || rateDate == null) {
            throw new ValidationException(Map.of("roomTypeId", "Room type is invalid."));
        }
        Optional<RoomRateView> daily = rooms.findRoomRate(roomTypeId, rateDate);
        return daily.map(rate -> new ResolvedRoomRate(
                        rate.nightlyPrice(), rate.stopSell(), true))
                .orElseGet(() -> new ResolvedRoomRate(
                        roomType.orElseThrow().basePrice(), false, false));
    }

    @Override
    public PageResult<RoomTypeView> searchRoomTypes(RoomTypeSearchCriteria criteria) {
        return rooms.searchRoomTypes(criteria);
    }

    @Override
    public PageResult<RoomView> searchRooms(RoomSearchCriteria criteria) {
        return rooms.searchRooms(criteria);
    }

    @Override
    public List<RoomRateView> searchRoomRates(long roomTypeId, LocalDate startDate,
                                              LocalDate endDate) {
        return rooms.searchRoomRates(roomTypeId, startDate, endDate);
    }

    @Override
    public PageResult<RoomRateView> searchRoomRates(long roomTypeId,
                                                    LocalDate startDate,
                                                    LocalDate endDate,
                                                    int page, int pageSize) {
        return rooms.searchRoomRates(roomTypeId, startDate, endDate, page, pageSize);
    }

    private String amenitiesJson(RoomTypeForm form) {
        StringBuilder json = new StringBuilder("[");
        for (int index = 0; index < form.amenities().size(); index++) {
            if (index > 0) {
                json.append(',');
            }
            json.append('"').append(escape(form.amenities().get(index))).append('"');
        }
        return json.append(']').toString();
    }

    private void validateRoomType(RoomTypeForm form) throws ValidationException {
        Map<String, String> errors = new LinkedHashMap<>();
        if (form.typeCode() == null || form.typeCode().isBlank()) {
            errors.put("typeCode", "Type code is required.");
        } else if (form.typeCode().length() > 20) {
            errors.put("typeCode", "Type code cannot exceed 20 characters.");
        }
        if (form.typeName() == null || form.typeName().isBlank()) {
            errors.put("typeName", "Type name is required.");
        } else if (form.typeName().length() > 100) {
            errors.put("typeName", "Type name cannot exceed 100 characters.");
        }
        if (form.maxAdults() <= 0) {
            errors.put("maxAdults", "Adult capacity must be greater than zero.");
        }
        if (form.maxChildren() < 0) {
            errors.put("maxChildren", "Child capacity cannot be negative.");
        }
        if (form.roomSizeM2() != null
                && form.roomSizeM2().compareTo(BigDecimal.ZERO) <= 0) {
            errors.put("roomSizeM2", "Room size must be greater than zero.");
        }
        if (form.basePrice() == null
                || form.basePrice().compareTo(BigDecimal.ZERO) < 0) {
            errors.put("basePrice", "Base price cannot be negative.");
        }
        if (form.bedType() != null && form.bedType().length() > 50) {
            errors.put("bedType", "Bed type cannot exceed 50 characters.");
        }
        if (!errors.containsKey("typeCode") && rooms.roomTypeCodeExists(
                form.typeCode().trim().toUpperCase(Locale.ROOT), form.id())) {
            errors.put("typeCode", "Type code already exists.");
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    private void validateRoom(RoomForm form, boolean updating) throws ValidationException {
        Map<String, String> errors = new LinkedHashMap<>();
        Optional<RoomTypeView> roomType = rooms.findRoomType(form.roomTypeId());
        if (form.roomTypeId() <= 0 || roomType.isEmpty()) {
            errors.put("roomTypeId", "Room type is required.");
        } else if (!roomType.orElseThrow().active()) {
            errors.put("roomTypeId",
                    "Only active room types can be assigned to rooms.");
        }
        if (form.roomNumber() == null || form.roomNumber().isBlank()) {
            errors.put("roomNumber", "Room number is required.");
        } else if (rooms.roomNumberExists(form.roomNumber().trim(), form.id())) {
            errors.put("roomNumber", "Room number already exists.");
        }
        if (!OPERATIONAL_STATUSES.contains(form.operationalStatus())) {
            errors.put("operationalStatus", "Operational status is invalid.");
        }
        if (updating && form.version() == null) {
            errors.put("general", "Room update version is missing.");
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    private String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
