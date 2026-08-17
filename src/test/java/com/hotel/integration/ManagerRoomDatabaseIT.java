package com.hotel.integration;

import com.hotel.dao.impl.ManagerRoomDaoImpl;
import com.hotel.dao.impl.FrontDeskDaoImpl;
import com.hotel.dto.RoomForm;
import com.hotel.dto.RoomRateForm;
import com.hotel.dto.RoomTypeForm;
import com.hotel.dto.RoomSearchCriteria;
import com.hotel.dto.RoomTypeSearchCriteria;
import com.hotel.service.ManagerRoomService;
import com.hotel.service.impl.ManagerRoomServiceImpl;
import com.hotel.util.DBConnection;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ManagerRoomDatabaseIT {
    @Test
    void managerRoomWorkflowPersistsThroughSqlServer() throws Exception {
        String suffix = Long.toString(System.nanoTime(), 36).toUpperCase();
        String typeCode = "IT" + suffix.substring(Math.max(0, suffix.length() - 8));
        String roomNumber = "IT-" + suffix;
        ManagerRoomService service = new ManagerRoomServiceImpl(new ManagerRoomDaoImpl());
        long roomTypeId = 0;
        long roomId = 0;
        long occupiedRoomId = 0;
        try {
            roomTypeId = service.createRoomType(new RoomTypeForm(null, typeCode,
                    "Integration Test Type", "Temporary test data", 2, 1, "King",
                    new BigDecimal("30"), new BigDecimal("1000000"),
                    List.of("Wi-Fi"), null));
            assertFalse(service.getRoomType(roomTypeId).orElseThrow().active());
            assertEquals(1, service.searchRoomTypes(
                    new RoomTypeSearchCriteria(typeCode, false, 1, 20)).totalItems());

            service.setRoomTypeActive(roomTypeId, true);
            assertTrue(service.getRoomType(roomTypeId).orElseThrow().active());

            roomId = service.createRoom(new RoomForm(null, roomTypeId, roomNumber, 99,
                    "AVAILABLE", "Temporary integration test", true, null));
            occupiedRoomId = service.createRoom(new RoomForm(null, roomTypeId,
                    roomNumber + "-O", 99, "OCCUPIED",
                    "Temporary occupied integration test", true, null));
            assertEquals(1, service.searchRooms(new RoomSearchCriteria(roomNumber,
                    roomTypeId, "AVAILABLE", true, 99, 1, 20)).totalItems());

            service.setRoomTypeActive(roomTypeId, false);
            assertEquals("OUT_OF_SERVICE",
                    service.getRoom(roomId).orElseThrow().operationalStatus());
            assertEquals("OCCUPIED",
                    service.getRoom(occupiedRoomId).orElseThrow().operationalStatus());

            try (Connection connection = DBConnection.getConnection()) {
                assertEquals(1, new FrontDeskDaoImpl().updateRoomStatus(
                        connection, occupiedRoomId, "AVAILABLE", "DIRTY"));
            }
            assertEquals("OUT_OF_SERVICE",
                    service.getRoom(occupiedRoomId).orElseThrow().operationalStatus());

            service.setRoomTypeActive(roomTypeId, true);
            service.changeRoomOperationalStatus(roomId, "OUT_OF_SERVICE", false);
            assertEquals("OUT_OF_SERVICE",
                    service.getRoom(roomId).orElseThrow().operationalStatus());

            LocalDate rateDate = LocalDate.now().plusDays(20);
            service.setRoomRateRange(new RoomRateForm(roomTypeId, rateDate, rateDate,
                    new BigDecimal("1250000"), true));
            assertTrue(service.resolveRoomRate(roomTypeId, rateDate).stopSell());
            assertEquals(new BigDecimal("1250000.00"),
                    service.resolveRoomRate(roomTypeId, rateDate).nightlyPrice());
            assertEquals(1, service.searchRoomRates(roomTypeId, rateDate, rateDate).size());

            service.setRoomRateRange(new RoomRateForm(roomTypeId, rateDate, rateDate,
                    new BigDecimal("1350000"), false));
            assertFalse(service.resolveRoomRate(roomTypeId, rateDate).stopSell());
            assertEquals(new BigDecimal("1350000.00"),
                    service.resolveRoomRate(roomTypeId, rateDate).nightlyPrice());
            assertEquals(1, service.searchRoomRates(roomTypeId, rateDate, rateDate).size());
        } finally {
            cleanup(List.of(roomId, occupiedRoomId), roomTypeId);
        }
    }

    private void cleanup(List<Long> roomIds, long roomTypeId) throws Exception {
        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                delete(connection, "DELETE FROM dbo.room_rates WHERE room_type_id = ?",
                        roomTypeId);
                for (long roomId : roomIds) {
                    delete(connection, "DELETE FROM dbo.rooms WHERE room_id = ?", roomId);
                }
                delete(connection, "DELETE FROM dbo.room_types WHERE room_type_id = ?",
                        roomTypeId);
                connection.commit();
            } catch (Exception exception) {
                connection.rollback();
                throw exception;
            }
        }
    }

    private void delete(Connection connection, String sql, long id) throws Exception {
        if (id <= 0) return;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }
}
