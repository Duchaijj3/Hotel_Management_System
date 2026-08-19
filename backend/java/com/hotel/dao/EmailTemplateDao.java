package com.hotel.dao;

import com.hotel.dto.EmailTemplateDetailDto;
import com.hotel.dto.EmailTemplateForm;
import com.hotel.dto.EmailTemplateSearchCriteria;
import com.hotel.dto.EmailTemplateSummaryDto;
import com.hotel.dto.PageResult;

import java.util.Optional;

public interface EmailTemplateDao {
    PageResult<EmailTemplateSummaryDto> search(EmailTemplateSearchCriteria criteria);

    Optional<EmailTemplateDetailDto> findById(long id);

    Optional<EmailTemplateDetailDto> findByEventCode(String eventCode);

    boolean codeExists(String templateCode, Long excludeId);

    long create(EmailTemplateForm form, long actorId);

    boolean update(EmailTemplateForm form, long actorId);

    boolean setActive(long id, boolean active, long actorId);

    boolean delete(long id);
}
