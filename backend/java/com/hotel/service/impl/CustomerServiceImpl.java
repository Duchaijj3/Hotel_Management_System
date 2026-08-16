package com.hotel.service.impl;

import com.hotel.dao.CustomerDao;
import com.hotel.dto.CustomerDetailDto;
import com.hotel.dto.CustomerFormDto;
import com.hotel.dto.CustomerSearchCriteria;
import com.hotel.dto.CustomerSummaryDto;
import com.hotel.dto.PageResult;
import com.hotel.exception.ValidationException;
import com.hotel.service.CustomerService;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class CustomerServiceImpl implements CustomerService {
    private final CustomerDao dao;

    public CustomerServiceImpl(CustomerDao dao) {
        this.dao = dao;
    }

    @Override
    public PageResult<CustomerSummaryDto> search(CustomerSearchCriteria criteria) {
        return dao.search(criteria);
    }

    @Override
    public Optional<CustomerDetailDto> detail(long id) {
        return id > 0 ? dao.findDetail(id) : Optional.empty();
    }

    @Override
    public long create(CustomerFormDto form, long creatorUserId) throws ValidationException {
        if (creatorUserId <= 0) {
            throw new ValidationException(Map.of("general", "Receptionist session is invalid."));
        }
        validate(form, false);
        String customerCode = "WI-" + UUID.randomUUID().toString()
                .replace("-", "").substring(0, 16).toUpperCase();
        return dao.create(form, creatorUserId, customerCode);
    }

    @Override
    public void update(CustomerFormDto form) throws ValidationException {
        if (form.id() == null || form.id() <= 0 || dao.findDetail(form.id()).isEmpty()) {
            throw new ValidationException(Map.of("general", "Customer not found."));
        }
        validate(form, true);
        if (!dao.update(form)) {
            throw new ValidationException(Map.of("general",
                    "Customer was changed by another user. Reload and try again."));
        }
    }

    private void validate(CustomerFormDto form, boolean updating) throws ValidationException {
        Map<String, String> errors = new LinkedHashMap<>();
        if (form.fullName() == null || form.fullName().isBlank()) {
            errors.put("fullName", "Full name is required.");
        } else if (form.fullName().length() > 150) {
            errors.put("fullName", "Maximum 150 characters.");
        }

        if (form.email() != null && !form.email().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            errors.put("email", "Invalid email address.");
        }
        if (form.dateOfBirth() != null && form.dateOfBirth().isAfter(LocalDate.now())) {
            errors.put("dateOfBirth", "Date of birth cannot be in the future.");
        }

        boolean hasDocumentType = form.idDocumentType() != null;
        boolean hasDocumentNumber = form.idDocumentNumber() != null;
        if (hasDocumentType != hasDocumentNumber) {
            errors.put("idDocumentNumber", "Identification type and number must both be supplied.");
        }

        max(errors, "email", form.email(), 255);
        max(errors, "phone", form.phone(), 30);
        max(errors, "idDocumentType", form.idDocumentType(), 30);
        max(errors, "idDocumentNumber", form.idDocumentNumber(), 50);
        max(errors, "nationality", form.nationality(), 80);
        max(errors, "address", form.address(), 255);

        if (updating && form.version() == null) {
            errors.put("general", "The update version is missing. Reload the customer form.");
        }
        if (errors.isEmpty() && hasDocumentType
                && dao.documentExists(form.idDocumentType(), form.idDocumentNumber(), form.id())) {
            errors.put("idDocumentNumber", "Identification already exists.");
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    private void max(Map<String, String> errors, String field, String value, int maximum) {
        if (value != null && value.length() > maximum) {
            errors.put(field, "Maximum " + maximum + " characters.");
        }
    }
}
