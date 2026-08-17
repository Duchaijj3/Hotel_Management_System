package com.hotel.controller;

import com.hotel.dto.PublicRoomTypeDto;
import com.hotel.service.PublicRoomService;
import com.hotel.service.impl.PublicRoomServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

@WebServlet(urlPatterns = { "/room-details" })
public class RoomDetailPageServlet extends HttpServlet {
    private final PublicRoomService roomService = new PublicRoomServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        if (idParam == null || idParam.isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/rooms");
            return;
        }

        try {
            long roomTypeId = Long.parseLong(idParam.trim());
            Optional<PublicRoomTypeDto> roomOpt = roomService.detail(roomTypeId);
            if (roomOpt.isPresent()) {
                req.setAttribute("room", roomOpt.get());
                req.getRequestDispatcher("/WEB-INF/views/rooms/room-details.jsp").forward(req, resp);
            } else {
                req.setAttribute("error", "Room type not found");
                req.getRequestDispatcher("/WEB-INF/views/rooms/room-details.jsp").forward(req, resp);
            }
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/rooms");
        }
    }
}
