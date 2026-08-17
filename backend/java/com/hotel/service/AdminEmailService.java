package com.hotel.service;

import com.hotel.dto.EmailDeliveryDto;
import com.hotel.dto.EmailDeliverySearchCriteria;
import com.hotel.dto.EmailTemplateDetailDto;
import com.hotel.dto.EmailTemplateForm;
import com.hotel.dto.EmailTemplateSearchCriteria;
import com.hotel.dto.EmailTemplateSummaryDto;
import com.hotel.dto.PageResult;
import com.hotel.exception.ValidationException;

import java.util.Map;
import java.util.Optional;

public interface AdminEmailService {
    PageResult<EmailTemplateSummaryDto> searchTemplates(EmailTemplateSearchCriteria criteria);

    Optional<EmailTemplateDetailDto> templateDetail(long id);

    long createTemplate(EmailTemplateForm form, long actorId) throws ValidationException;

    void updateTemplate(EmailTemplateForm form, long actorId) throws ValidationException;

    void setTemplateActive(long id, boolean active, long actorId) throws ValidationException;

    PageResult<EmailDeliveryDto> searchDeliveries(EmailDeliverySearchCriteria criteria);

    void retryDelivery(long deliveryId) throws ValidationException;

    void sendTemplatedEmail(String eventCode, String recipientEmail,
                            Map<String, String> placeholders);
}
