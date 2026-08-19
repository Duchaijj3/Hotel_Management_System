package com.hotel.filter;

import com.hotel.dto.SessionUser;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(urlPatterns = {"/receptionist/*", "/manager/*", "/admin/*", "/staff/*"})
public class AuthorizationFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpSession session = httpRequest.getSession(false);
        SessionUser user = session == null ? null : (SessionUser) session.getAttribute("sessionUser");

        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

        if (user != null && !RoleAccessPolicy.canAccess(user.roleCode(), path)) {
            ((HttpServletResponse) response).sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "You do not have permission to access this function.");
            return;
        }
        chain.doFilter(request, response);
    }
}