package com.hotel.dao;

import com.hotel.dto.EmailDeliveryDto;
import com.hotel.dto.EmailDeliverySearchCriteria;
import com.hotel.dto.PageResult;

import java.util.Optional;

public interface EmailDeliveryDao {
    PageResult<EmailDeliveryDto> search(EmailDeliverySearchCriteria criteria);

    Optional<EmailDeliveryDto> findById(long id);

    long queue(String recipientEmail, String subject, String eventCode, Long templateId);

    boolean markSent(long deliveryId);

    boolean markFailed(long deliveryId, String errorMessage);

    boolean incrementRetry(long deliveryId);
}
