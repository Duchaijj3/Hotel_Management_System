package com.hotel.controller;

import com.hotel.dto.HousekeepingTaskDto;
import com.hotel.exception.BusinessException;
import com.hotel.service.HousekeepingService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet({"/staff/housekeeping", "/staff/housekeeping/accept", "/staff/housekeeping/start", "/staff/housekeeping/complete"})
public class HousekeepingServlet extends HttpServlet {

    private HousekeepingService housekeepingService;

    @Override
    public void init() throws ServletException {
        // Khởi tạo HousekeepingService (Kết nối DAO và DB) tương tự như các Controller trước
        // housekeepingService = new HousekeepingServiceImpl(...);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        // Giả lập lấy ID của nhân viên đang đăng nhập
        long staffId = session.getAttribute("userId") != null ? (Long) session.getAttribute("userId") : 2L;

        // Lấy danh sách nhiệm vụ dọn dẹp
        List<HousekeepingTaskDto> pendingTasks = housekeepingService.getPendingTasks();
        List<HousekeepingTaskDto> myTasks = housekeepingService.getMyTasks(staffId);

        request.setAttribute("pendingTasks", pendingTasks);
        request.setAttribute("myTasks", myTasks);

        // Chuyển flash message thành request attribute để hiển thị
        if (session.getAttribute("success") != null) {
            request.setAttribute("success", session.getAttribute("success"));
            session.removeAttribute("success");
        }
        if (session.getAttribute("error") != null) {
            request.setAttribute("error", session.getAttribute("error"));
            session.removeAttribute("error");
        }

        request.getRequestDispatcher("/WEB-INF/views/staff/housekeeping.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        long staffId = session.getAttribute("userId") != null ? (Long) session.getAttribute("userId") : 2L;
        String action = request.getServletPath();

        try {
            long taskId = Long.parseLong(request.getParameter("taskId"));

            if ("/staff/housekeeping/accept".equals(action)) {
                housekeepingService.acceptTask(taskId, staffId);
                session.setAttribute("success", "Đã nhận ca dọn phòng!");
            } else if ("/staff/housekeeping/start".equals(action)) {
                housekeepingService.startTask(taskId, staffId);
                session.setAttribute("success", "Đã bắt đầu dọn phòng! Trạng thái phòng đã chuyển sang CLEANING.");
            } else if ("/staff/housekeeping/complete".equals(action)) {
                housekeepingService.completeTask(taskId, staffId);
                session.setAttribute("success", "Hoàn tất dọn phòng! Trạng thái phòng đã chuyển về CLEAN.");
            }
        } catch (BusinessException e) {
            session.setAttribute("error", e.getMessage());
        } catch (Exception e) {
            session.setAttribute("error", "Lỗi dữ liệu đầu vào: " + e.getMessage());
        }

        // Luôn Redirect về trang danh sách (PRG Pattern)
        response.sendRedirect(request.getContextPath() + "/staff/housekeeping");
    }
}