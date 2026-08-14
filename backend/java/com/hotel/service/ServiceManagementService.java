package com.hotel.service;

//  File: src/main/java/com/hotel/service/ServiceManagementService.java


import com.hotel.model.HotelService;
import java.math.BigDecimal;
import java.util.List;

/**
 * Service interface for Hotel Service Management
 */
public interface ServiceManagementService {

    void addService(String serviceCode, String serviceName, String unitName,
                    BigDecimal unitPrice, String description) throws Exception;

    void updateService(long serviceId, String serviceName, String unitName,
                       BigDecimal unitPrice, String description) throws Exception;

    HotelService getServiceById(long serviceId) throws Exception;

    HotelService getServiceByCode(String serviceCode) throws Exception;

    List<HotelService> getAllActiveServices() throws Exception;

    List<HotelService> getAllServices() throws Exception;

    void deleteService(long serviceId) throws Exception;
}
