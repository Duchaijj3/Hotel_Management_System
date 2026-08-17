package com.hotel.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LogoutServletTest {
    @Test
    void logoutInvalidatesAnExistingSession() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(request.getContextPath()).thenReturn("/hotel");

        new LogoutServlet().doPost(request, response);

        verify(session).invalidate();
        verify(response).sendRedirect("/hotel/login");
    }

    @Test
    void repeatedLogoutWithoutSessionIsIdempotent() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getSession(false)).thenReturn(null);
        when(request.getContextPath()).thenReturn("/hotel");

        new LogoutServlet().doPost(request, response);

        verify(response).sendRedirect("/hotel/login");
    }
}
