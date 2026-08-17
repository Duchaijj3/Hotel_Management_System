package com.hotel.dao.impl;

import com.hotel.dao.ManagerRoomDao;
import com.hotel.dto.PageResult;
import com.hotel.dto.RoomForm;
import com.hotel.dto.RoomRateView;
import com.hotel.dto.RoomSearchCriteria;
import com.hotel.dto.RoomTypeForm;
import com.hotel.dto.RoomTypeSearchCriteria;
import com.hotel.dto.RoomTypeView;
import com.hotel.dto.RoomView;
import com.hotel.exception.DataAccessException;
import com.hotel.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ManagerRoomDaoImpl implements ManagerRoomDao {
    private static final String ROOM_TYPE_COLUMNS = """
            room_type_id, type_code, type_name, [description], max_adults, max_children,
            bed_type, room_size_m2, base_price, amenities_json, is_active, updated_at
            """;

    private static final String ROOM_COLUMNS = """
            rm.room_id, rm.room_type_id, rt.type_code, rt.type_name,
            rm.room_number, rm.floor_number, rm.operational_status,
            rm.cleaning_status, rm.notes, rm.is_active, rm.updated_at
            """;

    @Override
    public boolean roomTypeCodeExists(String typeCode, Long excludedId) {
        String sql = """
                SELECT 1 FROM dbo.room_types
                 WHERE UPPER(type_code) = UPPER(?)
                   AND (? IS NULL OR room_type_id <> ?)
                """;
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, typeCode);
            nullableLong(statement, 2, excludedId);
            nullableLong(statement, 3, excludedId);
            try (ResultSet rows = statement.executeQuery()) {
                return rows.next();
            }
        } catch (SQLException exception) {
            throw failure("Unable to check room type code", exception);
        }
    }

    @Override
    public long createRoomType(RoomTypeForm form, String amenitiesJson) {
        String sql = """
                INSERT INTO dbo.room_types
                    (type_code, type_name, [description], max_adults, max_children,
                     bed_type, room_size_m2, base_price, amenities_json, images_json, is_active)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, 0)
                """;
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {
            bindRoomType(statement, form, amenitiesJson);
            statement.executeUpdate();
            return generatedId(statement);
        } catch (SQLException exception) {
            throw failure("Unable to create room type", exception);
        }
    }

    @Override
    public Optional<RoomTypeView> findRoomType(long id) {
        String sql = "SELECT " + ROOM_TYPE_COLUMNS
                + " FROM dbo.room_types WHERE room_type_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet rows = statement.executeQuery()) {
                return rows.next() ? Optional.of(mapRoomType(rows)) : Optional.empty();
            }
        } catch (SQLException exception) {
            throw failure("Unable to load room type", exception);
        }
    }

    @Override
    public boolean setRoomTypeActive(long id, boolean active) {
        String updateTypeSql = """
                UPDATE dbo.room_types
                   SET is_active = ?, updated_at = SYSUTCDATETIME()
                 WHERE room_type_id = ?
                """;
        String suspendRoomsSql = """
                UPDATE dbo.rooms
                   SET operational_status = 'OUT_OF_SERVICE',
                       updated_at = SYSUTCDATETIME()
                 WHERE room_type_id = ?
                   AND operational_status <> 'OCCUPIED'
                """;
        try (Connection connection = DBConnection.getConnection()) {
            boolean autoCommit = connection.getAutoCommit();
            try {
                connection.setAutoCommit(false);
                int updated;
                try (PreparedStatement statement = connection.prepareStatement(updateTypeSql)) {
                    statement.setBoolean(1, active);
                    statement.setLong(2, id);
                    updated = statement.executeUpdate();
                }
                if (updated == 1 && !active) {
                    try (PreparedStatement statement = connection.prepareStatement(
                            suspendRoomsSql)) {
                        statement.setLong(1, id);
                        statement.executeUpdate();
                    }
                }
                connection.commit();
                return updated == 1;
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(autoCommit);
            }
        } catch (SQLException exception) {
            throw failure("Unable to change room type status", exception);
        }
    }

    @Override
    public boolean updateRoomType(RoomTypeForm form, String amenitiesJson) {
        String sql = """
                UPDATE dbo.room_types
                   SET type_code = ?, type_name = ?, [description] = ?,
                       max_adults = ?, max_children = ?, bed_type = ?, room_size_m2 = ?,
                       base_price = ?, amenities_json = ?, updated_at = SYSUTCDATETIME()
                 WHERE room_type_id = ? AND updated_at = ?
                """;
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bindRoomType(statement, form, amenitiesJson);
            statement.setLong(10, form.id());
            statement.setTimestamp(11, Timestamp.valueOf(form.version()));
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            throw failure("Unable to update room type", exception);
        }
    }

    @Override
    public boolean roomNumberExists(String roomNumber, Long excludedId) {
        String sql = """
                SELECT 1 FROM dbo.rooms
                 WHERE room_number = ?
                   AND (? IS NULL OR room_id <> ?)
                """;
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, roomNumber);
            nullableLong(statement, 2, excludedId);
            nullableLong(statement, 3, excludedId);
            try (ResultSet rows = statement.executeQuery()) {
                return rows.next();
            }
        } catch (SQLException exception) {
            throw failure("Unable to check room number", exception);
        }
    }

    @Override
    public long createRoom(RoomForm form) {
        String sql = """
                INSERT INTO dbo.rooms
                    (room_type_id, room_number, floor_number, operational_status,
                     cleaning_status, notes, is_active)
                VALUES (?, ?, ?, ?, 'CLEAN', ?, ?)
                """;
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {
            bindRoom(statement, form);
            statement.executeUpdate();
            return generatedId(statement);
        } catch (SQLException exception) {
            throw failure("Unable to create room", exception);
        }
    }

    @Override
    public Optional<RoomView> findRoom(long id) {
        String sql = "SELECT " + ROOM_COLUMNS + """
                  FROM dbo.rooms rm
                  JOIN dbo.room_types rt ON rt.room_type_id = rm.room_type_id
                 WHERE rm.room_id = ?
                """;
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet rows = statement.executeQuery()) {
                return rows.next() ? Optional.of(mapRoom(rows)) : Optional.empty();
            }
        } catch (SQLException exception) {
            throw failure("Unable to load room", exception);
        }
    }

    @Override
    public boolean updateRoom(RoomForm form) {
        String sql = """
                UPDATE dbo.rooms
                   SET room_type_id = ?, room_number = ?, floor_number = ?,
                       operational_status = ?, notes = ?, is_active = ?,
                       updated_at = SYSUTCDATETIME()
                 WHERE room_id = ? AND updated_at = ?
                """;
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bindRoom(statement, form);
            statement.setLong(7, form.id());
            statement.setTimestamp(8, Timestamp.valueOf(form.version()));
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            throw failure("Unable to update room", exception);
        }
    }

    @Override
    public boolean changeRoomOperationalStatus(long id, String status, boolean active) {
        String sql = """
                UPDATE dbo.rooms
                   SET operational_status = ?, is_active = ?,
                       updated_at = SYSUTCDATETIME()
                 WHERE room_id = ?
                """;
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            statement.setBoolean(2, active);
            statement.setLong(3, id);
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            throw failure("Unable to change room operational status", exception);
        }
    }

    @Override
    public void upsertRoomRates(List<RoomRateView> rates) {
        if (rates.isEmpty()) {
            return;
        }
        String sql = """
                MERGE dbo.room_rates WITH (HOLDLOCK) AS target
                USING (VALUES (?, ?, ?, ?)) AS source
                    (room_type_id, rate_date, nightly_price, stop_sell)
                   ON target.room_type_id = source.room_type_id
                  AND target.rate_date = source.rate_date
                WHEN MATCHED THEN
                    UPDATE SET nightly_price = source.nightly_price,
                               stop_sell = source.stop_sell
                WHEN NOT MATCHED THEN
                    INSERT (room_type_id, rate_date, nightly_price, stop_sell)
                    VALUES (source.room_type_id, source.rate_date,
                            source.nightly_price, source.stop_sell);
                """;
        try (Connection connection = DBConnection.getConnection()) {
            boolean autoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                for (RoomRateView rate : rates) {
                    statement.setLong(1, rate.roomTypeId());
                    statement.setDate(2, Date.valueOf(rate.rateDate()));
                    statement.setBigDecimal(3, rate.nightlyPrice());
                    statement.setBoolean(4, rate.stopSell());
                    statement.addBatch();
                }
                statement.executeBatch();
                connection.commit();
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(autoCommit);
            }
        } catch (SQLException exception) {
            throw failure("Unable to save room rates", exception);
        }
    }

    @Override
    public Optional<RoomRateView> findRoomRate(long roomTypeId, LocalDate rateDate) {
        String sql = """
                SELECT rate.room_rate_id, rate.room_type_id, type.type_name,
                       rate.rate_date, rate.nightly_price, rate.stop_sell
                  FROM dbo.room_rates rate
                  JOIN dbo.room_types type ON type.room_type_id = rate.room_type_id
                 WHERE rate.room_type_id = ? AND rate.rate_date = ?
                """;
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, roomTypeId);
            statement.setDate(2, Date.valueOf(rateDate));
            try (ResultSet rows = statement.executeQuery()) {
                return rows.next() ? Optional.of(mapRoomRate(rows)) : Optional.empty();
            }
        } catch (SQLException exception) {
            throw failure("Unable to load room rate", exception);
        }
    }

    @Override
    public PageResult<RoomTypeView> searchRoomTypes(RoomTypeSearchCriteria criteria) {
        String where = """
                 WHERE (? IS NULL OR type_code LIKE ? OR type_name LIKE ?)
                   AND (? IS NULL OR is_active = ?)
                """;
        String countSql = "SELECT COUNT(*) FROM dbo.room_types" + where;
        String dataSql = "SELECT " + ROOM_TYPE_COLUMNS + " FROM dbo.room_types" + where
                + " ORDER BY type_name, type_code OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection connection = DBConnection.getConnection()) {
            long total = countRoomTypes(connection, countSql, criteria);
            List<RoomTypeView> items = new ArrayList<>();
            try (PreparedStatement statement = connection.prepareStatement(dataSql)) {
                int next = bindRoomTypeSearch(statement, criteria);
                statement.setInt(next++, (criteria.page() - 1) * criteria.pageSize());
                statement.setInt(next, criteria.pageSize());
                try (ResultSet rows = statement.executeQuery()) {
                    while (rows.next()) {
                        items.add(mapRoomType(rows));
                    }
                }
            }
            return new PageResult<>(items, criteria.page(), criteria.pageSize(), total);
        } catch (SQLException exception) {
            throw failure("Unable to search room types", exception);
        }
    }

    @Override
    public PageResult<RoomView> searchRooms(RoomSearchCriteria criteria) {
        String where = """
                 WHERE (? IS NULL OR rm.room_number LIKE ? OR rt.type_name LIKE ?)
                   AND (? IS NULL OR rm.room_type_id = ?)
                   AND (? IS NULL OR rm.operational_status = ?)
                   AND (? IS NULL OR rm.is_active = ?)
                   AND (? IS NULL OR rm.floor_number = ?)
                """;
        String from = " FROM dbo.rooms rm JOIN dbo.room_types rt"
                + " ON rt.room_type_id = rm.room_type_id";
        String countSql = "SELECT COUNT(*)" + from + where;
        String dataSql = "SELECT " + ROOM_COLUMNS + from + where
                + " ORDER BY rm.floor_number, rm.room_number"
                + " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection connection = DBConnection.getConnection()) {
            long total = countRooms(connection, countSql, criteria);
            List<RoomView> items = new ArrayList<>();
            try (PreparedStatement statement = connection.prepareStatement(dataSql)) {
                int next = bindRoomSearch(statement, criteria);
                statement.setInt(next++, (criteria.page() - 1) * criteria.pageSize());
                statement.setInt(next, criteria.pageSize());
                try (ResultSet rows = statement.executeQuery()) {
                    while (rows.next()) {
                        items.add(mapRoom(rows));
                    }
                }
            }
            return new PageResult<>(items, criteria.page(), criteria.pageSize(), total);
        } catch (SQLException exception) {
            throw failure("Unable to search rooms", exception);
        }
    }

    @Override
    public List<RoomRateView> searchRoomRates(long roomTypeId, LocalDate startDate,
                                              LocalDate endDate) {
        String sql = """
                SELECT rate.room_rate_id, rate.room_type_id, type.type_name,
                       rate.rate_date, rate.nightly_price, rate.stop_sell
                  FROM dbo.room_rates rate
                  JOIN dbo.room_types type ON type.room_type_id = rate.room_type_id
                 WHERE (? = 0 OR rate.room_type_id = ?)
                   AND (? IS NULL OR rate.rate_date >= ?)
                   AND (? IS NULL OR rate.rate_date <= ?)
                 ORDER BY rate.rate_date, type.type_name
                """;
        List<RoomRateView> result = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bindRoomRateSearch(statement, roomTypeId, startDate, endDate);
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) {
                    result.add(mapRoomRate(rows));
                }
            }
            return result;
        } catch (SQLException exception) {
            throw failure("Unable to search room rates", exception);
        }
    }

    @Override
    public PageResult<RoomRateView> searchRoomRates(long roomTypeId,
                                                    LocalDate startDate,
                                                    LocalDate endDate,
                                                    int page, int pageSize) {
        int safePage = Math.max(1, page);
        int safePageSize = pageSize <= 0 ? 25 : Math.min(pageSize, 100);
        String where = """
                 WHERE (? = 0 OR rate.room_type_id = ?)
                   AND (? IS NULL OR rate.rate_date >= ?)
                   AND (? IS NULL OR rate.rate_date <= ?)
                """;
        String countSql = "SELECT COUNT(*) FROM dbo.room_rates rate" + where;
        String dataSql = """
                SELECT rate.room_rate_id, rate.room_type_id, type.type_name,
                       rate.rate_date, rate.nightly_price, rate.stop_sell
                  FROM dbo.room_rates rate
                  JOIN dbo.room_types type ON type.room_type_id = rate.room_type_id
                """ + where + """
                 ORDER BY rate.rate_date, type.type_name, rate.room_rate_id
                 OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
                """;
        try (Connection connection = DBConnection.getConnection()) {
            long total;
            try (PreparedStatement statement = connection.prepareStatement(countSql)) {
                bindRoomRateSearch(statement, roomTypeId, startDate, endDate);
                try (ResultSet rows = statement.executeQuery()) {
                    rows.next();
                    total = rows.getLong(1);
                }
            }
            List<RoomRateView> items = new ArrayList<>();
            try (PreparedStatement statement = connection.prepareStatement(dataSql)) {
                int next = bindRoomRateSearch(statement, roomTypeId, startDate, endDate);
                statement.setInt(next++, (safePage - 1) * safePageSize);
                statement.setInt(next, safePageSize);
                try (ResultSet rows = statement.executeQuery()) {
                    while (rows.next()) {
                        items.add(mapRoomRate(rows));
                    }
                }
            }
            return new PageResult<>(items, safePage, safePageSize, total);
        } catch (SQLException exception) {
            throw failure("Unable to search room rates", exception);
        }
    }

    private int bindRoomRateSearch(PreparedStatement statement, long roomTypeId,
                                   LocalDate startDate, LocalDate endDate)
            throws SQLException {
        statement.setLong(1, roomTypeId);
        statement.setLong(2, roomTypeId);
        nullableDate(statement, 3, startDate);
        nullableDate(statement, 4, startDate);
        nullableDate(statement, 5, endDate);
        nullableDate(statement, 6, endDate);
        return 7;
    }

    private long countRoomTypes(Connection connection, String sql,
                                RoomTypeSearchCriteria criteria) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            bindRoomTypeSearch(statement, criteria);
            try (ResultSet rows = statement.executeQuery()) {
                rows.next();
                return rows.getLong(1);
            }
        }
    }

    private int bindRoomTypeSearch(PreparedStatement statement,
                                   RoomTypeSearchCriteria criteria) throws SQLException {
        String pattern = criteria.keyword() == null ? null : "%" + criteria.keyword() + "%";
        nullableString(statement, 1, criteria.keyword());
        nullableString(statement, 2, pattern);
        nullableString(statement, 3, pattern);
        nullableBoolean(statement, 4, criteria.active());
        nullableBoolean(statement, 5, criteria.active());
        return 6;
    }

    private long countRooms(Connection connection, String sql,
                            RoomSearchCriteria criteria) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            bindRoomSearch(statement, criteria);
            try (ResultSet rows = statement.executeQuery()) {
                rows.next();
                return rows.getLong(1);
            }
        }
    }

    private int bindRoomSearch(PreparedStatement statement, RoomSearchCriteria criteria)
            throws SQLException {
        String pattern = criteria.keyword() == null ? null : "%" + criteria.keyword() + "%";
        nullableString(statement, 1, criteria.keyword());
        nullableString(statement, 2, pattern);
        nullableString(statement, 3, pattern);
        nullableLong(statement, 4, criteria.roomTypeId());
        nullableLong(statement, 5, criteria.roomTypeId());
        nullableString(statement, 6, criteria.operationalStatus());
        nullableString(statement, 7, criteria.operationalStatus());
        nullableBoolean(statement, 8, criteria.active());
        nullableBoolean(statement, 9, criteria.active());
        nullableInteger(statement, 10, criteria.floorNumber());
        nullableInteger(statement, 11, criteria.floorNumber());
        return 12;
    }

    private void bindRoomType(PreparedStatement statement, RoomTypeForm form,
                              String amenitiesJson) throws SQLException {
        statement.setString(1, form.typeCode().trim().toUpperCase());
        statement.setString(2, form.typeName().trim());
        nullableString(statement, 3, form.description());
        statement.setInt(4, form.maxAdults());
        statement.setInt(5, form.maxChildren());
        nullableString(statement, 6, form.bedType());
        if (form.roomSizeM2() == null) {
            statement.setNull(7, Types.DECIMAL);
        } else {
            statement.setBigDecimal(7, form.roomSizeM2());
        }
        statement.setBigDecimal(8, form.basePrice());
        statement.setString(9, amenitiesJson);
    }

    private void bindRoom(PreparedStatement statement, RoomForm form) throws SQLException {
        statement.setLong(1, form.roomTypeId());
        statement.setString(2, form.roomNumber().trim());
        nullableInteger(statement, 3, form.floorNumber());
        statement.setString(4, form.operationalStatus());
        nullableString(statement, 5, form.notes());
        statement.setBoolean(6, form.active());
    }

    private RoomTypeView mapRoomType(ResultSet rows) throws SQLException {
        return new RoomTypeView(rows.getLong("room_type_id"), rows.getString("type_code"),
                rows.getString("type_name"), rows.getString("description"),
                rows.getInt("max_adults"), rows.getInt("max_children"),
                rows.getString("bed_type"), rows.getBigDecimal("room_size_m2"),
                rows.getBigDecimal("base_price"),
                parseJsonArray(rows.getString("amenities_json")),
                rows.getBoolean("is_active"), rows.getTimestamp("updated_at").toLocalDateTime());
    }

    private RoomView mapRoom(ResultSet rows) throws SQLException {
        Number floor = (Number) rows.getObject("floor_number");
        return new RoomView(rows.getLong("room_id"), rows.getLong("room_type_id"),
                rows.getString("type_code"), rows.getString("type_name"),
                rows.getString("room_number"), floor == null ? null : floor.intValue(),
                rows.getString("operational_status"), rows.getString("cleaning_status"),
                rows.getString("notes"), rows.getBoolean("is_active"),
                rows.getTimestamp("updated_at").toLocalDateTime());
    }

    private RoomRateView mapRoomRate(ResultSet rows) throws SQLException {
        return new RoomRateView(rows.getLong("room_rate_id"),
                rows.getLong("room_type_id"), rows.getString("type_name"),
                rows.getDate("rate_date").toLocalDate(),
                rows.getBigDecimal("nightly_price"), rows.getBoolean("stop_sell"));
    }

    private List<String> parseJsonArray(String json) {
        if (json == null || json.isBlank() || "[]".equals(json.trim())) {
            return List.of();
        }
        List<String> values = new ArrayList<>();
        StringBuilder value = new StringBuilder();
        boolean quoted = false;
        boolean escaped = false;
        for (int index = 0; index < json.length(); index++) {
            char character = json.charAt(index);
            if (escaped) {
                value.append(character);
                escaped = false;
            } else if (character == '\\' && quoted) {
                escaped = true;
            } else if (character == '"') {
                if (quoted) {
                    values.add(value.toString());
                    value.setLength(0);
                }
                quoted = !quoted;
            } else if (quoted) {
                value.append(character);
            }
        }
        return List.copyOf(values);
    }

    private long generatedId(PreparedStatement statement) throws SQLException {
        try (ResultSet keys = statement.getGeneratedKeys()) {
            if (!keys.next()) {
                throw new SQLException("Insert did not return a generated key");
            }
            return keys.getLong(1);
        }
    }

    private void nullableLong(PreparedStatement statement, int index, Long value)
            throws SQLException {
        if (value == null) statement.setNull(index, Types.BIGINT);
        else statement.setLong(index, value);
    }

    private void nullableInteger(PreparedStatement statement, int index, Integer value)
            throws SQLException {
        if (value == null) statement.setNull(index, Types.INTEGER);
        else statement.setInt(index, value);
    }

    private void nullableString(PreparedStatement statement, int index, String value)
            throws SQLException {
        if (value == null || value.isBlank()) statement.setNull(index, Types.NVARCHAR);
        else statement.setString(index, value.trim());
    }

    private void nullableBoolean(PreparedStatement statement, int index, Boolean value)
            throws SQLException {
        if (value == null) statement.setNull(index, Types.BIT);
        else statement.setBoolean(index, value);
    }

    private void nullableDate(PreparedStatement statement, int index, LocalDate value)
            throws SQLException {
        if (value == null) statement.setNull(index, Types.DATE);
        else statement.setDate(index, Date.valueOf(value));
    }

    private DataAccessException failure(String message, SQLException exception) {
        return new DataAccessException(message, exception);
    }
}
