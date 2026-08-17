package com.hotel.controller;

import com.hotel.dto.PageResult;
import com.hotel.dto.PublicRoomTypeDto;
import com.hotel.service.PublicRoomService;
import com.hotel.service.impl.PublicRoomServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@WebServlet(urlPatterns = { "/rooms" })
public class RoomPageServlet extends HttpServlet {
    private final PublicRoomService roomService = new PublicRoomServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String keyword = req.getParameter("keyword");
        LocalDate checkIn = parseDate(req.getParameter("checkIn"));
        LocalDate checkOut = parseDate(req.getParameter("checkOut"));
        Integer guests = parseInteger(req.getParameter("guests"));
        BigDecimal minPrice = parseBigDecimal(req.getParameter("minPrice"));
        BigDecimal maxPrice = parseBigDecimal(req.getParameter("maxPrice"));

        int page = parseIntegerOrDefault(req.getParameter("page"), 1);
        int pageSize = parseIntegerOrDefault(req.getParameter("pageSize"), 6);

        String typesParam = req.getParameter("types");
        List<String> typeCodes = null;
        if (typesParam != null && !typesParam.isBlank()) {
            typeCodes = Arrays.stream(typesParam.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
        }

        String amenitiesParam = req.getParameter("amenities");
        List<String> amenities = null;
        if (amenitiesParam != null && !amenitiesParam.isBlank()) {
            amenities = Arrays.stream(amenitiesParam.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
        }

        PageResult<PublicRoomTypeDto> pageResult = roomService.search(
                keyword, checkIn, checkOut, guests, minPrice, maxPrice, typeCodes, amenities, page, pageSize);

        req.setAttribute("pageResult", pageResult);
        req.setAttribute("keyword", keyword != null ? keyword : "");
        req.setAttribute("checkIn", checkIn != null ? checkIn.toString() : LocalDate.now().toString());
        req.setAttribute("checkOut", checkOut != null ? checkOut.toString() : LocalDate.now().plusDays(1).toString());
        req.setAttribute("guests", guests != null ? guests : 2);
        req.setAttribute("maxPrice", maxPrice != null ? maxPrice : 1000);
        req.setAttribute("selectedTypes", typeCodes != null ? typeCodes
                : List.of("DELUXE_OCEAN", "EXEC_SUITE", "PREM_GARDEN", "PRESIDENTIAL", "STD_CITY"));
        req.setAttribute("selectedAmenities", amenities != null ? amenities : List.of());

        req.getRequestDispatcher("/WEB-INF/views/rooms/rooms.jsp").forward(req, resp);
    }

    private LocalDate parseDate(String val) {
        if (val == null || val.isBlank())
            return null;
        try {
            return LocalDate.parse(val.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private Integer parseInteger(String val) {
        if (val == null || val.isBlank())
            return null;
        try {
            return Integer.parseInt(val.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private int parseIntegerOrDefault(String val, int defaultVal) {
        if (val == null || val.isBlank())
            return defaultVal;
        try {
            return Integer.parseInt(val.trim());
        } catch (Exception e) {
            return defaultVal;
        }
    }

    private BigDecimal parseBigDecimal(String val) {
        if (val == null || val.isBlank())
            return null;
        try {
            return new BigDecimal(val.trim());
        } catch (Exception e) {
            return null;
        }
    }
}
