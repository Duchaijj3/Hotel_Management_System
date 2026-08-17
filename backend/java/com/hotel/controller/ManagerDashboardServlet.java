package com.hotel.controller;

import com.hotel.dto.ManagerDashboardCard;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(ManagerRoutes.DASHBOARD)
public class ManagerDashboardServlet extends HttpServlet {
    private static final List<ManagerDashboardCard> CARDS = List.of(
            new ManagerDashboardCard("room-types", "Room Types",
                    "Quản lý danh mục loại phòng và trạng thái kích hoạt.",
                    "bed", List.of("UC60", "UC63"), ManagerRoutes.ROOM_TYPES, true),
            new ManagerDashboardCard("amenities", "Amenities",
                    "Thêm, đổi tên hoặc gỡ tiện nghi trong cấu hình loại phòng.",
                    "featured_seasonal_and_gifts", List.of("UC61"),
                    ManagerRoutes.ROOM_TYPES, true),
            new ManagerDashboardCard("rooms", "Rooms",
                    "Quản lý phòng vật lý và trạng thái vận hành.",
                    "meeting_room", List.of("UC58", "UC59"), ManagerRoutes.ROOMS, true),
            new ManagerDashboardCard("pricing", "Room Pricing",
                    "Cấu hình giá cơ bản, giá theo ngày và stop-sell.",
                    "sell", List.of("UC64"), ManagerRoutes.PRICING, true)
    );

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("activePage", "dashboard");
        request.setAttribute("pageTitle", "Manager Dashboard");
        request.setAttribute("dashboardCards", CARDS);
        request.getRequestDispatcher("/WEB-INF/views/manager/dashboard.jsp")
                .forward(request, response);
    }
}
