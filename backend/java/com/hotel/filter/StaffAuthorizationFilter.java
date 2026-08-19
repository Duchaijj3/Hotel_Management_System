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

@WebFilter("/staff/*")
public class StaffAuthorizationFilter implements Filter {

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        HttpSession session = httpRequest.getSession(false);

        SessionUser user = session == null
                ? null
                : (SessionUser) session.getAttribute("sessionUser");

        if (user == null) {
            httpResponse.sendRedirect(
                    httpRequest.getContextPath() + "/login"
            );
            return;
        }

        String path = httpRequest.getRequestURI()
                .substring(httpRequest.getContextPath().length());

        boolean allowed = RoleAccessPolicy.canAccess(
                user.roleCode(),
                path
        );

        if (!allowed) {
            httpResponse.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "You do not have permission to access this resource."
            );
            return;
        }

        chain.doFilter(request, response);
    }
}