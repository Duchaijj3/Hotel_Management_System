//
//
//package com.hotel.filter;
//
//import com.hotel.dto.SessionUser;
//import jakarta.servlet.Filter;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.ServletRequest;
//import jakarta.servlet.ServletResponse;
//import jakarta.servlet.annotation.WebFilter;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpSession;
//
//import java.io.IOException;
//
//@WebFilter("/staff/*")
//public class StaffAuthorizationFilter implements Filter {
//
//    @Override
//    public void doFilter(ServletRequest request,
//                         ServletResponse response,
//                         FilterChain chain)
//            throws IOException, ServletException {
//
//        HttpServletRequest req = (HttpServletRequest) request;
//        HttpServletResponse resp = (HttpServletResponse) response;
//
//        HttpSession session = req.getSession(false);
//        SessionUser user = session == null
//                ? null
//                : (SessionUser) session.getAttribute("sessionUser");
//
//        if (user == null) {
//            resp.sendRedirect(req.getContextPath() + "/login");
//            return;
//        }
//
//        String path = req.getRequestURI()
//                .substring(req.getContextPath().length());
//
//        boolean isHousekeepingRoute = path.startsWith("/staff/housekeeping");
//        boolean isServiceRequestRoute = path.startsWith("/staff/service-requests");
//
//        boolean allowed = (isHousekeepingRoute
//                && "HOUSEKEEPING_STAFF".equals(user.roleCode()))
//                || (isServiceRequestRoute
//                && "SERVICE_STAFF".equals(user.roleCode()));
//
//        if (!allowed) {
//            resp.sendError(
//                    HttpServletResponse.SC_FORBIDDEN,
//                    "You do not have permission to access this resource."
//            );
//            return;
//        }
//
//        chain.doFilter(request, response);
//    }
//}