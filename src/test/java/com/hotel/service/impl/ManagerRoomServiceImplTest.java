package com.hotel.service.impl;

import com.hotel.dao.ManagerRoomDao;
import com.hotel.dto.RoomTypeForm;
import com.hotel.dto.RoomTypeView;
import com.hotel.dto.RoomForm;
import com.hotel.dto.RoomView;
import com.hotel.dto.RoomRateForm;
import com.hotel.dto.RoomRateView;
import com.hotel.dto.ResolvedRoomRate;
import com.hotel.exception.ValidationException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ManagerRoomServiceImplTest {
    private static final Clock CLOCK = Clock.fixed(
            Instant.parse("2026-08-17T03:00:00Z"), ZoneOffset.UTC);

    @Test
    void newlyCreatedRoomTypeStartsInactive() throws Exception {
        InMemoryManagerRoomDao rooms = new InMemoryManagerRoomDao();
        ManagerRoomServiceImpl service = new ManagerRoomServiceImpl(rooms, CLOCK);

        long id = service.createRoomType(validRoomType());

        assertFalse(service.getRoomType(id).orElseThrow().active());
    }

    @Test
    void invalidCapacityAndPriceAreRejected() {
        ManagerRoomServiceImpl service = new ManagerRoomServiceImpl(
                new InMemoryManagerRoomDao(), CLOCK);
        RoomTypeForm invalid = new RoomTypeForm(null, "", "", null,
                0, -1, null, BigDecimal.ZERO, new BigDecimal("-1"), List.of(), null);

        ValidationException error = assertThrows(ValidationException.class,
                () -> service.createRoomType(invalid));

        assertTrue(error.getErrors().containsKey("typeCode"));
        assertTrue(error.getErrors().containsKey("typeName"));
        assertTrue(error.getErrors().containsKey("maxAdults"));
        assertTrue(error.getErrors().containsKey("maxChildren"));
        assertTrue(error.getErrors().containsKey("roomSizeM2"));
        assertTrue(error.getErrors().containsKey("basePrice"));
    }

    @Test
    void duplicateRoomTypeCodeIsRejected() throws Exception {
        ManagerRoomServiceImpl service = new ManagerRoomServiceImpl(
                new InMemoryManagerRoomDao(), CLOCK);
        service.createRoomType(validRoomType());

        ValidationException error = assertThrows(ValidationException.class,
                () -> service.createRoomType(validRoomType()));

        assertTrue(error.getErrors().containsKey("typeCode"));
    }

    @Test
    void roomTypeCanBeActivatedAfterCreation() throws Exception {
        InMemoryManagerRoomDao rooms = new InMemoryManagerRoomDao();
        ManagerRoomServiceImpl service = new ManagerRoomServiceImpl(rooms, CLOCK);
        long id = service.createRoomType(validRoomType());

        service.setRoomTypeActive(id, true);

        assertTrue(service.getRoomType(id).orElseThrow().active());
    }

    @Test
    void inactiveRoomTypeCannotBeUsedForNewRoom() throws Exception {
        InMemoryManagerRoomDao rooms = new InMemoryManagerRoomDao();
        ManagerRoomServiceImpl service = new ManagerRoomServiceImpl(rooms, CLOCK);
        long roomTypeId = service.createRoomType(validRoomType());

        ValidationException error = assertThrows(ValidationException.class,
                () -> service.createRoom(new RoomForm(null, roomTypeId, "101", 1,
                        "AVAILABLE", null, true, null)));

        assertEquals("Only active room types can be assigned to rooms.",
                error.getErrors().get("roomTypeId"));
    }

    @Test
    void inactiveRoomTypeCannotBeAssignedWhenRoomIsUpdated() throws Exception {
        InMemoryManagerRoomDao rooms = new InMemoryManagerRoomDao();
        ManagerRoomServiceImpl service = new ManagerRoomServiceImpl(rooms, CLOCK);
        long roomTypeId = service.createRoomType(validRoomType());
        service.setRoomTypeActive(roomTypeId, true);
        long roomId = service.createRoom(new RoomForm(null, roomTypeId, "101", 1,
                "AVAILABLE", null, true, null));
        service.setRoomTypeActive(roomTypeId, false);
        RoomView current = service.getRoom(roomId).orElseThrow();

        ValidationException error = assertThrows(ValidationException.class,
                () -> service.updateRoom(new RoomForm(roomId, roomTypeId, "101", 1,
                        "OUT_OF_SERVICE", null, true, current.updatedAt())));

        assertEquals("Only active room types can be assigned to rooms.",
                error.getErrors().get("roomTypeId"));
    }

    @Test
    void amenitiesCanBeAttachedAndDetachedWhenRoomTypeIsUpdated() throws Exception {
        InMemoryManagerRoomDao rooms = new InMemoryManagerRoomDao();
        ManagerRoomServiceImpl service = new ManagerRoomServiceImpl(rooms, CLOCK);
        long id = service.createRoomType(validRoomType());
        RoomTypeForm update = new RoomTypeForm(id, "DLX", "Deluxe", "Updated",
                2, 1, "King", new BigDecimal("32.5"),
                new BigDecimal("1800000"), List.of("Wi-Fi"),
                service.getRoomType(id).orElseThrow().updatedAt());

        service.updateRoomType(update);

        assertEquals(List.of("Wi-Fi"), service.getRoomType(id).orElseThrow().amenities());
    }

    @Test
    void roomCreationKeepsOperationalAndCleaningStatusesSeparate() throws Exception {
        InMemoryManagerRoomDao rooms = new InMemoryManagerRoomDao();
        ManagerRoomServiceImpl service = new ManagerRoomServiceImpl(rooms, CLOCK);
        long roomTypeId = service.createRoomType(validRoomType());
        service.setRoomTypeActive(roomTypeId, true);

        long roomId = service.createRoom(new RoomForm(null, roomTypeId, "101", 1,
                "AVAILABLE", "Near elevator", true, null));

        RoomView room = service.getRoom(roomId).orElseThrow();
        assertEquals("AVAILABLE", room.operationalStatus());
        assertEquals("CLEAN", room.cleaningStatus());
    }

    @Test
    void operationalStatusCanChangeWithoutChangingCleaningStatus() throws Exception {
        InMemoryManagerRoomDao rooms = new InMemoryManagerRoomDao();
        ManagerRoomServiceImpl service = new ManagerRoomServiceImpl(rooms, CLOCK);
        long roomTypeId = service.createRoomType(validRoomType());
        service.setRoomTypeActive(roomTypeId, true);
        long roomId = service.createRoom(new RoomForm(null, roomTypeId, "101", 1,
                "AVAILABLE", null, true, null));

        service.changeRoomOperationalStatus(roomId, "OUT_OF_SERVICE", false);

        RoomView changed = service.getRoom(roomId).orElseThrow();
        assertEquals("OUT_OF_SERVICE", changed.operationalStatus());
        assertEquals("CLEAN", changed.cleaningStatus());
        assertFalse(changed.active());
    }

    @Test
    void dailyRateOverridesTheRoomTypeBasePrice() throws Exception {
        InMemoryManagerRoomDao rooms = new InMemoryManagerRoomDao();
        ManagerRoomServiceImpl service = new ManagerRoomServiceImpl(rooms, CLOCK);
        long roomTypeId = service.createRoomType(validRoomType());
        LocalDate date = LocalDate.of(2026, 8, 20);

        service.setRoomRateRange(new RoomRateForm(roomTypeId, date, date,
                new BigDecimal("2200000"), false));

        ResolvedRoomRate resolved = service.resolveRoomRate(roomTypeId, date);
        assertEquals(new BigDecimal("2200000"), resolved.nightlyPrice());
        assertTrue(resolved.dailyOverride());
        assertFalse(resolved.stopSell());
    }

    @Test
    void existingDailyRatesAreOverwrittenAcrossTheSelectedRange() throws Exception {
        InMemoryManagerRoomDao rooms = new InMemoryManagerRoomDao();
        ManagerRoomServiceImpl service = new ManagerRoomServiceImpl(rooms, CLOCK);
        long roomTypeId = service.createRoomType(validRoomType());
        LocalDate start = LocalDate.of(2026, 8, 20);
        LocalDate end = start.plusDays(2);
        service.setRoomRateRange(new RoomRateForm(roomTypeId, start, end,
                new BigDecimal("1000000"), false));

        service.setRoomRateRange(new RoomRateForm(roomTypeId, start, end,
                new BigDecimal("2000000"), true));

        assertEquals(3, service.searchRoomRates(roomTypeId, start, end).size());
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            ResolvedRoomRate resolved = service.resolveRoomRate(roomTypeId, date);
            assertEquals(new BigDecimal("2000000"), resolved.nightlyPrice());
            assertTrue(resolved.stopSell());
        }
    }

    @Test
    void roomDetailsCanBeUpdatedWithoutChangingCleaningStatus() throws Exception {
        InMemoryManagerRoomDao rooms = new InMemoryManagerRoomDao();
        ManagerRoomServiceImpl service = new ManagerRoomServiceImpl(rooms, CLOCK);
        long roomTypeId = service.createRoomType(validRoomType());
        service.setRoomTypeActive(roomTypeId, true);
        long roomId = service.createRoom(new RoomForm(null, roomTypeId, "101", 1,
                "AVAILABLE", null, true, null));
        RoomView current = service.getRoom(roomId).orElseThrow();

        service.updateRoom(new RoomForm(roomId, roomTypeId, "101A", 2,
                "AVAILABLE", "Renumbered", true, current.updatedAt()));

        RoomView updated = service.getRoom(roomId).orElseThrow();
        assertEquals("101A", updated.roomNumber());
        assertEquals("CLEAN", updated.cleaningStatus());
    }

    @Test
    void deactivatingRoomTypeSuspendsFreeRoomsButKeepsOccupiedRooms() throws Exception {
        InMemoryManagerRoomDao rooms = new InMemoryManagerRoomDao();
        ManagerRoomServiceImpl service = new ManagerRoomServiceImpl(rooms, CLOCK);
        long roomTypeId = service.createRoomType(validRoomType());
        service.setRoomTypeActive(roomTypeId, true);
        long availableRoom = service.createRoom(new RoomForm(null, roomTypeId,
                "101", 1, "AVAILABLE", null, true, null));
        long occupiedRoom = service.createRoom(new RoomForm(null, roomTypeId,
                "102", 1, "OCCUPIED", null, true, null));

        service.setRoomTypeActive(roomTypeId, false);

        assertEquals("OUT_OF_SERVICE",
                service.getRoom(availableRoom).orElseThrow().operationalStatus());
        assertEquals("OCCUPIED",
                service.getRoom(occupiedRoom).orElseThrow().operationalStatus());
    }

    @Test
    void roomRatesArePaginatedAtRequestedPageSize() throws Exception {
        InMemoryManagerRoomDao rooms = new InMemoryManagerRoomDao();
        ManagerRoomServiceImpl service = new ManagerRoomServiceImpl(rooms, CLOCK);
        long roomTypeId = service.createRoomType(validRoomType());
        LocalDate start = LocalDate.of(2026, 10, 1);
        service.setRoomRateRange(new RoomRateForm(roomTypeId, start,
                start.plusDays(30), new BigDecimal("1200000"), false));

        var firstPage = service.searchRoomRates(roomTypeId, start,
                start.plusDays(30), 1, 25);
        var secondPage = service.searchRoomRates(roomTypeId, start,
                start.plusDays(30), 2, 25);

        assertEquals(25, firstPage.items().size());
        assertEquals(6, secondPage.items().size());
        assertEquals(31, firstPage.totalItems());
        assertEquals(2, firstPage.totalPages());
    }

    @Test
    void valuesLongerThanDatabaseColumnsAreRejectedBeforePersistence() throws Exception {
        InMemoryManagerRoomDao rooms = new InMemoryManagerRoomDao();
        ManagerRoomServiceImpl service = new ManagerRoomServiceImpl(rooms, CLOCK);
        RoomTypeForm tooLong = new RoomTypeForm(null, "X".repeat(21), "N".repeat(101),
                null, 2, 0, "B".repeat(51), null, BigDecimal.ZERO, List.of(), null);

        ValidationException typeError = assertThrows(ValidationException.class,
                () -> service.createRoomType(tooLong));

        assertTrue(typeError.getErrors().containsKey("typeCode"));
        assertTrue(typeError.getErrors().containsKey("typeName"));
        assertTrue(typeError.getErrors().containsKey("bedType"));
    }

    private RoomTypeForm validRoomType() {
        return new RoomTypeForm(null, "DLX", "Deluxe", "Large deluxe room",
                2, 1, "King", new BigDecimal("32.5"),
                new BigDecimal("1800000"), List.of("Wi-Fi", "Bathtub"), null);
    }

    private static final class InMemoryManagerRoomDao implements ManagerRoomDao {
        private final Map<Long, RoomTypeView> roomTypes = new HashMap<>();
        private final Map<Long, RoomView> physicalRooms = new HashMap<>();
        private final Map<String, RoomRateView> roomRates = new HashMap<>();
        private long nextId = 1;

        @Override
        public boolean roomTypeCodeExists(String typeCode, Long excludedId) {
            return roomTypes.values().stream().anyMatch(type -> type.typeCode().equals(typeCode)
                    && (excludedId == null || type.id() != excludedId));
        }

        @Override
        public long createRoomType(RoomTypeForm form, String amenitiesJson) {
            long id = nextId++;
            roomTypes.put(id, new RoomTypeView(id, form.typeCode(), form.typeName(),
                    form.description(), form.maxAdults(), form.maxChildren(), form.bedType(),
                    form.roomSizeM2(), form.basePrice(), form.amenities(), false,
                    LocalDateTime.now(CLOCK)));
            return id;
        }

        @Override
        public Optional<RoomTypeView> findRoomType(long id) {
            return Optional.ofNullable(roomTypes.get(id));
        }

        @Override
        public boolean setRoomTypeActive(long id, boolean active) {
            RoomTypeView current = roomTypes.get(id);
            if (current == null) {
                return false;
            }
            roomTypes.put(id, new RoomTypeView(current.id(), current.typeCode(),
                    current.typeName(), current.description(), current.maxAdults(),
                    current.maxChildren(), current.bedType(), current.roomSizeM2(),
                    current.basePrice(), current.amenities(), active,
                    LocalDateTime.now(CLOCK)));
            if (!active) {
                physicalRooms.replaceAll((roomId, room) ->
                        room.roomTypeId() == id
                                && !"OCCUPIED".equals(room.operationalStatus())
                                ? new RoomView(room.id(), room.roomTypeId(),
                                        room.typeCode(), room.typeName(),
                                        room.roomNumber(), room.floorNumber(),
                                        "OUT_OF_SERVICE", room.cleaningStatus(),
                                        room.notes(), room.active(),
                                        LocalDateTime.now(CLOCK).plusSeconds(1))
                                : room);
            }
            return true;
        }

        @Override
        public boolean updateRoomType(RoomTypeForm form, String amenitiesJson) {
            RoomTypeView current = roomTypes.get(form.id());
            if (current == null) {
                return false;
            }
            roomTypes.put(form.id(), new RoomTypeView(form.id(), form.typeCode(),
                    form.typeName(), form.description(), form.maxAdults(),
                    form.maxChildren(), form.bedType(), form.roomSizeM2(),
                    form.basePrice(), form.amenities(), current.active(),
                    LocalDateTime.now(CLOCK).plusSeconds(1)));
            return true;
        }

        @Override
        public boolean roomNumberExists(String roomNumber, Long excludedId) {
            return physicalRooms.values().stream().anyMatch(room ->
                    room.roomNumber().equals(roomNumber)
                            && (excludedId == null || room.id() != excludedId));
        }

        @Override
        public long createRoom(RoomForm form) {
            long id = nextId++;
            RoomTypeView type = roomTypes.get(form.roomTypeId());
            physicalRooms.put(id, new RoomView(id, form.roomTypeId(), type.typeCode(),
                    type.typeName(), form.roomNumber(), form.floorNumber(),
                    form.operationalStatus(), "CLEAN", form.notes(), true,
                    LocalDateTime.now(CLOCK)));
            return id;
        }

        @Override
        public Optional<RoomView> findRoom(long id) {
            return Optional.ofNullable(physicalRooms.get(id));
        }

        @Override
        public boolean changeRoomOperationalStatus(long id, String status, boolean active) {
            RoomView current = physicalRooms.get(id);
            if (current == null) {
                return false;
            }
            physicalRooms.put(id, new RoomView(current.id(), current.roomTypeId(),
                    current.typeCode(), current.typeName(), current.roomNumber(),
                    current.floorNumber(), status, current.cleaningStatus(), current.notes(),
                    active, LocalDateTime.now(CLOCK).plusSeconds(1)));
            return true;
        }

        @Override
        public void upsertRoomRates(List<RoomRateView> rates) {
            rates.forEach(rate -> roomRates.put(rate.roomTypeId() + ":" + rate.rateDate(), rate));
        }

        @Override
        public Optional<RoomRateView> findRoomRate(long roomTypeId, LocalDate rateDate) {
            return Optional.ofNullable(roomRates.get(roomTypeId + ":" + rateDate));
        }

        @Override
        public List<RoomRateView> searchRoomRates(long roomTypeId, LocalDate startDate,
                                                  LocalDate endDate) {
            return roomRates.values().stream()
                    .filter(rate -> roomTypeId == 0 || rate.roomTypeId() == roomTypeId)
                    .filter(rate -> startDate == null || !rate.rateDate().isBefore(startDate))
                    .filter(rate -> endDate == null || !rate.rateDate().isAfter(endDate))
                    .sorted(java.util.Comparator.comparing(RoomRateView::rateDate)
                            .thenComparing(RoomRateView::roomTypeName))
                    .toList();
        }

        @Override
        public boolean updateRoom(RoomForm form) {
            RoomView current = physicalRooms.get(form.id());
            if (current == null) {
                return false;
            }
            RoomTypeView type = roomTypes.get(form.roomTypeId());
            physicalRooms.put(form.id(), new RoomView(form.id(), form.roomTypeId(),
                    type.typeCode(), type.typeName(), form.roomNumber(), form.floorNumber(),
                    form.operationalStatus(), current.cleaningStatus(), form.notes(),
                    form.active(), LocalDateTime.now(CLOCK).plusSeconds(1)));
            return true;
        }
    }
}
