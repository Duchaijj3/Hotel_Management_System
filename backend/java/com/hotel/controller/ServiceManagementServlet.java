package java.com.hotel.controller;

// File: src/main/java/com/hotel/controller/ServiceManagementServlet.java


import java.com.hotel.model.HotelService;
import java.com.hotel.service.ServiceManagementService;
import java.com.hotel.service.impl.ServiceManagementServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Servlet for managing hotel services (add, update, view, soft delete)
 * Handles requests: /services/*
 */
@WebServlet("/services/*")
public class ServiceManagementServlet extends HttpServlet {

    private ServiceManagementService serviceService;

    @Override
    public void init() throws ServletException {
        this.serviceService = new ServiceManagementServiceImpl();
    }

    // GET: Display service list, add form, or edit form
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/list")) {
                // Display all services
                List<HotelService> services = serviceService.getAllServices();
                request.setAttribute("services", services);
                request.getRequestDispatcher("/WEB-INF/templates/services/list.jsp").forward(request, response);

            } else if (pathInfo.equals("/add")) {
                // Show add service form
                request.getRequestDispatcher("/WEB-INF/templates/services/add.jsp").forward(request, response);

            } else if (pathInfo.startsWith("/edit/")) {
                // Show edit service form
                long serviceId = parseLongSafely(pathInfo.substring(6));
                HotelService service = serviceService.getServiceById(serviceId);

                request.setAttribute("service", service);
                request.getRequestDispatcher("/WEB-INF/templates/services/edit.jsp").forward(request, response);

            } else {
                // Fallback for unknown URLs
                response.sendRedirect(request.getContextPath() + "/services/list");
            }

        } catch (Exception e) {
            request.setAttribute("error", "Lỗi: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/templates/error.jsp").forward(request, response);
        }
    }

    // POST: Process Add or Update service
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo != null && pathInfo.equals("/add")) {
                // Add new service
                String serviceCode = request.getParameter("serviceCode");
                String serviceName = request.getParameter("serviceName");
                String unitName = request.getParameter("unitName");
                BigDecimal unitPrice = parseBigDecimalSafely(request.getParameter("unitPrice"));
                String description = request.getParameter("description");

                serviceService.addService(serviceCode, serviceName, unitName, unitPrice, description);
                response.sendRedirect(request.getContextPath() + "/services/list");

            } else if (pathInfo != null && pathInfo.startsWith("/update/")) {
                // Update existing service
                long serviceId = parseLongSafely(pathInfo.substring(8));
                String serviceName = request.getParameter("serviceName");
                String unitName = request.getParameter("unitName");
                BigDecimal unitPrice = parseBigDecimalSafely(request.getParameter("unitPrice"));
                String description = request.getParameter("description");

                serviceService.updateService(serviceId, serviceName, unitName, unitPrice, description);
                response.sendRedirect(request.getContextPath() + "/services/list");

            } else {
                response.sendRedirect(request.getContextPath() + "/services/list");
            }

        } catch (Exception e) {
            request.setAttribute("error", "Lỗi xử lý dịch vụ: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/templates/error.jsp").forward(request, response);
        }
    }

    // DELETE: Soft delete service (set is_active = false)
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            if (pathInfo != null && pathInfo.startsWith("/delete/")) {
                long serviceId = parseLongSafely(pathInfo.substring(8));
                serviceService.deleteService(serviceId);

                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("{\"success\": true, \"message\": \"Đã xóa dịch vụ thành công\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Đường dẫn không hợp lệ\"}");
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // --- Helper Methods ---

    private long parseLongSafely(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Mã ID không hợp lệ: " + value);
        }
    }

    private BigDecimal parseBigDecimalSafely(String value) {
        try {
            if (value == null || value.trim().isEmpty()) {
                return BigDecimal.ZERO;
            }
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Đơn giá không đúng định dạng số: " + value);
        }
    }
}