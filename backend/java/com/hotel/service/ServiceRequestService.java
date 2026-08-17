package com.hotel.service;

import com.hotel.dto.ServiceRequestDto;
import com.hotel.exception.BusinessException;
import java.util.List;

public interface ServiceRequestService {
    List<ServiceRequestDto> getPendingRequests();
    List<ServiceRequestDto> getMyTasks(long staffId);
    void cancelTask(long requestId, long staffId, String reason) throws BusinessException;
    void acceptTask(long requestId, long staffId) throws BusinessException;
    void startTask(long requestId, long staffId) throws BusinessException;
    void completeTask(long requestId, long staffId) throws BusinessException;
}