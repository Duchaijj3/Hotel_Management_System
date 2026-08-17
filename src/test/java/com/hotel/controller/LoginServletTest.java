package com.hotel.controller;

import com.hotel.dao.UserDao;
import com.hotel.model.User;
import com.hotel.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LoginServletTest {
    @Test
    void successfulManagerLoginCreatesSessionAndRedirectsToDashboard() throws Exception {
        LoginServlet servlet = new LoginServlet(authFor("MANAGER"));
        HttpServletRequest request = loginRequest();
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(null);
        when(request.getSession(true)).thenReturn(session);

        servlet.doPost(request, response);

        verify(session).setAttribute(eq("sessionUser"), any());
        verify(response).sendRedirect("/hotel/manager/dashboard");
    }

    @Test
    void successfulReceptionistLoginKeepsItsExistingDestination() throws Exception {
        LoginServlet servlet = new LoginServlet(authFor("RECEPTIONIST"));
        HttpServletRequest request = loginRequest();
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(null);
        when(request.getSession(true)).thenReturn(session);

        servlet.doPost(request, response);

        verify(response).sendRedirect("/hotel/receptionist/customers");
    }

    private HttpServletRequest loginRequest() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("email")).thenReturn("manager@hotel.local");
        when(request.getParameter("password")).thenReturn("correct-password");
        when(request.getContextPath()).thenReturn("/hotel");
        return request;
    }

    private AuthService authFor(String roleCode) {
        User user = new User();
        user.setUserId(7);
        user.setEmail("manager@hotel.local");
        user.setPasswordHash(BCrypt.hashpw("correct-password", BCrypt.gensalt(4)));
        user.setFullName("Hotel Manager");
        user.setRoleCode(roleCode);
        user.setStatusCode("ACTIVE");
        UserDao users = email -> Optional.of(user);
        return new AuthService(users);
    }
}
