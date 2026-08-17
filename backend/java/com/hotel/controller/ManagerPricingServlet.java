package com.hotel.controller;

import com.hotel.dao.impl.ManagerRoomDaoImpl;
import com.hotel.dto.RoomRateForm;
import com.hotel.dto.PageResult;
import com.hotel.dto.RoomRateView;
import com.hotel.dto.RoomTypeSearchCriteria;
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
import java.time.LocalDate;

@WebServlet(ManagerRoutes.PRICING)
public class ManagerPricingServlet extends HttpServlet {
    private static final int RATE_PAGE_SIZE = 25;
    private final ManagerRoomService service = new ManagerRoomServiceImpl(
            new ManagerRoomDaoImpl());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            show(request, response, bindFilter(request));
        } catch (DataAccessException exception) {
            fail(response, exception);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RoomRateForm form = bind(request);
        request.setAttribute("rateForm", form);
        try {
            service.setRoomRateRange(form);
            request.getSession().setAttribute("flash", "Room rates saved.");
            response.sendRedirect(request.getContextPath()
                    + "/manager/pricing?filterRoomTypeId=" + form.roomTypeId()
                    + "&filterStartDate=" + form.startDate()
                    + "&filterEndDate=" + form.endDate());
        } catch (ValidationException exception) {
            request.setAttribute("errors", exception.getErrors());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            show(request, response, filterFor(form));
        } catch (DataAccessException exception) {
            fail(response, exception);
        }
    }

    private void show(HttpServletRequest request, HttpServletResponse response,
                      PricingFilter filter)
            throws ServletException, IOException {
        if (request.getAttribute("rateForm") == null) {
            LocalDate today = LocalDate.now();
            request.setAttribute("rateForm", new RoomRateForm(
                    0, today, today.plusDays(30), null, false));
        }
        request.setAttribute("roomTypes", service.searchRoomTypes(
                new RoomTypeSearchCriteria(null, null, 1, 100)).items());
        PageResult<RoomRateView> rateResult = service.searchRoomRates(
                filter.roomTypeId(), filter.startDate(), filter.endDate(),
                filter.page(), RATE_PAGE_SIZE);
        request.setAttribute("rateResult", rateResult);
        request.setAttribute("rates", rateResult.items());
        request.setAttribute("filterRoomTypeId", filter.roomTypeId());
        request.setAttribute("filterStartDate", filter.startDate());
        request.setAttribute("filterEndDate", filter.endDate());
        request.setAttribute("activePage", "pricing");
        request.getRequestDispatcher("/WEB-INF/views/manager/pricing/list.jsp")
                .forward(request, response);
    }

    private RoomRateForm bind(HttpServletRequest request) {
        return new RoomRateForm(longValue(request.getParameter("rateRoomTypeId")),
                date(request.getParameter("rateStartDate"), null),
                date(request.getParameter("rateEndDate"), null),
                decimal(request.getParameter("rateNightlyPrice")),
                request.getParameter("rateStopSell") != null);
    }

    private PricingFilter bindFilter(HttpServletRequest request) {
        LocalDate today = LocalDate.now();
        long roomTypeId = longValue(firstNonBlank(
                request.getParameter("filterRoomTypeId"),
                request.getParameter("roomTypeId")));
        LocalDate start = date(firstNonBlank(
                request.getParameter("filterStartDate"),
                request.getParameter("startDate")), today);
        LocalDate end = date(firstNonBlank(
                request.getParameter("filterEndDate"),
                request.getParameter("endDate")), start.plusDays(30));
        int page = positiveInt(request.getParameter("filterPage"), 1);
        return new PricingFilter(roomTypeId, start, end, page);
    }

    private PricingFilter filterFor(RoomRateForm form) {
        LocalDate start = form.startDate() == null ? LocalDate.now() : form.startDate();
        LocalDate end = form.endDate() == null ? start.plusDays(30) : form.endDate();
        return new PricingFilter(form.roomTypeId(), start, end, 1);
    }

    private String firstNonBlank(String preferred, String fallback) {
        return preferred == null || preferred.isBlank() ? fallback : preferred;
    }

    private long longValue(String value) {
        try { return value == null || value.isBlank() ? 0 : Long.parseLong(value); }
        catch (RuntimeException exception) { return 0; }
    }

    private int positiveInt(String value, int fallback) {
        try { return value == null || value.isBlank()
                ? fallback : Math.max(1, Integer.parseInt(value)); }
        catch (RuntimeException exception) { return fallback; }
    }

    private LocalDate date(String value, LocalDate fallback) {
        try { return value == null || value.isBlank() ? fallback : LocalDate.parse(value); }
        catch (RuntimeException exception) { return fallback; }
    }

    private BigDecimal decimal(String value) {
        try { return value == null || value.isBlank() ? null : new BigDecimal(value); }
        catch (RuntimeException exception) { return null; }
    }

    private void fail(HttpServletResponse response, RuntimeException exception)
            throws IOException {
        getServletContext().log("Manager pricing operation failed", exception);
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                 "MSG21: An unexpected error has occurred. Please try again later.");
    }

    private record PricingFilter(long roomTypeId, LocalDate startDate,
                                 LocalDate endDate, int page) {
    }
}
