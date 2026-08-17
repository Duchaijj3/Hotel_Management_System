package com.hotel.controller;

import com.hotel.dto.RoomRateForm;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ManagerPricingServletTest {
    @Test
    void rateFormUsesItsOwnFieldsWhenLegacyFilterParametersAreAlsoPresent()
            throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);

        // These values represent the price-calendar filter left in the URL.
        when(request.getParameter("roomTypeId")).thenReturn("14");
        when(request.getParameter("startDate")).thenReturn("2026-08-17");
        when(request.getParameter("endDate")).thenReturn("2026-09-16");

        // These values represent the room-rate form submitted by the manager.
        when(request.getParameter("rateRoomTypeId")).thenReturn("11");
        when(request.getParameter("rateStartDate")).thenReturn("2026-09-17");
        when(request.getParameter("rateEndDate")).thenReturn("2026-09-24");
        when(request.getParameter("rateNightlyPrice")).thenReturn("123456");

        Method bind = ManagerPricingServlet.class
                .getDeclaredMethod("bind", HttpServletRequest.class);
        bind.setAccessible(true);
        RoomRateForm form = (RoomRateForm) bind.invoke(
                new ManagerPricingServlet(), request);

        assertEquals(11, form.roomTypeId());
        assertEquals(LocalDate.of(2026, 9, 17), form.startDate());
        assertEquals(LocalDate.of(2026, 9, 24), form.endDate());
        assertEquals("123456", form.nightlyPrice().toPlainString());
    }
}
