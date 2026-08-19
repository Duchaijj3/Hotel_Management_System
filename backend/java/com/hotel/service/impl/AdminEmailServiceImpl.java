package com.hotel.service.impl;

import com.hotel.dao.EmailDeliveryDao;
import com.hotel.dao.EmailTemplateDao;
import com.hotel.dto.EmailDeliveryDto;
import com.hotel.dto.EmailDeliverySearchCriteria;
import com.hotel.dto.EmailTemplateDetailDto;
import com.hotel.dto.EmailTemplateForm;
import com.hotel.dto.EmailTemplateSearchCriteria;
import com.hotel.dto.EmailTemplateSummaryDto;
import com.hotel.dto.PageResult;
import com.hotel.exception.ValidationException;
import com.hotel.service.AdminEmailService;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdminEmailServiceImpl implements AdminEmailService {
    private static final Set<String> EVENT_CODES = Set.of(
            "ACCOUNT_ACTIVATION", "PASSWORD_RESET", "ACCOUNT_VERIFICATION",
            "RESERVATION_CONFIRMED", "CHECK_IN_REMINDER");
    private static final Pattern PLACEHOLDER = Pattern.compile("\\{\\{([a-zA-Z0-9_]+)}}");

    private final EmailTemplateDao templates;
    private final EmailDeliveryDao deliveries;

    public AdminEmailServiceImpl(EmailTemplateDao templates, EmailDeliveryDao deliveries) {
        this.templates = templates;
        this.deliveries = deliveries;
    }

    @Override
    public PageResult<EmailTemplateSummaryDto> searchTemplates(
            EmailTemplateSearchCriteria criteria) {
        return templates.search(criteria);
    }

    @Override
    public Optional<EmailTemplateDetailDto> templateDetail(long id) {
        return id > 0 ? templates.findById(id) : Optional.empty();
    }

    @Override
    public long createTemplate(EmailTemplateForm form, long actorId) throws ValidationException {
        validateTemplateForm(form, false);
        if (templates.codeExists(form.templateCode(), null)) {
            throw new ValidationException(Map.of("templateCode", "Template code already exists."));
        }
        return templates.create(form, actorId);
    }

    @Override
    public void updateTemplate(EmailTemplateForm form, long actorId) throws ValidationException {
        validateTemplateForm(form, true);
        if (templates.codeExists(form.templateCode(), form.id())) {
            throw new ValidationException(Map.of("templateCode", "Template code already exists."));
        }
        if (form.id() == null || form.id() <= 0 || !templates.update(form, actorId)) {
            throw new ValidationException(Map.of("general", "Template not found."));
        }
    }

    @Override
    public void setTemplateActive(long id, boolean active, long actorId)
            throws ValidationException {
        if (id <= 0 || !templates.setActive(id, active, actorId)) {
            throw new ValidationException(Map.of("general", "Template not found."));
        }
    }

    @Override
    public void deleteTemplate(long id, long actorId) throws ValidationException {
        if (id <= 0 || !templates.delete(id)) {
            throw new ValidationException(Map.of("general", "Template not found."));
        }
    }

    @Override
    public PageResult<EmailDeliveryDto> searchDeliveries(EmailDeliverySearchCriteria criteria) {
        return deliveries.search(criteria);
    }

    @Override
    public void retryDelivery(long deliveryId) throws ValidationException {
        EmailDeliveryDto delivery = deliveries.findById(deliveryId).orElseThrow(() ->
                new ValidationException(Map.of("general", "Delivery not found.")));
        if (!"FAILED".equals(delivery.statusCode())) {
            throw new ValidationException(Map.of("general", "Only failed deliveries can be retried."));
        }
        deliveries.incrementRetry(deliveryId);
        dispatch(deliveryId, delivery.recipientEmail(), delivery.subject(),
                delivery.eventCode(), true);
    }

    @Override
    public void sendTemplatedEmail(String eventCode, String recipientEmail,
                                   Map<String, String> placeholders) {
        EmailTemplateDetailDto template = templates.findByEventCode(eventCode).orElse(null);
        String subject = template == null
                ? defaultSubject(eventCode)
                : render(template.subjectTemplate(), placeholders);
        long deliveryId = deliveries.queue(recipientEmail, subject, eventCode,
                template == null ? null : template.id());
        dispatch(deliveryId, recipientEmail, subject, eventCode, false);
    }

    private void dispatch(long deliveryId, String recipientEmail, String subject,
                          String eventCode, boolean retry) {
        try {
            if (recipientEmail == null || !recipientEmail.contains("@")) {
                throw new IllegalArgumentException("Invalid recipient email.");
            }
            deliveries.markSent(deliveryId);
        } catch (RuntimeException exception) {
            deliveries.markFailed(deliveryId, truncate(
                    (retry ? "Retry failed: " : "Send failed: ") + exception.getMessage()));
        }
    }

    private void validateTemplateForm(EmailTemplateForm form, boolean update)
            throws ValidationException {
        Map<String, String> errors = new LinkedHashMap<>();
        if (form.templateCode() == null || !form.templateCode().matches("^[A-Z0-9_]{3,50}$")) {
            errors.put("templateCode", "Use 3-50 uppercase letters, numbers, or underscores.");
        }
        if (form.templateName() == null || form.templateName().isBlank()) {
            errors.put("templateName", "Template name is required.");
        }
        if (form.eventCode() == null || !EVENT_CODES.contains(form.eventCode())) {
            errors.put("eventCode", "Select a valid event code.");
        }
        if (form.subjectTemplate() == null || form.subjectTemplate().isBlank()) {
            errors.put("subjectTemplate", "Subject is required.");
        }
        if (form.bodyHtml() == null || form.bodyHtml().isBlank()) {
            errors.put("bodyHtml", "HTML body is required.");
        }
        if (update && (form.id() == null || form.id() <= 0)) {
            errors.put("general", "Template id is missing.");
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    private static String render(String template, Map<String, String> placeholders) {
        if (template == null) {
            return "";
        }
        Matcher matcher = PLACEHOLDER.matcher(template);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String key = matcher.group(1);
            String value = placeholders.getOrDefault(key, "");
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(value));
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private static String defaultSubject(String eventCode) {
        return switch (eventCode.toUpperCase(Locale.ROOT)) {
            case "ACCOUNT_ACTIVATION" -> "Your LuxeStay account is ready";
            case "PASSWORD_RESET" -> "Your LuxeStay password was reset";
            case "ACCOUNT_VERIFICATION" -> "Verify your LuxeStay email";
            default -> "LuxeStay Hotel notification";
        };
    }

    private static String truncate(String message) {
        return message.length() <= 1000 ? message : message.substring(0, 997) + "...";
    }
}
