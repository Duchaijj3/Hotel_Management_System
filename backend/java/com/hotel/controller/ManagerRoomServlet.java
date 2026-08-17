package com.hotel.controller;

import com.hotel.dao.impl.ManagerRoomDaoImpl;
import com.hotel.dto.RoomForm;
import com.hotel.dto.RoomSearchCriteria;
import com.hotel.dto.RoomTypeSearchCriteria;
import com.hotel.dto.RoomView;
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
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@WebServlet(urlPatterns = {ManagerRoutes.ROOMS, "/manager/rooms/view",
        "/manager/rooms/create", "/manager/rooms/edit", "/manager/rooms/status"})
public class ManagerRoomServlet extends HttpServlet {
    private final ManagerRoomService service = new ManagerRoomServiceImpl(
            new ManagerRoomDaoImpl());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("activePage", "rooms");
            switch (request.getServletPath()) {
                case "/manager/rooms" -> list(request, response);
                case "/manager/rooms/view" -> detail(request, response);
                case "/manager/rooms/create" -> {
                    request.setAttribute("mode", "create");
                    request.setAttribute("item", new RoomForm(null, 0, "", null,
                            "AVAILABLE", "", true, null));
                    roomTypes(request, true);
                    view(request, response, "form.jsp");
                }
                case "/manager/rooms/edit" -> edit(request, response);
                default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (NoSuchElementException | NumberFormatException exception) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (DataAccessException exception) {
            fail(response, exception);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        try {
            if (path.endsWith("/status")) {
                long id = Long.parseLong(request.getParameter("id"));
                service.changeRoomOperationalStatus(id,
                        request.getParameter("operationalStatus"),
                        Boolean.parseBoolean(request.getParameter("active")));
                flash(request, "Room operational status updated.");
                response.sendRedirect(request.getContextPath()
                        + "/manager/rooms/view?id=" + id);
                return;
            }

            RoomForm form = bind(request, path.endsWith("/edit"));
            request.setAttribute("item", form);
            long id;
            if (path.endsWith("/create")) {
                id = service.createRoom(form);
                flash(request, "Room created.");
            } else {
                service.updateRoom(form);
                id = form.id();
                flash(request, "Room updated.");
            }
            response.sendRedirect(request.getContextPath() + "/manager/rooms/view?id=" + id);
        } catch (ValidationException exception) {
            request.setAttribute("errors", exception.getErrors());
            request.setAttribute("mode", path.endsWith("/create") ? "create" : "edit");
            request.setAttribute("activePage", "rooms");
            roomTypes(request, path.endsWith("/create") || path.endsWith("/edit"));
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            view(request, response, "form.jsp");
        } catch (NumberFormatException exception) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (DataAccessException exception) {
            fail(response, exception);
        }
    }

    private void list(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RoomSearchCriteria criteria = new RoomSearchCriteria(request.getParameter("keyword"),
                longOrNull(request.getParameter("roomTypeId")),
                request.getParameter("operationalStatus"),
                booleanOrNull(request.getParameter("active")),
                intOrNull(request.getParameter("floorNumber")),
                positiveInt(request.getParameter("page"), 1), 20);
        request.setAttribute("criteria", criteria);
        request.setAttribute("result", service.searchRooms(criteria));
        request.setAttribute("activePage", "rooms");
        roomTypes(request, false);
        view(request, response, "list.jsp");
    }

    private void detail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RoomView item = service.getRoom(Long.parseLong(request.getParameter("id")))
                .orElseThrow();
        request.setAttribute("item", item);
        request.setAttribute("activePage", "rooms");
        view(request, response, "detail.jsp");
    }

    private void edit(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RoomView item = service.getRoom(Long.parseLong(request.getParameter("id")))
                .orElseThrow();
        request.setAttribute("item", item);
        request.setAttribute("mode", "edit");
        request.setAttribute("activePage", "rooms");
        roomTypes(request, true);
        view(request, response, "form.jsp");
    }

    private RoomForm bind(HttpServletRequest request, boolean updating) {
        return new RoomForm(updating ? longOrNull(request.getParameter("id")) : null,
                longValue(request.getParameter("roomTypeId")), trim(request, "roomNumber"),
                intOrNull(request.getParameter("floorNumber")),
                request.getParameter("operationalStatus"), trim(request, "notes"),
                request.getParameter("active") != null,
                updating ? dateTime(request.getParameter("version")) : null);
    }

    private void roomTypes(HttpServletRequest request, boolean activeOnly) {
        request.setAttribute("roomTypes", service.searchRoomTypes(
                new RoomTypeSearchCriteria(null, activeOnly ? true : null,
                        1, 100)).items());
    }

    private Long longOrNull(String value) {
        try { return value == null || value.isBlank() ? null : Long.valueOf(value); }
        catch (RuntimeException exception) { return null; }
    }

    private long longValue(String value) {
        Long parsed = longOrNull(value);
        return parsed == null ? 0 : parsed;
    }

    private Integer intOrNull(String value) {
        try { return value == null || value.isBlank() ? null : Integer.valueOf(value); }
        catch (RuntimeException exception) { return null; }
    }

    private int positiveInt(String value, int fallback) {
        Integer parsed = intOrNull(value);
        return parsed == null ? fallback : Math.max(1, parsed);
    }

    private Boolean booleanOrNull(String value) {
        return value == null || value.isBlank() ? null : Boolean.valueOf(value);
    }

    private LocalDateTime dateTime(String value) {
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
        request.getRequestDispatcher("/WEB-INF/views/manager/rooms/" + name)
                .forward(request, response);
    }

    private void fail(HttpServletResponse response, RuntimeException exception)
            throws IOException {
        getServletContext().log("Manager room operation failed", exception);
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "MSG21: An unexpected error has occurred. Please try again later.");
    }
}
