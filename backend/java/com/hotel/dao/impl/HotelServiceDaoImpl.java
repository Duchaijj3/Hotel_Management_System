package java.com.hotel.dao.impl;

// File: src/main/java/com/hotel/dao/impl/HotelServiceDaoImpl.java

import java.com.hotel.dao.HotelServiceDao;
import java.com.hotel.model.HotelService;
import java.com.hotel.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation of HotelServiceDao interface
 */
public class HotelServiceDaoImpl implements HotelServiceDao {

    // Create new hotel service
    @Override
    public void addService(HotelService service) throws Exception {
        String sql = "INSERT INTO dbo.hotel_services (service_code, service_name, description, unit_name, unit_price, is_active) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, service.getServiceCode());
            stmt.setString(2, service.getServiceName());
            stmt.setString(3, service.getDescription());
            stmt.setString(4, service.getUnitName());
            stmt.setBigDecimal(5, service.getUnitPrice());
            stmt.setBoolean(6, service.isActive());

            stmt.executeUpdate();
        }
    }

    // Update existing hotel service
    @Override
    public void updateService(HotelService service) throws Exception {
        String sql = "UPDATE dbo.hotel_services " +
                "SET service_code = ?, service_name = ?, description = ?, unit_name = ?, unit_price = ?, is_active = ?, updated_at = SYSUTCDATETIME() " +
                "WHERE hotel_service_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, service.getServiceCode());
            stmt.setString(2, service.getServiceName());
            stmt.setString(3, service.getDescription());
            stmt.setString(4, service.getUnitName());
            stmt.setBigDecimal(5, service.getUnitPrice());
            stmt.setBoolean(6, service.isActive());
            stmt.setLong(7, service.getHotelServiceId());

            stmt.executeUpdate();
        }
    }

    // Get service by ID
    @Override
    public HotelService getServiceById(long serviceId) throws Exception {
        String sql = "SELECT hotel_service_id, service_code, service_name, description, unit_name, unit_price, is_active, created_at, updated_at " +
                "FROM dbo.hotel_services WHERE hotel_service_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, serviceId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToHotelService(rs);
                }
            }
        }
        return null;
    }

    // Get service by Code (e.g. LAUNDRY, SPA_MASSAGE)
    @Override
    public HotelService getServiceByCode(String serviceCode) throws Exception {
        String sql = "SELECT hotel_service_id, service_code, service_name, description, unit_name, unit_price, is_active, created_at, updated_at " +
                "FROM dbo.hotel_services WHERE service_code = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, serviceCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToHotelService(rs);
                }
            }
        }
        return null;
    }

    // Get all active services (for Customer/Service Staff viewing available services)
    @Override
    public List<HotelService> getAllActiveServices() throws Exception {
        String sql = "SELECT hotel_service_id, service_code, service_name, description, unit_name, unit_price, is_active, created_at, updated_at " +
                "FROM dbo.hotel_services WHERE is_active = 1 ORDER BY service_name";
        List<HotelService> services = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                services.add(mapRowToHotelService(rs));
            }
        }
        return services;
    }

    // Get all services including inactive ones (for Manager/Admin management)
    @Override
    public List<HotelService> getAllServices() throws Exception {
        String sql = "SELECT hotel_service_id, service_code, service_name, description, unit_name, unit_price, is_active, created_at, updated_at " +
                "FROM dbo.hotel_services ORDER BY service_name";
        List<HotelService> services = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                services.add(mapRowToHotelService(rs));
            }
        }
        return services;
    }

    // Soft delete: deactivate service to prevent FK conflict with service_requests
    @Override
    public void deleteService(long serviceId) throws Exception {
        String sql = "UPDATE dbo.hotel_services SET is_active = 0, updated_at = SYSUTCDATETIME() WHERE hotel_service_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, serviceId);
            stmt.executeUpdate();
        }
    }

    // Helper method to map ResultSet to HotelService object
    private HotelService mapRowToHotelService(ResultSet rs) throws SQLException {
        HotelService service = new HotelService();
        service.setHotelServiceId(rs.getLong("hotel_service_id"));
        service.setServiceCode(rs.getString("service_code"));
        service.setServiceName(rs.getString("service_name"));
        service.setDescription(rs.getString("description"));
        service.setUnitName(rs.getString("unit_name"));
        service.setUnitPrice(rs.getBigDecimal("unit_price"));
        service.setActive(rs.getBoolean("is_active"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            service.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            service.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return service;
    }
}