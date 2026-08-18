package com.hotel.controller;

import com.hotel.dao.impl.ServiceRequestDaoImpl;
import com.hotel.dto.ServiceRequestDto;
import com.hotel.dto.SessionUser;
import com.hotel.exception.BusinessException;
import com.hotel.exception.DataAccessException;
import com.hotel.service.ServiceRequestService;
import com.hotel.service.impl.ServiceRequestServiceImpl;
import com.hotel.util.DBConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {
        "/staff/service-requests",
        "/staff/service-requests/view",
        "/staff/service-requests/accept",
        "/staff/service-requests/start",
        "/staff/service-requests/complete",
        "/staff/service-requests/cancel"
})
public class ServiceRequestServlet extends HttpServlet {

    private final ServiceRequestService service = new ServiceRequestServiceImpl(
            new ServiceRequestDaoImpl(),
            DBConnection::getConnection
    );

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        SessionUser user = currentUser(request);

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            switch (request.getServletPath()) {
                case "/staff/service-requests" -> list(request, response, user);
                case "/staff/service-requests/view" -> detail(request, response, user);
                default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (NumberFormatException exception) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (DataAccessException exception) {
            getServletContext().log("Unable to load service requests", exception);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        request.setCharacterEncoding("UTF-8");

        SessionUser user = currentUser(request);

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            long requestId = requiredId(request);
            String path = request.getServletPath();

            switch (path) {
                case "/staff/service-requests/accept" -> {
                    service.acceptRequest(requestId, user.userId());
                    flash(request, "flashSuccess",
                            "Đã tiếp nhận yêu cầu dịch vụ.");
                    redirectToDetail(request, response, requestId);
                }

                case "/staff/service-requests/start" -> {
                    service.startRequest(requestId, user.userId());
                    flash(request, "flashSuccess",
                            "Đã bắt đầu thực hiện yêu cầu dịch vụ.");
                    redirectToDetail(request, response, requestId);
                }

                case "/staff/service-requests/complete" -> {
                    service.completeRequest(requestId, user.userId());
                    flash(request, "flashSuccess",
                            "Đã hoàn thành yêu cầu dịch vụ.");
                    redirectToDetail(request, response, requestId);
                }

                case "/staff/service-requests/cancel" -> {
                    service.cancelRequest(
                            requestId,
                            user.userId(),
                            request.getParameter("cancellationReason")
                    );
                    flash(request, "flashSuccess",
                            "Đã hủy yêu cầu dịch vụ.");
                    response.sendRedirect(
                            request.getContextPath() + "/staff/service-requests"
                    );
                }

                default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (NumberFormatException exception) {
            flash(request, "flashError", "Mã yêu cầu không hợp lệ.");
            response.sendRedirect(
                    request.getContextPath() + "/staff/service-requests"
            );
        } catch (BusinessException exception) {
            flash(request, "flashError", exception.getMessage());
            response.sendRedirect(
                    request.getContextPath() + "/staff/service-requests"
            );
        } catch (DataAccessException exception) {
            getServletContext().log("Unable to update service request", exception);
            flash(request, "flashError",
                    "Lỗi hệ thống khi xử lý yêu cầu dịch vụ.");
            response.sendRedirect(
                    request.getContextPath() + "/staff/service-requests"
            );
        }
    }

    private void list(
            HttpServletRequest request,
            HttpServletResponse response,
            SessionUser user
    ) throws ServletException, IOException {
        List<ServiceRequestDto> pendingRequests = service.getPendingRequests();
        List<ServiceRequestDto> myRequests = service.getMyRequests(user.userId());

        request.setAttribute("activePage", "service-requests");
        request.setAttribute("pendingRequests", pendingRequests);
        request.setAttribute("myRequests", myRequests);

        moveFlashToRequest(request);

        request.getRequestDispatcher(
                "/WEB-INF/views/staff/service-requests.jsp"
        ).forward(request, response);
    }

    private void detail(
            HttpServletRequest request,
            HttpServletResponse response,
            SessionUser user
    ) throws ServletException, IOException {
        long requestId = requiredId(request);

        ServiceRequestDto serviceRequest = service.getMyRequests(user.userId())
                .stream()
                .filter(item -> item.requestId() == requestId)
                .findFirst()
                .orElse(null);

        if (serviceRequest == null) {
            flash(request, "flashError",
                    "Không tìm thấy yêu cầu dịch vụ của bạn.");
            response.sendRedirect(
                    request.getContextPath() + "/staff/service-requests"
            );
            return;
        }

        request.setAttribute("activePage", "service-requests");
        request.setAttribute("serviceRequest", serviceRequest);

        moveFlashToRequest(request);

        request.getRequestDispatcher(
                "/WEB-INF/views/staff/service-request-detail.jsp"
        ).forward(request, response);
    }

    private SessionUser currentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        return session != null
                && session.getAttribute("sessionUser") instanceof SessionUser user
                ? user
                : null;
    }

    private long requiredId(HttpServletRequest request) {
        String rawId = request.getParameter("id");

        if (rawId == null || rawId.isBlank()) {
            throw new NumberFormatException();
        }

        long id = Long.parseLong(rawId);

        if (id <= 0) {
            throw new NumberFormatException();
        }

        return id;
    }

    private void redirectToDetail(
            HttpServletRequest request,
            HttpServletResponse response,
            long requestId
    ) throws IOException {
        response.sendRedirect(
                request.getContextPath()
                        + "/staff/service-requests/view?id="
                        + requestId
        );
    }

    private void flash(
            HttpServletRequest request,
            String key,
            String message
    ) {
        request.getSession().setAttribute(key, message);
    }

    private void moveFlashToRequest(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            return;
        }

        moveFlash(session, request, "flashSuccess", "success");
        moveFlash(session, request, "flashError", "error");
    }

    private void moveFlash(
            HttpSession session,
            HttpServletRequest request,
            String sessionKey,
            String requestKey
    ) {
        Object message = session.getAttribute(sessionKey);

        if (message != null) {
            request.setAttribute(requestKey, message);
            session.removeAttribute(sessionKey);
        }
    }
}