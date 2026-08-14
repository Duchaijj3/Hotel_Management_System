package com.hotel.dao.impl;

import com.hotel.dao.RoomDao;
import com.hotel.dto.AvailableRoomDto;
import com.hotel.exception.DataAccessException;
import com.hotel.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RoomDaoImpl implements RoomDao {
    private static final String TARGET_SQL = """
            SELECT rr.room_type_id, rr.adult_count, rr.child_count,
                   v.check_in_date, v.check_out_date
            FROM reservation_rooms rr
            JOIN reservations v ON v.reservation_id = rr.reservation_id
            WHERE rr.reservation_room_id = ?
            """;

    private static final String AVAILABLE_SQL = """
            SELECT rm.room_id, rm.room_number, rm.floor_number,
                   rt.type_code, rt.type_name, rt.bed_type,
                   rt.max_adults, rt.max_children,
                   rm.operational_status, rm.cleaning_status
            FROM rooms rm
            JOIN room_types rt ON rt.room_type_id = rm.room_type_id
            WHERE rm.is_active = 1
              AND rt.is_active = 1
              AND rm.operational_status = 'AVAILABLE'
              AND rm.cleaning_status IN ('CLEAN', 'INSPECTED')
              AND rm.room_type_id = ?
              AND rt.max_adults >= ?
              AND rt.max_children >= ?
              AND (? IS NULL OR rm.floor_number = ?)
              AND NOT EXISTS (
                  SELECT 1
                  FROM room_assignments ra
                  JOIN reservation_rooms xrr
                    ON xrr.reservation_room_id = ra.reservation_room_id
                  JOIN reservations xv ON xv.reservation_id = xrr.reservation_id
                  WHERE ra.room_id = rm.room_id
                    AND ra.is_current = 1
                    AND xv.status_code NOT IN ('CANCELLED', 'NO_SHOW', 'CHECKED_OUT')
                    AND xv.check_in_date < ?
                    AND xv.check_out_date > ?
              )
            ORDER BY rm.floor_number ASC, rm.room_number ASC
            """;

    @Override
    public Optional<List<AvailableRoomDto>> findAvailable(long reservationRoomId, Integer floor) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement targetStatement = connection.prepareStatement(TARGET_SQL)) {
            targetStatement.setLong(1, reservationRoomId);

            long roomTypeId;
            int adults;
            int children;
            LocalDate checkIn;
            LocalDate checkOut;
            try (ResultSet result = targetStatement.executeQuery()) {
                if (!result.next()) {
                    return Optional.empty();
                }
                roomTypeId = result.getLong(1);
                adults = result.getInt(2);
                children = result.getInt(3);
                checkIn = result.getObject(4, LocalDate.class);
                checkOut = result.getObject(5, LocalDate.class);
            }

            List<AvailableRoomDto> rooms = new ArrayList<>();
            try (PreparedStatement statement = connection.prepareStatement(AVAILABLE_SQL)) {
                statement.setLong(1, roomTypeId);
                statement.setInt(2, adults);
                statement.setInt(3, children);
                if (floor == null) {
                    statement.setNull(4, Types.INTEGER);
                    statement.setNull(5, Types.INTEGER);
                } else {
                    statement.setInt(4, floor);
                    statement.setInt(5, floor);
                }
                statement.setDate(6, java.sql.Date.valueOf(checkOut));
                statement.setDate(7, java.sql.Date.valueOf(checkIn));

                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        Number floorValue = (Number) result.getObject(3);
                        rooms.add(new AvailableRoomDto(
                                result.getLong(1), result.getString(2),
                                floorValue == null ? null : floorValue.intValue(),
                                result.getString(4), result.getString(5), result.getString(6),
                                result.getInt(7), result.getInt(8), result.getString(9),
                                result.getString(10), checkIn, checkOut));
                    }
                }
            }
            return Optional.of(rooms);
        } catch (SQLException exception) {
            throw new DataAccessException("Unable to find available rooms", exception);
        }
    }
}
