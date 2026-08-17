package com.hotel.controller;

import com.hotel.dao.impl.ManagerRoomDaoImpl;
import com.hotel.dto.RoomTypeForm;
import com.hotel.dto.RoomTypeSearchCriteria;
import com.hotel.dto.RoomTypeView;
import com.hotel.exception.DataAccessException;
import com.hotel.exception.ValidationException;
import com.hotel.service.ManagerRoomService;
import com.hotel.service.impl.ManagerRoomServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

@WebServlet(urlPatterns = {ManagerRoutes.ROOM_TYPES, "/manager/room-types/view",
        "/manager/room-types/create", "/manager/room-types/edit",
        "/manager/room-types/status"})
public class ManagerRoomTypeServlet extends HttpServlet {
    private final ManagerRoomService service = new ManagerRoomServiceImpl(
            new ManagerRoomDaoImpl());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("activePage", "room-types");
            switch (request.getServletPath()) {
                case "/manager/room-types" -> list(request, response);
                case "/manager/room-types/view" -> detail(request, response);
                case "/manager/room-types/create" -> {
                    request.setAttribute("mode", "create");
                    request.setAttribute("item", new RoomTypeForm(null, "", "", "",
                            1, 0, "", null, BigDecimal.ZERO, List.of(), null));
                    view(request, response, "form.jsp");
                }
                case "/manager/room-types/edit" -> edit(request, response);
                default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (NoSuchElementException | NumberFormatException exception) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (DataAccessException exception) {
            fail(request, response, exception);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        try {
            if (path.endsWith("/status")) {
                long id = Long.parseLong(request.getParameter("id"));
                service.setRoomTypeActive(id,
                        Boolean.parseBoolean(request.getParameter("active")));
                flash(request, "Room type status updated.");
                response.sendRedirect(request.getContextPath()
                        + "/manager/room-types/view?id=" + id);
                return;
            }

            RoomTypeForm form = bind(request, path.endsWith("/edit"));
            request.setAttribute("item", form);
            request.setAttribute("amenitiesText", String.join(", ", form.amenities()));
            long id;
            if (path.endsWith("/create")) {
                id = service.createRoomType(form);
                flash(request, "Room type created as inactive.");
            } else {
                service.updateRoomType(form);
                id = form.id();
                flash(request, "Room type updated.");
            }
            response.sendRedirect(request.getContextPath()
                    + "/manager/room-types/view?id=" + id);
        } catch (ValidationException exception) {
            request.setAttribute("errors", exception.getErrors());
            request.setAttribute("mode", path.endsWith("/create") ? "create" : "edit");
            request.setAttribute("activePage", "room-types");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            view(request, response, "form.jsp");
        } catch (NumberFormatException exception) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (DataAccessException exception) {
            fail(request, response, exception);
        }
    }

    private void list(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RoomTypeSearchCriteria criteria = new RoomTypeSearchCriteria(
                request.getParameter("keyword"), active(request.getParameter("active")),
                positiveInt(request.getParameter("page"), 1), 20);
        request.setAttribute("criteria", criteria);
        request.setAttribute("result", service.searchRoomTypes(criteria));
        request.setAttribute("activePage", "room-types");
        view(request, response, "list.jsp");
    }

    private void detail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RoomTypeView item = service.getRoomType(
                Long.parseLong(request.getParameter("id"))).orElseThrow();
        request.setAttribute("item", item);
        request.setAttribute("activePage", "room-types");
        view(request, response, "detail.jsp");
    }

    private void edit(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RoomTypeView item = service.getRoomType(
                Long.parseLong(request.getParameter("id"))).orElseThrow();
        request.setAttribute("item", item);
        request.setAttribute("amenitiesText", String.join(", ", item.amenities()));
        request.setAttribute("mode", "edit");
        request.setAttribute("activePage", "room-types");
        view(request, response, "form.jsp");
    }

    private RoomTypeForm bind(HttpServletRequest request, boolean updating) {
        Long id = updating ? parseLong(request.getParameter("id")) : null;
        LocalDateTime version = updating ? parseDateTime(request.getParameter("version")) : null;
        return new RoomTypeForm(id, trim(request, "typeCode"), trim(request, "typeName"),
                trim(request, "description"), integer(request.getParameter("maxAdults")),
                integer(request.getParameter("maxChildren")), trim(request, "bedType"),
                decimal(request.getParameter("roomSizeM2")),
                decimal(request.getParameter("basePrice")),
                amenities(request.getParameter("amenities")), version);
    }

    private List<String> amenities(String value) {
        if (value == null || value.isBlank()) return List.of();
        return Arrays.stream(value.split("[,\\r\\n]+"))
                .map(String::trim).filter(item -> !item.isEmpty()).distinct().toList();
    }

    private Boolean active(String value) {
        if (value == null || value.isBlank()) return null;
        return "true".equalsIgnoreCase(value);
    }

    private int positiveInt(String value, int fallback) {
        try { return Math.max(1, Integer.parseInt(value)); }
        catch (RuntimeException exception) { return fallback; }
    }

    private int integer(String value) {
        try { return Integer.parseInt(value); }
        catch (RuntimeException exception) { return 0; }
    }

    private Long parseLong(String value) {
        try { return Long.valueOf(value); }
        catch (RuntimeException exception) { return null; }
    }

    private BigDecimal decimal(String value) {
        try { return value == null || value.isBlank() ? null : new BigDecimal(value.trim()); }
        catch (RuntimeException exception) { return null; }
    }

    private LocalDateTime parseDateTime(String value) {
        try { return LocalDateTime.parse(value); }
        catch (RuntimeException exception) { return null; }
    }

    private String trim(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        return value == null || value.isBlank() ? null : value.trim();
    }

    private void flash(HttpServletRequest request, String message) {
        request.getSession().setAttribute("flash", message);
    }

    private void view(HttpServletRequest request, HttpServletResponse response, String name)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/manager/room-types/" + name)
                .forward(request, response);
    }

    private void fail(HttpServletRequest request, HttpServletResponse response,
                      RuntimeException exception) throws IOException {
        getServletContext().log("Manager room type operation failed", exception);
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "MSG21: An unexpected error has occurred. Please try again later.");
    }
}
