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
import java.util.Optional;

@WebServlet(urlPatterns = { "/api/rooms", "/api/rooms/detail" })
public class PublicRoomServlet extends HttpServlet {
    private final PublicRoomService roomService = new PublicRoomServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");

        String servletPath = req.getServletPath();
        try {
            if ("/api/rooms/detail".equals(servletPath)) {
                handleDetail(req, resp);
            } else {
                handleSearch(req, resp);
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": " + escapeJson("Failed to process request: " + e.getMessage()) + "}");
        }
    }

    private void handleSearch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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

        resp.getWriter().write(toJson(pageResult));
    }

    private void handleDetail(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idParam = req.getParameter("id");
        if (idParam == null || idParam.isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Missing required 'id' parameter\"}");
            return;
        }

        try {
            long roomTypeId = Long.parseLong(idParam.trim());
            Optional<PublicRoomTypeDto> roomOpt = roomService.detail(roomTypeId);
            if (roomOpt.isPresent()) {
                resp.getWriter().write(toJson(roomOpt.get()));
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\": \"Room type not found\"}");
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Invalid room type id\"}");
        }
    }

    private String toJson(PageResult<PublicRoomTypeDto> pr) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"page\":").append(pr.getPage()).append(",");
        sb.append("\"pageSize\":").append(pr.getPageSize()).append(",");
        sb.append("\"totalItems\":").append(pr.getTotalItems()).append(",");
        sb.append("\"totalPages\":").append(pr.totalPages()).append(",");
        sb.append("\"items\":[");
        List<PublicRoomTypeDto> items = pr.getItems();
        for (int i = 0; i < items.size(); i++) {
            if (i > 0)
                sb.append(",");
            sb.append(toJson(items.get(i)));
        }
        sb.append("]}");
        return sb.toString();
    }

    private String toJson(PublicRoomTypeDto r) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"roomTypeId\":").append(r.getRoomTypeId()).append(",");
        sb.append("\"typeCode\":").append(escapeJson(r.getTypeCode())).append(",");
        sb.append("\"typeName\":").append(escapeJson(r.getTypeName())).append(",");
        sb.append("\"description\":").append(escapeJson(r.getDescription())).append(",");
        sb.append("\"maxAdults\":").append(r.getMaxAdults()).append(",");
        sb.append("\"maxChildren\":").append(r.getMaxChildren()).append(",");
        sb.append("\"bedType\":").append(escapeJson(r.getBedType())).append(",");
        sb.append("\"roomSizeM2\":").append(r.getRoomSizeM2() != null ? r.getRoomSizeM2() : "null").append(",");
        sb.append("\"basePrice\":").append(r.getBasePrice() != null ? r.getBasePrice() : "0").append(",");
        sb.append("\"availableRoomsCount\":").append(r.getAvailableRoomsCount()).append(",");

        // amenities array
        sb.append("\"amenities\":[");
        if (r.getAmenities() != null) {
            for (int i = 0; i < r.getAmenities().size(); i++) {
                if (i > 0)
                    sb.append(",");
                sb.append(escapeJson(r.getAmenities().get(i)));
            }
        }
        sb.append("],");

        // images array
        sb.append("\"images\":[");
        if (r.getImages() != null) {
            for (int i = 0; i < r.getImages().size(); i++) {
                if (i > 0)
                    sb.append(",");
                sb.append(escapeJson(r.getImages().get(i)));
            }
        }
        sb.append("]");

        sb.append("}");
        return sb.toString();
    }

    private String escapeJson(String s) {
        if (s == null)
            return "null";
        StringBuilder sb = new StringBuilder("\"");
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\b' -> sb.append("\\b");
                case '\f' -> sb.append("\\f");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < ' ') {
                        String hex = "000" + Integer.toHexString(c);
                        sb.append("\\u").append(hex.substring(hex.length() - 4));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        sb.append("\"");
        return sb.toString();
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
