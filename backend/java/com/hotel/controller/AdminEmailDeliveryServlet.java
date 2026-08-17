package com.hotel.controller;

import com.hotel.dto.EmailDeliverySearchCriteria;
import com.hotel.exception.DataAccessException;
import com.hotel.exception.ValidationException;
import com.hotel.service.AdminEmailService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.NoSuchElementException;

@WebServlet(urlPatterns = {
        AdminRoutes.EMAIL_DELIVERIES,
        "/admin/email-deliveries/retry"
})
public class AdminEmailDeliveryServlet extends HttpServlet {
    private final AdminEmailService service = AdminServices.emails();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("activePage", "email-deliveries");
            if (AdminRoutes.EMAIL_DELIVERIES.equals(request.getServletPath())) {
                list(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
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
            if ("/admin/email-deliveries/retry".equals(path)) {
                long id = Long.parseLong(request.getParameter("id"));
                service.retryDelivery(id);
                flash(request, "Email queued for retry.");
                response.sendRedirect(request.getContextPath() + AdminRoutes.EMAIL_DELIVERIES);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (ValidationException exception) {
            flash(request, "Failed to retry: " + exception.getMessage());
            response.sendRedirect(request.getContextPath() + AdminRoutes.EMAIL_DELIVERIES);
        } catch (NoSuchElementException | NumberFormatException exception) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (DataAccessException exception) {
            fail(response, exception);
        }
    }

    private void list(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int page = parsePositiveInt(request.getParameter("page"), 1);
        String statusCode = trim(request, "statusCode");
        String eventCode = trim(request, "eventCode");

        EmailDeliverySearchCriteria criteria = new EmailDeliverySearchCriteria(
                statusCode, eventCode, page, 20);
        request.setAttribute("criteria", criteria);
        request.setAttribute("result", service.searchDeliveries(criteria));
        
        request.setAttribute("flash", request.getSession().getAttribute("flash"));
        request.getSession().removeAttribute("flash");

        view(request, response, "list.jsp");
    }

    private int parsePositiveInt(String value, int fallback) {
        try {
            return Math.max(1, Integer.parseInt(value));
        } catch (RuntimeException exception) {
            return fallback;
        }
    }

    private String trim(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }

    private void flash(HttpServletRequest request, String message) {
        request.getSession().setAttribute("flash", message);
    }

    private void view(HttpServletRequest request, HttpServletResponse response, String page)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/admin/email-deliveries/" + page)
                .forward(request, response);
    }

    private void fail(HttpServletResponse response, DataAccessException exception)
            throws IOException {
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception.getMessage());
    }
}
