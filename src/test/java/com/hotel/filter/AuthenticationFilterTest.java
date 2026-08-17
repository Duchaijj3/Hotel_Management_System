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

class AuthenticationFilterTest {
    @Test
    void unauthenticatedDashboardRequestRedirectsToLoginWithoutRendering() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        when(request.getSession(false)).thenReturn(null);
        when(request.getContextPath()).thenReturn("/hotel");

        new AuthenticationFilter().doFilter(request, response, chain);

        verify(response).sendRedirect("/hotel/login");
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    void dashboardReloadWithManagerSessionContinuesRequest() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("sessionUser")).thenReturn(
                new SessionUser(7, "manager@hotel.local", "Hotel Manager", "MANAGER"));

        new AuthenticationFilter().doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
    }
}
