package com.hotel.service;

import com.hotel.dto.HousekeepingTaskDto;
import com.hotel.exception.BusinessException;
import java.util.List;

public interface HousekeepingService {
    List<HousekeepingTaskDto> getPendingTasks();
    List<HousekeepingTaskDto> getMyTasks(long staffId);
    void acceptTask(long taskId, long staffId) throws BusinessException;
    void startTask(long taskId, long staffId) throws BusinessException;
    void completeTask(long taskId, long staffId) throws BusinessException;
}