package com.hotel.controller;

import com.hotel.dao.impl.UserDaoImpl;
import com.hotel.dto.SessionUser;
import com.hotel.exception.DataAccessException;
import com.hotel.service.AuthService;
import com.hotel.service.AuthenticationResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final Map<String, String> MESSAGES = Map.of(
            "MSG09", "Incorrect email or password. Please try again.",
            "MSG10", "Email and password are required.",
            "MSG12", "Your account is inactive or blocked.",
            "MSG13", "Your account has been locked for 30 minutes.",
            "MSG21", "An unexpected error has occurred. Please try again later."
    );

    private final AuthService auth;

    public LoginServlet() {
        this(new AuthService(new UserDaoImpl()));
    }

    LoginServlet(AuthService auth) {
        this.auth = Objects.requireNonNull(auth);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            AuthenticationResult result = auth.login(
                    request.getParameter("email"), request.getParameter("password"));
            if (result.status() != AuthenticationResult.Status.SUCCESS) {
                showError(request, response, result.messageCode(),
                        HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            HttpSession previous = request.getSession(false);
            if (previous != null) {
                previous.invalidate();
            }
            HttpSession session = request.getSession(true);
            session.setMaxInactiveInterval(30 * 60);
            SessionUser user = result.user();
            session.setAttribute("sessionUser", user);
            response.sendRedirect(request.getContextPath() + landingPage(user.roleCode()));
        } catch (DataAccessException exception) {
            getServletContext().log("Login failed", exception);
            showError(request, response, "MSG21",
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void showError(HttpServletRequest request, HttpServletResponse response,
                           String messageCode, int status)
            throws ServletException, IOException {
        request.setAttribute("errorCode", messageCode);
        request.setAttribute("error", MESSAGES.getOrDefault(messageCode, MESSAGES.get("MSG21")));
        request.setAttribute("email", request.getParameter("email"));
        response.setStatus(status);
        doGet(request, response);
    }

    private String landingPage(String roleCode) {
        return switch (roleCode) {
            case "MANAGER" -> ManagerRoutes.DASHBOARD;
            case "RECEPTIONIST" -> "/receptionist/customers";
            default -> "/";
        };
    }
}
