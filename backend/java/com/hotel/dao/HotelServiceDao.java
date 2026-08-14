package com.hotel.dao;
// File: src/main/java/com/hotel/dao/ServiceDao.java


import com.hotel.model.HotelService;
import java.util.List;

/**
 * DAO interface for HotelService entity
 */
public interface HotelServiceDao {
    void addService(HotelService service) throws Exception;
    void updateService(HotelService service) throws Exception;
    HotelService getServiceById(long serviceId) throws Exception;
    HotelService getServiceByCode(String serviceCode) throws Exception;
    List<HotelService> getAllActiveServices() throws Exception;
    List<HotelService> getAllServices() throws Exception;
    void deleteService(long serviceId) throws Exception; // Soft delete (set is_active = 0)
}
