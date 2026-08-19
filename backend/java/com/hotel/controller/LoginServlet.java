package com.hotel.controller;

import com.hotel.dao.impl.UserDaoImpl;
import com.hotel.dto.SessionUser;
import com.hotel.service.AuthService;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final AuthService auth;

    // 1. Constructor mặc định (Bắt buộc phải có để server Tomcat chạy được trên thực tế)
    public LoginServlet() {
        this.auth = new AuthService(new UserDaoImpl());
    }

    // 2. Constructor nhận tham số (Để file LoginServletTest nhúng mock object vào)
    public LoginServlet(AuthService auth) {
        this.auth = auth;
    }

    protected void doGet(HttpServletRequest q, HttpServletResponse s) throws ServletException, IOException {
        q.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(q, s);
    }

    protected void doPost(HttpServletRequest q, HttpServletResponse s) throws ServletException, IOException {
        SessionUser u = auth.authenticate(q.getParameter("email"), q.getParameter("password"));

        if (u == null) {
            q.setAttribute("error", "Invalid email, password, or account status.");
            doGet(q, s);
            return;
        }

        q.getSession(true).setAttribute("sessionUser", u);

        // Điều hướng tùy theo role (Khớp với các Test Case của bạn)
        String redirectPath = "/";
        if ("RECEPTIONIST".equals(u.roleCode())) {
            redirectPath = "/receptionist/customers";
        } else if ("MANAGER".equals(u.roleCode())) {
            redirectPath = "/manager/dashboard";
        }

        s.sendRedirect(q.getContextPath() + redirectPath);
    }
}