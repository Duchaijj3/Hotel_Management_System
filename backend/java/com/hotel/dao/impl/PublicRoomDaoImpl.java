package com.hotel.dao.impl;

import com.hotel.dao.PublicRoomDao;
import com.hotel.dto.PageResult;
import com.hotel.dto.PublicRoomTypeDto;
import com.hotel.exception.DataAccessException;
import com.hotel.util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PublicRoomDaoImpl implements PublicRoomDao {

    @Override
    public List<PublicRoomTypeDto> searchRoomTypes(
            String keyword,
            LocalDate checkIn,
            LocalDate checkOut,
            Integer guests,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            List<String> typeCodes,
            List<String> amenities) {
        PageResult<PublicRoomTypeDto> pageResult = searchRoomTypes(
                keyword, checkIn, checkOut, guests, minPrice, maxPrice, typeCodes, amenities, 1, 1000);
        return pageResult.getItems();
    }

    @Override
    public PageResult<PublicRoomTypeDto> searchRoomTypes(
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
        if (page < 1)
            page = 1;
        if (pageSize < 1)
            pageSize = 6;

        String whereClause = buildWhereClause(keyword, typeCodes, amenities);

        String countSql = "SELECT COUNT(1) FROM room_types rt WHERE rt.is_active = 1 " + whereClause;

        String dataSql = """
                    SELECT rt.room_type_id, rt.type_code, rt.type_name, rt.description,
                           rt.max_adults, rt.max_children, rt.bed_type, rt.room_size_m2,
                           rt.base_price,
                           (
                               SELECT COUNT(1)
                               FROM rooms rm
                               WHERE rm.room_type_id = rt.room_type_id
                                 AND rm.is_active = 1
                                 AND rm.operational_status = 'AVAILABLE'
                                 AND rm.cleaning_status IN ('CLEAN', 'INSPECTED')
                                 AND NOT EXISTS (
                                     SELECT 1
                                     FROM room_assignments ra
                                     JOIN reservation_rooms rr ON rr.reservation_room_id = ra.reservation_room_id
                                     JOIN reservations res ON res.reservation_id = rr.reservation_id
                                     WHERE ra.room_id = rm.room_id
                                       AND ra.is_current = 1
                                       AND res.status_code NOT IN ('CANCELLED', 'NO_SHOW', 'CHECKED_OUT')
                                       AND (? IS NOT NULL AND ? IS NOT NULL AND res.check_in_date < ? AND res.check_out_date > ?)
                                 )
                           ) AS available_count
                    FROM room_types rt
                    WHERE rt.is_active = 1
                """
                + whereClause + """
                            ORDER BY rt.base_price ASC
                            OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
                        """;

        try (Connection conn = DBConnection.getConnection()) {
            // 1. Count Total Items
            long totalItems = 0;
            try (PreparedStatement psCount = conn.prepareStatement(countSql)) {
                setCommonParams(psCount, 1, keyword, guests, minPrice, maxPrice, typeCodes, amenities);
                try (ResultSet rsCount = psCount.executeQuery()) {
                    if (rsCount.next()) {
                        totalItems = rsCount.getLong(1);
                    }
                }
            }

            if (totalItems == 0) {
                return new PageResult<>(List.of(), page, pageSize, 0);
            }

            // 2. Query Page Items
            Map<Long, RoomDataHolder> holderMap = new LinkedHashMap<>();
            try (PreparedStatement ps = conn.prepareStatement(dataSql)) {
                int paramIndex = 1;
                Date checkInSql = checkIn != null ? Date.valueOf(checkIn) : null;
                Date checkOutSql = checkOut != null ? Date.valueOf(checkOut) : null;

                // Subquery date parameters
                ps.setDate(paramIndex++, checkInSql);
                ps.setDate(paramIndex++, checkOutSql);
                ps.setDate(paramIndex++, checkOutSql);
                ps.setDate(paramIndex++, checkInSql);

                paramIndex = setCommonParams(ps, paramIndex, keyword, guests, minPrice, maxPrice, typeCodes, amenities);

                // Pagination params
                int offset = (page - 1) * pageSize;
                ps.setInt(paramIndex++, offset);
                ps.setInt(paramIndex++, pageSize);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        long id = rs.getLong("room_type_id");
                        RoomDataHolder holder = new RoomDataHolder(
                                id,
                                rs.getString("type_code"),
                                rs.getString("type_name"),
                                rs.getString("description"),
                                rs.getInt("max_adults"),
                                rs.getInt("max_children"),
                                rs.getString("bed_type"),
                                rs.getObject("room_size_m2") != null ? rs.getDouble("room_size_m2") : null,
                                rs.getBigDecimal("base_price"),
                                rs.getInt("available_count"));
                        holderMap.put(id, holder);
                    }
                }
            }

            if (holderMap.isEmpty()) {
                return new PageResult<>(List.of(), page, pageSize, totalItems);
            }

            // 3. Fetch images using CROSS APPLY OPENJSON for these room types
            String imagesSql = """
                        SELECT rt.room_type_id, img.value AS image_url
                        FROM room_types rt
                        CROSS APPLY OPENJSON(rt.images_json) AS img
                        WHERE rt.is_active = 1
                        ORDER BY rt.room_type_id, CAST(img.[key] AS INT)
                    """;
            try (PreparedStatement psImg = conn.prepareStatement(imagesSql);
                    ResultSet rsImg = psImg.executeQuery()) {
                while (rsImg.next()) {
                    long id = rsImg.getLong("room_type_id");
                    RoomDataHolder holder = holderMap.get(id);
                    if (holder != null) {
                        holder.images.add(rsImg.getString("image_url"));
                    }
                }
            }

            // 4. Fetch amenities using CROSS APPLY OPENJSON for these room types
            String amenitiesSql = """
                        SELECT rt.room_type_id, am.value AS amenity
                        FROM room_types rt
                        CROSS APPLY OPENJSON(rt.amenities_json) AS am
                        WHERE rt.is_active = 1
                        ORDER BY rt.room_type_id, CAST(am.[key] AS INT)
                    """;
            try (PreparedStatement psAm = conn.prepareStatement(amenitiesSql);
                    ResultSet rsAm = psAm.executeQuery()) {
                while (rsAm.next()) {
                    long id = rsAm.getLong("room_type_id");
                    RoomDataHolder holder = holderMap.get(id);
                    if (holder != null) {
                        holder.amenities.add(rsAm.getString("amenity"));
                    }
                }
            }

            // Build final list of DTOs
            List<PublicRoomTypeDto> items = new ArrayList<>();
            for (RoomDataHolder h : holderMap.values()) {
                items.add(new PublicRoomTypeDto(
                        h.roomTypeId, h.typeCode, h.typeName, h.description,
                        h.maxAdults, h.maxChildren, h.bedType, h.roomSizeM2,
                        h.basePrice, h.amenities, h.images, h.availableRoomsCount));
            }

            return new PageResult<>(items, page, pageSize, totalItems);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to search public room types with pagination", e);
        }
    }

    private String buildWhereClause(String keyword, List<String> typeCodes, List<String> amenities) {
        StringBuilder sb = new StringBuilder();

        if (keyword != null && !keyword.isBlank()) {
            sb.append(" AND (rt.type_name LIKE ? OR rt.description LIKE ?) ");
        }

        sb.append(" AND (? IS NULL OR (rt.max_adults + rt.max_children) >= ?) ");
        sb.append(" AND (? IS NULL OR rt.base_price >= ?) ");
        sb.append(" AND (? IS NULL OR rt.base_price <= ?) ");

        if (typeCodes != null && !typeCodes.isEmpty()) {
            sb.append(" AND rt.type_code IN (");
            for (int i = 0; i < typeCodes.size(); i++) {
                sb.append(i == 0 ? "?" : ", ?");
            }
            sb.append(")");
        }

        if (amenities != null && !amenities.isEmpty()) {
            for (int i = 0; i < amenities.size(); i++) {
                sb.append(" AND EXISTS (SELECT 1 FROM OPENJSON(rt.amenities_json) WHERE value = ?) ");
            }
        }

        return sb.toString();
    }

    private int setCommonParams(
            PreparedStatement ps,
            int startIndex,
            String keyword,
            Integer guests,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            List<String> typeCodes,
            List<String> amenities) throws SQLException {
        int paramIndex = startIndex;

        if (keyword != null && !keyword.isBlank()) {
            String kwParam = "%" + keyword.trim() + "%";
            ps.setString(paramIndex++, kwParam);
            ps.setString(paramIndex++, kwParam);
        }

        if (guests != null) {
            ps.setInt(paramIndex++, guests);
            ps.setInt(paramIndex++, guests);
        } else {
            ps.setNull(paramIndex++, java.sql.Types.INTEGER);
            ps.setNull(paramIndex++, java.sql.Types.INTEGER);
        }

        if (minPrice != null) {
            ps.setBigDecimal(paramIndex++, minPrice);
            ps.setBigDecimal(paramIndex++, minPrice);
        } else {
            ps.setNull(paramIndex++, java.sql.Types.DECIMAL);
            ps.setNull(paramIndex++, java.sql.Types.DECIMAL);
        }

        if (maxPrice != null) {
            ps.setBigDecimal(paramIndex++, maxPrice);
            ps.setBigDecimal(paramIndex++, maxPrice);
        } else {
            ps.setNull(paramIndex++, java.sql.Types.DECIMAL);
            ps.setNull(paramIndex++, java.sql.Types.DECIMAL);
        }

        if (typeCodes != null && !typeCodes.isEmpty()) {
            for (String code : typeCodes) {
                ps.setString(paramIndex++, code);
            }
        }

        if (amenities != null && !amenities.isEmpty()) {
            for (String am : amenities) {
                ps.setString(paramIndex++, am.trim());
            }
        }

        return paramIndex;
    }

    @Override
    public Optional<PublicRoomTypeDto> findById(long roomTypeId) {
        String baseSql = """
                    SELECT rt.room_type_id, rt.type_code, rt.type_name, rt.description,
                           rt.max_adults, rt.max_children, rt.bed_type, rt.room_size_m2,
                           rt.base_price,
                           (
                               SELECT COUNT(1)
                               FROM rooms rm
                               WHERE rm.room_type_id = rt.room_type_id
                                 AND rm.is_active = 1
                                 AND rm.operational_status = 'AVAILABLE'
                                 AND rm.cleaning_status IN ('CLEAN', 'INSPECTED')
                           ) AS available_count
                    FROM room_types rt
                    WHERE rt.room_type_id = ? AND rt.is_active = 1
                """;

        try (Connection conn = DBConnection.getConnection()) {
            RoomDataHolder holder = null;

            try (PreparedStatement ps = conn.prepareStatement(baseSql)) {
                ps.setLong(1, roomTypeId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        holder = new RoomDataHolder(
                                rs.getLong("room_type_id"),
                                rs.getString("type_code"),
                                rs.getString("type_name"),
                                rs.getString("description"),
                                rs.getInt("max_adults"),
                                rs.getInt("max_children"),
                                rs.getString("bed_type"),
                                rs.getObject("room_size_m2") != null ? rs.getDouble("room_size_m2") : null,
                                rs.getBigDecimal("base_price"),
                                rs.getInt("available_count"));
                    }
                }
            }

            if (holder == null) {
                return Optional.empty();
            }

            // 1. Fetch images for this room type with CROSS APPLY OPENJSON
            String imgSql = """
                        SELECT img.value AS image_url
                        FROM room_types rt
                        CROSS APPLY OPENJSON(rt.images_json) AS img
                        WHERE rt.room_type_id = ?
                        ORDER BY CAST(img.[key] AS INT)
                    """;
            try (PreparedStatement psImg = conn.prepareStatement(imgSql)) {
                psImg.setLong(1, roomTypeId);
                try (ResultSet rsImg = psImg.executeQuery()) {
                    while (rsImg.next()) {
                        holder.images.add(rsImg.getString("image_url"));
                    }
                }
            }

            // 2. Fetch amenities for this room type with CROSS APPLY OPENJSON
            String amSql = """
                        SELECT am.value AS amenity
                        FROM room_types rt
                        CROSS APPLY OPENJSON(rt.amenities_json) AS am
                        WHERE rt.room_type_id = ?
                        ORDER BY CAST(am.[key] AS INT)
                    """;
            try (PreparedStatement psAm = conn.prepareStatement(amSql)) {
                psAm.setLong(1, roomTypeId);
                try (ResultSet rsAm = psAm.executeQuery()) {
                    while (rsAm.next()) {
                        holder.amenities.add(rsAm.getString("amenity"));
                    }
                }
            }

            return Optional.of(new PublicRoomTypeDto(
                    holder.roomTypeId, holder.typeCode, holder.typeName, holder.description,
                    holder.maxAdults, holder.maxChildren, holder.bedType, holder.roomSizeM2,
                    holder.basePrice, holder.amenities, holder.images, holder.availableRoomsCount));
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find room type by id using OPENJSON: " + roomTypeId, e);
        }
    }

    private static class RoomDataHolder {
        long roomTypeId;
        String typeCode;
        String typeName;
        String description;
        int maxAdults;
        int maxChildren;
        String bedType;
        Double roomSizeM2;
        BigDecimal basePrice;
        int availableRoomsCount;
        List<String> amenities = new ArrayList<>();
        List<String> images = new ArrayList<>();

        RoomDataHolder(long roomTypeId, String typeCode, String typeName, String description,
                int maxAdults, int maxChildren, String bedType, Double roomSizeM2,
                BigDecimal basePrice, int availableRoomsCount) {
            this.roomTypeId = roomTypeId;
            this.typeCode = typeCode;
            this.typeName = typeName;
            this.description = description;
            this.maxAdults = maxAdults;
            this.maxChildren = maxChildren;
            this.bedType = bedType;
            this.roomSizeM2 = roomSizeM2;
            this.basePrice = basePrice;
            this.availableRoomsCount = availableRoomsCount;
        }
    }
}
