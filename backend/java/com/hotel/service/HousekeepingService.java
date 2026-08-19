package com.hotel.service;

import com.hotel.dto.HousekeepingTaskDto;
import com.hotel.exception.BusinessException;

import java.util.List;

public interface HousekeepingService {

    List<HousekeepingTaskDto> getPendingTasks();

    List<HousekeepingTaskDto> getMyTasks(long staffId);

    /**
     * PENDING → IN_PROGRESS.
     * Gán nhân viên, ghi thời gian bắt đầu và chuyển phòng sang CLEANING.
     */
    void acceptTask(long taskId, long staffId) throws BusinessException;

    /**
     * IN_PROGRESS → COMPLETED.
     * Ghi thời gian hoàn thành và chuyển phòng sang CLEAN.
     */
    void completeTask(long taskId, long staffId) throws BusinessException;
}