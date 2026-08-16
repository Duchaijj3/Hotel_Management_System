package com.hotel.service.impl;

import com.hotel.dao.CustomerDao;
import com.hotel.dto.CustomerDetailDto;
import com.hotel.dto.CustomerFormDto;
import com.hotel.dto.CustomerSearchCriteria;
import com.hotel.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceImplTest {
    CustomerDao dao;
    CustomerServiceImpl service;

    @BeforeEach void setup() {
        dao = mock(CustomerDao.class);
        service = new CustomerServiceImpl(dao);
    }

    private CustomerFormDto valid() {
        return new CustomerFormDto(null, "Guest Name", "guest@example.com", "123",
                LocalDate.of(2000, 1, 1), "PASSPORT", "P1", "VN", "Address", null);
    }

    @Test void rejectsBlankName() {
        var form = new CustomerFormDto(null, " ", null, null, null,
                null, null, null, null, null);
        assertThrows(ValidationException.class, () -> service.create(form, 7));
        verifyNoInteractions(dao);
    }

    @Test void rejectsInvalidEmail() {
        var x = valid();
        var form = new CustomerFormDto(null, x.fullName(), "bad", x.phone(), x.dateOfBirth(),
                x.idDocumentType(), x.idDocumentNumber(), x.nationality(), x.address(), null);
        assertThrows(ValidationException.class, () -> service.create(form, 7));
    }

    @Test void rejectsFutureBirthDate() {
        var x = valid();
        var form = new CustomerFormDto(null, x.fullName(), x.email(), x.phone(),
                LocalDate.now().plusDays(1), x.idDocumentType(), x.idDocumentNumber(),
                x.nationality(), x.address(), null);
        assertThrows(ValidationException.class, () -> service.create(form, 7));
    }

    @Test void requiresBothDocumentFields() {
        var x = valid();
        var form = new CustomerFormDto(null, x.fullName(), x.email(), x.phone(), x.dateOfBirth(),
                x.idDocumentType(), null, x.nationality(), x.address(), null);
        assertThrows(ValidationException.class, () -> service.create(form, 7));
    }

    @Test void rejectsDuplicateDocument() {
        when(dao.documentExists("PASSPORT", "P1", null)).thenReturn(true);
        assertThrows(ValidationException.class, () -> service.create(valid(), 7));
    }

    @Test void creatorComesFromSessionArgumentAndWalkInCodeIsGenerated() throws Exception {
        when(dao.create(any(), eq(42L), startsWith("WI-"))).thenReturn(9L);
        assertEquals(9, service.create(valid(), 42));
        verify(dao).create(argThat(form -> form.id() == null), eq(42L), startsWith("WI-"));
    }

    @Test void normalizesSearchPaging() {
        var criteria = new CustomerSearchCriteria("  Jane   Doe ", null, 0, 20);
        assertEquals("Jane Doe", criteria.keyword());
        assertEquals(1, criteria.page());
    }

    @Test void rejectsUnknownSearchStatus() {
        assertNull(new CustomerSearchCriteria(null, "deleted", 1, 20).status());
    }

    @Test void rejectsOversizedIdentificationFields() {
        var x = valid();
        var form = new CustomerFormDto(null, x.fullName(), x.email(), x.phone(), x.dateOfBirth(),
                "T".repeat(31), "N".repeat(51), x.nationality(), x.address(), null);
        ValidationException error = assertThrows(ValidationException.class,
                () -> service.create(form, 7));
        assertTrue(error.getErrors().containsKey("idDocumentType"));
        assertTrue(error.getErrors().containsKey("idDocumentNumber"));
    }

    @Test void rejectsInvalidCreatorSession() {
        ValidationException error = assertThrows(ValidationException.class,
                () -> service.create(valid(), 0));
        assertTrue(error.getErrors().containsKey("general"));
        verifyNoInteractions(dao);
    }

    @Test void detectsMissingCustomerOnUpdate() {
        var form = new CustomerFormDto(99L, "Name", null, null, null,
                null, null, null, null, LocalDateTime.now());
        when(dao.findDetail(99)).thenReturn(Optional.empty());
        assertThrows(ValidationException.class, () -> service.update(form));
    }

    @Test void requiresVersionOnUpdate() {
        var form = new CustomerFormDto(3L, "Name", null, null, null,
                null, null, null, null, null);
        when(dao.findDetail(3)).thenReturn(Optional.of(mock(CustomerDetailDto.class)));
        ValidationException error = assertThrows(ValidationException.class,
                () -> service.update(form));
        assertTrue(error.getErrors().containsKey("general"));
        verify(dao, never()).update(any());
    }

    @Test void excludesCurrentCustomerFromDuplicateCheck() throws Exception {
        var form = new CustomerFormDto(3L, "Name", null, null, null,
                "ID", "X", null, null, LocalDateTime.now());
        when(dao.findDetail(3)).thenReturn(Optional.of(mock(CustomerDetailDto.class)));
        when(dao.update(form)).thenReturn(true);
        service.update(form);
        verify(dao).documentExists("ID", "X", 3L);
    }
}
