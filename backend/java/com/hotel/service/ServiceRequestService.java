package com.hotel.service;

import com.hotel.dto.ServiceRequestDto;
import com.hotel.exception.BusinessException;

import java.util.List;

public interface ServiceRequestService {
    List<ServiceRequestDto> getPendingRequests();

    List<ServiceRequestDto> getMyRequests(long staffId);

    void acceptRequest(long requestId, long staffId) throws BusinessException;

    void startRequest(long requestId, long staffId) throws BusinessException;

    void completeRequest(long requestId, long staffId) throws BusinessException;

    void cancelRequest(long requestId, long staffId, String cancellationReason)
            throws BusinessException;
}