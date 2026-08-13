package com.hotel.service.impl;

// File: src/main/java/com/hotel/service/impl/ServiceManagementServiceImpl.java

import com.hotel.dao.HotelServiceDao;
import com.hotel.dao.impl.HotelServiceDaoImpl;
import com.hotel.model.HotelService;
import com.hotel.service.ServiceManagementService;

import java.math.BigDecimal;
import java.util.List;

/**
 * Implementation of ServiceManagementService
 */
public class ServiceManagementServiceImpl implements ServiceManagementService {

    private final HotelServiceDao hotelServiceDao;

    public ServiceManagementServiceImpl() {
        this.hotelServiceDao = new HotelServiceDaoImpl();
    }

    public ServiceManagementServiceImpl(HotelServiceDao hotelServiceDao) {
        this.hotelServiceDao = hotelServiceDao;
    }

    @Override
    public void addService(String serviceCode, String serviceName, String unitName,
                           BigDecimal unitPrice, String description) throws Exception {

        if (serviceCode == null || serviceCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã dịch vụ không được để trống");
        }
        if (serviceName == null || serviceName.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên dịch vụ không được để trống");
        }
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Giá dịch vụ phải lớn hơn hoặc bằng 0");
        }

        String formattedCode = serviceCode.trim().toUpperCase();

        HotelService existing = hotelServiceDao.getServiceByCode(formattedCode);
        if (existing != null) {
            throw new IllegalArgumentException("Mã dịch vụ '" + formattedCode + "' đã tồn tại");
        }

        HotelService service = new HotelService();
        service.setServiceCode(formattedCode);
        service.setServiceName(serviceName.trim());
        service.setUnitName(unitName != null ? unitName.trim() : null);
        service.setUnitPrice(unitPrice);
        service.setDescription(description != null ? description.trim() : null);
        service.setActive(true);

        hotelServiceDao.addService(service);
    }

    @Override
    public void updateService(long serviceId, String serviceName, String unitName,
                              BigDecimal unitPrice, String description) throws Exception {

        HotelService existingService = hotelServiceDao.getServiceById(serviceId);
        if (existingService == null) {
            throw new IllegalArgumentException("Không tìm thấy dịch vụ với ID: " + serviceId);
        }

        if (serviceName == null || serviceName.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên dịch vụ không được để trống");
        }
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Giá dịch vụ phải lớn hơn hoặc bằng 0");
        }

        existingService.setServiceName(serviceName.trim());
        existingService.setUnitName(unitName != null ? unitName.trim() : null);
        existingService.setUnitPrice(unitPrice);
        existingService.setDescription(description != null ? description.trim() : null);

        hotelServiceDao.updateService(existingService);
    }

    @Override
    public HotelService getServiceById(long serviceId) throws Exception {
        HotelService service = hotelServiceDao.getServiceById(serviceId);
        if (service == null) {
            throw new IllegalArgumentException("Không tìm thấy dịch vụ với ID: " + serviceId);
        }
        return service;
    }

    @Override
    public HotelService getServiceByCode(String serviceCode) throws Exception {
        if (serviceCode == null || serviceCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã dịch vụ không được để trống");
        }
        return hotelServiceDao.getServiceByCode(serviceCode.trim().toUpperCase());
    }

    @Override
    public List<HotelService> getAllActiveServices() throws Exception {
        return hotelServiceDao.getAllActiveServices();
    }

    @Override
    public List<HotelService> getAllServices() throws Exception {
        return hotelServiceDao.getAllServices();
    }

    @Override
    public void deleteService(long serviceId) throws Exception {
        HotelService service = hotelServiceDao.getServiceById(serviceId);
        if (service == null) {
            throw new IllegalArgumentException("Không tìm thấy dịch vụ để xóa");
        }
        hotelServiceDao.deleteService(serviceId);
    }
}
