package com.hotel.controller;

import com.hotel.dto.ManagerDashboardCard;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(AdminRoutes.DASHBOARD)
public class AdminDashboardServlet extends HttpServlet {
    private static final List<ManagerDashboardCard> CARDS = List.of(
            new ManagerDashboardCard("users", "User Accounts & Roles",
                    "Tạo tài khoản nhân viên, gán vai trò và khóa/mở khóa truy cập.",
                    "manage_accounts", List.of("F25"), AdminRoutes.USERS, true),
            new ManagerDashboardCard("email-templates", "Email Templates",
                    "Chuẩn hóa nội dung thông báo và cấu hình trigger gửi email.",
                    "mail", List.of("F26"), AdminRoutes.EMAIL_TEMPLATES, true),
            new ManagerDashboardCard("email-deliveries", "Email Delivery Log",
                    "Theo dõi trạng thái gửi và thử lại email thất bại.",
                    "mark_email_read", List.of("F26"), AdminRoutes.EMAIL_DELIVERIES, true),
            new ManagerDashboardCard("profile", "Personal Profile",
                    "Cập nhật thông tin liên hệ và đổi mật khẩu tài khoản Admin.",
                    "account_circle", List.of("F05"), AdminRoutes.PROFILE, true)
    );

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("activePage", "dashboard");
        request.setAttribute("pageTitle", "Admin Dashboard");
        request.setAttribute("dashboardCards", CARDS);
        request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp")
                .forward(request, response);
    }
}
