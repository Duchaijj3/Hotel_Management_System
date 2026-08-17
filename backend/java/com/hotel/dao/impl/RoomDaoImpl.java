package com.hotel.dao.impl;

import com.hotel.dao.RoomDao;
import com.hotel.dto.AvailableRoomDto;
import com.hotel.dto.RoomTypeOptionDto;
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

public class RoomDaoImpl implements RoomDao {
    @Override
    public List<RoomTypeOptionDto> findActiveRoomTypes() {
        String sql = """
                SELECT room_type_id, type_code, type_name, max_adults, max_children
                  FROM room_types
                 WHERE is_active = 1
                 ORDER BY type_name
                """;
        List<RoomTypeOptionDto> result = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rows = statement.executeQuery()) {
            while (rows.next()) {
                result.add(new RoomTypeOptionDto(rows.getLong(1), rows.getString(2),
                        rows.getString(3), rows.getInt(4), rows.getInt(5)));
            }
            return result;
        } catch (SQLException exception) {
            throw new DataAccessException("Unable to load room types", exception);
        }
    }

    @Override
    public List<AvailableRoomDto> searchAvailable(LocalDate checkIn, LocalDate checkOut,
                                                   Long typeId, int adults, int children,
                                                   Integer floor) {
        String sql = """
                SELECT rm.room_id, rm.room_number, rm.floor_number,
                       rt.type_code, rt.type_name, rt.bed_type,
                       rt.max_adults, rt.max_children,
                       rm.operational_status, rm.cleaning_status
                  FROM rooms rm
                  JOIN room_types rt ON rt.room_type_id = rm.room_type_id
                 WHERE rm.is_active = 1
                   AND rt.is_active = 1
                   AND rm.operational_status = 'AVAILABLE'
                   AND rm.cleaning_status = 'INSPECTED'
                   AND (? IS NULL OR rm.room_type_id = ?)
                   AND rt.max_adults >= ?
                   AND rt.max_children >= ?
                   AND (? IS NULL OR rm.floor_number = ?)
                   AND NOT EXISTS (
                       SELECT 1
                         FROM room_rates rate
                        WHERE rate.room_type_id = rm.room_type_id
                          AND rate.rate_date >= ?
                          AND rate.rate_date < ?
                          AND rate.stop_sell = 1
                   )
                   AND NOT EXISTS (
                       SELECT 1
                         FROM room_assignments assignment
                         JOIN reservation_rooms reserved
                           ON reserved.reservation_room_id = assignment.reservation_room_id
                         JOIN reservations reservation
                           ON reservation.reservation_id = reserved.reservation_id
                        WHERE assignment.room_id = rm.room_id
                          AND assignment.is_current = 1
                          AND assignment.unassigned_at IS NULL
                          AND reservation.status_code NOT IN
                              ('CANCELLED', 'NO_SHOW', 'CHECKED_OUT')
                          AND reservation.check_in_date < ?
                          AND reservation.check_out_date > ?
                   )
                 ORDER BY rt.type_name, rm.floor_number, rm.room_number
                """;
        List<AvailableRoomDto> result = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            if (typeId == null) {
                statement.setNull(1, Types.BIGINT);
                statement.setNull(2, Types.BIGINT);
            } else {
                statement.setLong(1, typeId);
                statement.setLong(2, typeId);
            }
            statement.setInt(3, adults);
            statement.setInt(4, children);
            if (floor == null) {
                statement.setNull(5, Types.INTEGER);
                statement.setNull(6, Types.INTEGER);
            } else {
                statement.setInt(5, floor);
                statement.setInt(6, floor);
            }
            statement.setDate(7, java.sql.Date.valueOf(checkIn));
            statement.setDate(8, java.sql.Date.valueOf(checkOut));
            statement.setDate(9, java.sql.Date.valueOf(checkOut));
            statement.setDate(10, java.sql.Date.valueOf(checkIn));

            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) {
                    Number floorNumber = (Number) rows.getObject(3);
                    result.add(new AvailableRoomDto(rows.getLong(1), rows.getString(2),
                            floorNumber == null ? null : floorNumber.intValue(),
                            rows.getString(4), rows.getString(5), rows.getString(6),
                            rows.getInt(7), rows.getInt(8), rows.getString(9),
                            rows.getString(10), checkIn, checkOut));
                }
            }
            return result;
        } catch (SQLException exception) {
            throw new DataAccessException("Unable to find available rooms", exception);
        }
    }
}
