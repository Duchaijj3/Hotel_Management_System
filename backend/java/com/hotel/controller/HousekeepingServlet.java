package com.hotel.controller;

import com.hotel.dao.impl.HousekeepingTaskDaoImpl;
import com.hotel.dto.HousekeepingTaskDto;
import com.hotel.dto.SessionUser;
import com.hotel.exception.BusinessException;
import com.hotel.exception.DataAccessException;
import com.hotel.service.HousekeepingService;
import com.hotel.service.impl.HousekeepingServiceImpl;
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
        "/staff/housekeeping",
        "/staff/housekeeping/view",
        "/staff/housekeeping/accept",
        "/staff/housekeeping/complete"
})
public class HousekeepingServlet extends HttpServlet {

    private final HousekeepingService housekeepingService =
            new HousekeepingServiceImpl(
                    new HousekeepingTaskDaoImpl(),
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
                case "/staff/housekeeping" -> list(request, response, user.userId());
                case "/staff/housekeeping/view" -> detail(request, response, user.userId());
                default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (NumberFormatException exception) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (DataAccessException exception) {
            getServletContext().log("Unable to load housekeeping tasks", exception);
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
            long taskId = requiredId(request);

            switch (request.getServletPath()) {
                case "/staff/housekeeping/accept" -> {
                    housekeepingService.acceptTask(taskId, user.userId());

                    flash(request, "flashSuccess",
                            "Đã nhận việc và bắt đầu dọn phòng. "
                                    + "Trạng thái phòng đã chuyển sang CLEANING.");

                    response.sendRedirect(
                            request.getContextPath()
                                    + "/staff/housekeeping/view?id="
                                    + taskId
                    );
                }

                case "/staff/housekeeping/complete" -> {
                    housekeepingService.completeTask(taskId, user.userId());

                    flash(request, "flashSuccess",
                            "Đã hoàn thành dọn phòng. "
                                    + "Trạng thái phòng đã chuyển sang CLEAN.");

                    response.sendRedirect(
                            request.getContextPath() + "/staff/housekeeping"
                    );
                }

                default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (NumberFormatException exception) {
            flash(request, "flashError", "Mã công việc không hợp lệ.");

            response.sendRedirect(
                    request.getContextPath() + "/staff/housekeeping"
            );
        } catch (BusinessException exception) {
            flash(request, "flashError", exception.getMessage());

            response.sendRedirect(
                    request.getContextPath() + "/staff/housekeeping"
            );
        } catch (DataAccessException exception) {
            getServletContext().log("Unable to update housekeeping task", exception);

            flash(request, "flashError",
                    "Lỗi hệ thống khi xử lý công việc dọn phòng.");

            response.sendRedirect(
                    request.getContextPath() + "/staff/housekeeping"
            );
        }
    }

    private void list(
            HttpServletRequest request,
            HttpServletResponse response,
            long staffId
    ) throws ServletException, IOException {
        List<HousekeepingTaskDto> pendingTasks =
                housekeepingService.getPendingTasks();

        List<HousekeepingTaskDto> myTasks =
                housekeepingService.getMyTasks(staffId);

        long totalInProgress = myTasks.stream()
                .filter(task -> "IN_PROGRESS".equals(task.statusCode()))
                .count();

        long totalCompleted = myTasks.stream()
                .filter(task -> "COMPLETED".equals(task.statusCode()))
                .count();

        request.setAttribute("activePage", "housekeeping");
        request.setAttribute("pendingTasks", pendingTasks);
        request.setAttribute("myTasks", myTasks);
        request.setAttribute("totalPending", pendingTasks.size());
        request.setAttribute("totalInProgress", totalInProgress);
        request.setAttribute("totalCompleted", totalCompleted);

        moveFlashToRequest(request);

        view(request, response, "housekeeping.jsp");
    }

    private void detail(
            HttpServletRequest request,
            HttpServletResponse response,
            long staffId
    ) throws ServletException, IOException {
        long taskId = requiredId(request);

        HousekeepingTaskDto task = housekeepingService.getMyTasks(staffId)
                .stream()
                .filter(item -> item.taskId() == taskId)
                .findFirst()
                .orElse(null);

        if (task == null) {
            flash(request, "flashError",
                    "Không tìm thấy công việc dọn phòng của bạn.");

            response.sendRedirect(
                    request.getContextPath() + "/staff/housekeeping"
            );
            return;
        }

        request.setAttribute("activePage", "housekeeping");
        request.setAttribute("task", task);

        moveFlashToRequest(request);

        view(request, response, "housekeeping-detail.jsp");
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

    private void view(
            HttpServletRequest request,
            HttpServletResponse response,
            String pageName
    ) throws ServletException, IOException {
        request.getRequestDispatcher(
                "/WEB-INF/views/staff/" + pageName
        ).forward(request, response);
    }
}