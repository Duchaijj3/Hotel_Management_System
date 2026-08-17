package com.hotel.controller;

import com.hotel.dto.SessionUser;
import com.hotel.dto.UserDetailDto;
import com.hotel.dto.UserFormDto;
import com.hotel.dto.UserSearchCriteria;
import com.hotel.exception.DataAccessException;
import com.hotel.exception.ValidationException;
import com.hotel.service.AdminUserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.NoSuchElementException;

@WebServlet(urlPatterns = {AdminRoutes.USERS, "/admin/users/view", "/admin/users/create",
        "/admin/users/edit", "/admin/users/lock", "/admin/users/unlock",
        "/admin/users/reset-password", "/admin/users/clear-lockout"})
public class AdminUserServlet extends HttpServlet {
    private final AdminUserService service = AdminServices.users();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("activePage", "users");
            switch (request.getServletPath()) {
                case AdminRoutes.USERS -> list(request, response);
                case "/admin/users/view" -> detail(request, response);
                case "/admin/users/create" -> {
                    request.setAttribute("mode", "create");
                    request.setAttribute("item", new UserFormDto(null, "", "", "",
                            "RECEPTIONIST", null, "ACTIVE", true, ""));
                    view(request, response, "form.jsp");
                }
                case "/admin/users/edit" -> edit(request, response);
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
            long actorId = actorId(request);
            if ("/admin/users/lock".equals(path)) {
                service.lock(requiredId(request), actorId);
                flash(request, "Account locked.");
                redirectView(request, response, requiredId(request));
                return;
            }
            if ("/admin/users/unlock".equals(path)) {
                service.unlock(requiredId(request), actorId);
                flash(request, "Account unlocked.");
                redirectView(request, response, requiredId(request));
                return;
            }
            if ("/admin/users/clear-lockout".equals(path)) {
                service.clearLockout(requiredId(request), actorId);
                flash(request, "Temporary lockout cleared.");
                redirectView(request, response, requiredId(request));
                return;
            }
            if ("/admin/users/reset-password".equals(path)) {
                boolean sendEmail = "on".equals(request.getParameter("sendEmail"));
                String temporaryPassword = service.resetPassword(
                        requiredId(request), actorId, sendEmail);
                flash(request, sendEmail
                        ? "Password reset and notification queued."
                        : "Password reset. Temporary password: " + temporaryPassword);
                redirectView(request, response, requiredId(request));
                return;
            }

            UserFormDto form = bindForm(request, path.endsWith("/edit"));
            request.setAttribute("item", form);
            long userId;
            if (path.endsWith("/create")) {
                String password = form.password();
                if (password == null || password.isBlank()) {
                    password = generateTemporaryPassword();
                    form = new UserFormDto(form.id(), form.email(), form.fullName(), form.phone(),
                            form.roleCode(), form.departmentCode(), form.statusCode(),
                            form.sendActivationEmail(), password);
                    userId = service.create(form, actorId);
                    flash(request, "Tạo tài khoản thành công. Mật khẩu tạm thời là: " + password);
                } else {
                    userId = service.create(form, actorId);
                    flash(request, "Tạo tài khoản thành công.");
                }
            } else {
                service.update(form, actorId);
                userId = form.id();
                flash(request, "Account updated.");
            }
            redirectView(request, response, userId);
        } catch (ValidationException exception) {
            request.setAttribute("errors", exception.getErrors());
            request.setAttribute("item", bindLenient(request));
            request.setAttribute("mode", path.endsWith("/edit") ? "edit" : "create");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            view(request, response, "form.jsp");
        } catch (NoSuchElementException | NumberFormatException exception) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (DataAccessException exception) {
            fail(response, exception);
        }
    }

    private void list(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int page = parsePositiveInt(request.getParameter("page"), 1);
        UserSearchCriteria criteria = new UserSearchCriteria(
                trim(request, "keyword"), trim(request, "roleCode"),
                trim(request, "statusCode"), page, 20);
        request.setAttribute("criteria", criteria);
        request.setAttribute("result", service.search(criteria));
        view(request, response, "list.jsp");
    }

    private void detail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserDetailDto user = service.detail(requiredId(request)).orElseThrow();
        request.setAttribute("user", user);
        HttpSession session = request.getSession();
        request.setAttribute("flash", session.getAttribute("flash"));
        session.removeAttribute("flash");
        view(request, response, "detail.jsp");
    }

    private void edit(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserDetailDto user = service.detail(requiredId(request)).orElseThrow();
        request.setAttribute("item", new UserFormDto(user.id(), user.email(), user.fullName(),
                user.phone(), user.roleCode(), user.departmentCode(), user.statusCode(), false, user.plainPassword()));
        request.setAttribute("mode", "edit");
        view(request, response, "form.jsp");
    }

    private UserFormDto bindForm(HttpServletRequest request, boolean update) {
        Long id = update ? requiredId(request) : null;
        String email = update ? trim(request, "email") : trim(request, "email");
        return new UserFormDto(id, email, trim(request, "fullName"), trim(request, "phone"),
                trim(request, "roleCode"), trim(request, "departmentCode"),
                trim(request, "statusCode"), "on".equals(request.getParameter("sendActivationEmail")),
                trim(request, "password"));
    }

    private UserFormDto bindLenient(HttpServletRequest request) {
        Long id = safeLong(request.getParameter("id"));
        return new UserFormDto(id, trim(request, "email"), trim(request, "fullName"),
                trim(request, "phone"), trim(request, "roleCode"), trim(request, "departmentCode"),
                trim(request, "statusCode"),
                "on".equals(request.getParameter("sendActivationEmail")),
                trim(request, "password"));
    }

    private long actorId(HttpServletRequest request) {
        SessionUser user = (SessionUser) request.getSession().getAttribute("sessionUser");
        return user.userId();
    }

    private long requiredId(HttpServletRequest request) {
        long id = Long.parseLong(request.getParameter("id"));
        if (id <= 0) {
            throw new NumberFormatException();
        }
        return id;
    }

    private Long safeLong(String value) {
        try {
            return value == null ? null : Long.valueOf(value);
        } catch (RuntimeException exception) {
            return null;
        }
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

    private void redirectView(HttpServletRequest request, HttpServletResponse response, long id)
            throws IOException {
        response.sendRedirect(request.getContextPath() + "/admin/users/view?id=" + id);
    }

    private void view(HttpServletRequest request, HttpServletResponse response, String page)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/admin/users/" + page)
                .forward(request, response);
    }

    private void fail(HttpServletResponse response, DataAccessException exception)
            throws IOException {
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception.getMessage());
    }

    private String generateTemporaryPassword() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
        java.security.SecureRandom random = new java.security.SecureRandom();
        StringBuilder builder = new StringBuilder(12);
        for (int index = 0; index < 12; index++) {
            builder.append(chars.charAt(random.nextInt(chars.length())));
        }
        return builder.toString();
    }
}
