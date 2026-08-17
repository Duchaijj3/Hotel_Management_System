package com.hotel.controller;

import com.hotel.dto.ProfileFormDto;
import com.hotel.dto.SessionUser;
import com.hotel.dto.UserDetailDto;
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

@WebServlet(urlPatterns = {AdminRoutes.PROFILE})
public class AdminProfileServlet extends HttpServlet {
    private final AdminUserService service = AdminServices.users();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("activePage", "profile");
            request.setAttribute("pageTitle", "Personal Profile");
            long actorId = actorId(request);
            UserDetailDto user = service.profile(actorId).orElseThrow();
            request.setAttribute("user", user);
            
            HttpSession session = request.getSession();
            request.setAttribute("flash", session.getAttribute("flash"));
            session.removeAttribute("flash");
            
            view(request, response, "view.jsp");
        } catch (NoSuchElementException | NumberFormatException exception) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (DataAccessException exception) {
            fail(response, exception);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            long actorId = actorId(request);
            ProfileFormDto form = new ProfileFormDto(
                    trim(request, "fullName"),
                    trim(request, "phone"),
                    trim(request, "currentPassword"),
                    trim(request, "newPassword"),
                    trim(request, "confirmPassword")
            );
            
            service.updateProfile(actorId, form);
            flash(request, "Profile updated successfully.");
            response.sendRedirect(request.getContextPath() + AdminRoutes.PROFILE);
        } catch (ValidationException exception) {
            long actorId = actorId(request);
            UserDetailDto user = service.profile(actorId).orElseThrow();
            request.setAttribute("user", user);
            request.setAttribute("activePage", "profile");
            request.setAttribute("errors", exception.getErrors());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            view(request, response, "view.jsp");
        } catch (NoSuchElementException | NumberFormatException exception) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (DataAccessException exception) {
            fail(response, exception);
        }
    }

    private long actorId(HttpServletRequest request) {
        SessionUser user = (SessionUser) request.getSession().getAttribute("sessionUser");
        return user.userId();
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
        request.getRequestDispatcher("/WEB-INF/views/admin/profile/" + page)
                .forward(request, response);
    }

    private void fail(HttpServletResponse response, DataAccessException exception)
            throws IOException {
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception.getMessage());
    }
}
