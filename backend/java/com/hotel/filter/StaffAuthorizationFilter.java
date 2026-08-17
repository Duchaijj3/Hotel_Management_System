package com.hotel.filter;

import com.hotel.dto.SessionUser;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebFilter("/staff/*")
public class StaffAuthorizationFilter implements Filter {
    public void doFilter(ServletRequest a, ServletResponse b, FilterChain c) throws IOException, ServletException {
        HttpSession h = ((HttpServletRequest) a).getSession(false);
        SessionUser u = h == null ? null : (SessionUser) h.getAttribute("sessionUser");

        // Chỉ cho phép nhân viên (STAFF) truy cập
        if (u != null && !"STAFF".equals(u.roleCode())) {
            ((HttpServletResponse) b).sendError(403, "Access Denied: Quyền hạn không hợp lệ. Cần role STAFF.");
            return;
        }
        c.doFilter(a, b);
    }
}