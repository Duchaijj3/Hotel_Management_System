package com.hotel.filter;

import com.hotel.dto.SessionUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthorizationFilterTest {
    @Test
    void receptionistReceivesMsg20OnManagerDashboard() throws Exception {
        HttpServletRequest request = requestFor(
                new SessionUser(9, "desk@hotel.local", "Front Desk", "RECEPTIONIST"));
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        new AuthorizationFilter().doFilter(request, response, chain);

        verify(response).sendError(403,
                "MSG20: You do not have permission to access this function.");
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    void managerReachesManagerDashboard() throws Exception {
        HttpServletRequest request = requestFor(
                new SessionUser(7, "manager@hotel.local", "Manager", "MANAGER"));
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        new AuthorizationFilter().doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
    }

    private HttpServletRequest requestFor(SessionUser user) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("sessionUser")).thenReturn(user);
        when(request.getContextPath()).thenReturn("/hotel");
        when(request.getRequestURI()).thenReturn("/hotel/manager/dashboard");
        return request;
    }
}
