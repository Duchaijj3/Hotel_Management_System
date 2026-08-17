package com.hotel.controller;

import com.hotel.dao.impl.ServiceRequestDaoImpl;
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
import java.io.IOException;

@WebServlet({"/staff/tasks", "/staff/tasks/accept", "/staff/tasks/start", "/staff/tasks/complete", "/staff/tasks/cancel"})
public class StaffTaskServlet extends HttpServlet {

    // Inject các implementation trực tiếp tại đây theo chuẩn mới
    private final ServiceRequestService service = new ServiceRequestServiceImpl(
            new ServiceRequestDaoImpl(), DBConnection::getConnection
    );

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionUser user = (SessionUser) request.getSession().getAttribute("sessionUser");
        try {
            // Đổ dữ liệu 2 danh sách: Việc đang chờ, và Việc của tôi
            request.setAttribute("pendingTasks", service.getPendingRequests());
            request.setAttribute("myTasks", service.getMyTasks(user.userId()));

            // Xử lý flash message để hiện thông báo trên UI
            String[] flashKeys = {"flashSuccess", "flashError"};
            for (String key : flashKeys) {
                Object msg = request.getSession().getAttribute(key);
                if (msg != null) {
                    request.setAttribute(key.equals("flashSuccess") ? "success" : "error", msg);
                    request.getSession().removeAttribute(key);
                }
            }

            request.getRequestDispatcher("/WEB-INF/views/staff/tasks.jsp").forward(request, response);

        } catch (DataAccessException e) {
            getServletContext().log("Failed to load staff tasks", e);
            response.sendError(500);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionUser user = (SessionUser) request.getSession().getAttribute("sessionUser");
        String path = request.getServletPath();


        try {
            long requestId = Long.parseLong(request.getParameter("requestId"));

            switch (path) {

                case "/staff/tasks/accept":
                    service.acceptTask(requestId, user.userId());
                    flash(request, "flashSuccess", "Đã nhận công việc thành công.");
                    break;
                case "/staff/tasks/start":
                    service.startTask(requestId, user.userId());
                    flash(request, "flashSuccess", "Đã bắt đầu thực hiện công việc.");
                    break;
                case "/staff/tasks/complete":
                    service.completeTask(requestId, user.userId());
                    flash(request, "flashSuccess", "Đã hoàn thành công việc! Hệ thống đã ghi nhận phí dịch vụ.");
                    break;
                case "/staff/tasks/cancel":
                    String reason = request.getParameter("reason");
                    service.cancelTask(requestId, user.userId(), reason);
                    flash(request, "flashSuccess", "Đã hủy yêu cầu thành công!");
                    break;
                default:
                    response.sendError(404);
                    return;
            }
        } catch (NumberFormatException e) {
            flash(request, "flashError", "ID công việc không hợp lệ.");
        } catch (BusinessException e) {
            flash(request, "flashError", e.getMessage());
        } catch (DataAccessException e) {
            getServletContext().log("Task operation failed", e);
            flash(request, "flashError", "Lỗi hệ thống khi xử lý công việc.");
        }


        // Post-Redirect-Get (PRG)
        response.sendRedirect(request.getContextPath() + "/staff/tasks");

    }


    private void flash(HttpServletRequest request, String key, String message) {
        request.getSession().setAttribute(key, message);
    }
}