package com.hotel.controller;

import com.hotel.dao.impl.CustomerDaoImpl;
import com.hotel.dto.CustomerDetailDto;
import com.hotel.dto.CustomerFormDto;
import com.hotel.dto.CustomerSearchCriteria;
import com.hotel.dto.SessionUser;
import com.hotel.exception.DataAccessException;
import com.hotel.exception.ValidationException;
import com.hotel.service.CustomerService;
import com.hotel.service.impl.CustomerServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.NoSuchElementException;

@WebServlet(urlPatterns = {"/receptionist/customers", "/receptionist/customers/view",
        "/receptionist/customers/create", "/receptionist/customers/edit",
        "/receptionist/customers/update"})
public class ReceptionistCustomerServlet extends HttpServlet {
    private final CustomerService service = new CustomerServiceImpl(new CustomerDaoImpl());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            switch (request.getServletPath()) {
                case "/receptionist/customers" -> list(request, response);
                case "/receptionist/customers/view" -> detail(request, response);
                case "/receptionist/customers/create" -> {
                    request.setAttribute("mode", "create");
                    view(request, response, "form.jsp");
                }
                case "/receptionist/customers/edit" -> edit(request, response);
                default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (NoSuchElementException | NumberFormatException exception) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (DataAccessException exception) {
            getServletContext().log("Customer operation failed", exception);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String mode = request.getServletPath().endsWith("create") ? "create" : "edit";
        try {
            CustomerFormDto form = bindForm(request, mode.equals("edit"));
            request.setAttribute("customer", form);
            long customerId;
            if (mode.equals("create")) {
                SessionUser user = (SessionUser) request.getSession().getAttribute("sessionUser");
                customerId = service.create(form, user.userId());
                flash(request, "Walk-in customer created.");
            } else {
                service.update(form);
                customerId = form.id();
                flash(request, "Customer updated.");
            }
            response.sendRedirect(request.getContextPath()
                    + "/receptionist/customers/view?id=" + customerId);
        } catch (ValidationException exception) {
            request.setAttribute("customer", bindLenient(request));
            request.setAttribute("errors", exception.getErrors());
            request.setAttribute("mode", mode);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            view(request, response, "form.jsp");
        } catch (DataAccessException exception) {
            getServletContext().log("Customer write failed", exception);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void list(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int page = parsePositiveInt(request.getParameter("page"), 1);
        CustomerSearchCriteria criteria = new CustomerSearchCriteria(
                request.getParameter("keyword"), request.getParameter("status"), page, 20);
        request.setAttribute("criteria", criteria);
        request.setAttribute("result", service.search(criteria));
        view(request, response, "list.jsp");
    }

    private void detail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        CustomerDetailDto customer = service.detail(requiredId(request)).orElseThrow();
        request.setAttribute("customer", customer);
        HttpSession session = request.getSession();
        request.setAttribute("flash", session.getAttribute("flash"));
        session.removeAttribute("flash");
        view(request, response, "detail.jsp");
    }

    private void edit(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        CustomerDetailDto customer = service.detail(requiredId(request)).orElseThrow();
        request.setAttribute("customer", new CustomerFormDto(customer.id(), customer.fullName(),
                customer.email(), customer.phone(), customer.dateOfBirth(), customer.idType(),
                customer.idNumber(), customer.nationality(), customer.address(), customer.updatedAt()));
        request.setAttribute("mode", "edit");
        view(request, response, "form.jsp");
    }

    private CustomerFormDto bindForm(HttpServletRequest request, boolean update)
            throws ValidationException {
        Map<String, String> error;
        Long id = null;
        LocalDateTime version = null;
        LocalDate dateOfBirth = null;
        try {
            if (update) {
                id = Long.valueOf(request.getParameter("id"));
                if (id <= 0) throw new NumberFormatException();
                version = LocalDateTime.parse(request.getParameter("version"));
            }
        } catch (RuntimeException exception) {
            error = Map.of("general", "Invalid customer or update version. Reload the form.");
            throw new ValidationException(error);
        }
        try {
            String rawDate = request.getParameter("dateOfBirth");
            if (rawDate != null && !rawDate.isBlank()) {
                dateOfBirth = LocalDate.parse(rawDate);
            }
        } catch (DateTimeParseException exception) {
            throw new ValidationException(Map.of("dateOfBirth", "Invalid date of birth."));
        }
        return new CustomerFormDto(id, trim(request, "fullName"), trim(request, "email"),
                trim(request, "phone"), dateOfBirth, trim(request, "idDocumentType"),
                trim(request, "idDocumentNumber"), trim(request, "nationality"),
                trim(request, "address"), version);
    }

    private CustomerFormDto bindLenient(HttpServletRequest request) {
        return new CustomerFormDto(safeLong(request.getParameter("id")), trim(request, "fullName"),
                trim(request, "email"), trim(request, "phone"), safeDate(request.getParameter("dateOfBirth")),
                trim(request, "idDocumentType"), trim(request, "idDocumentNumber"),
                trim(request, "nationality"), trim(request, "address"),
                safeDateTime(request.getParameter("version")));
    }

    private long requiredId(HttpServletRequest request) {
        long id = Long.parseLong(request.getParameter("id"));
        if (id <= 0) throw new NumberFormatException();
        return id;
    }

    private int parsePositiveInt(String value, int fallback) {
        try {
            return Math.max(1, Integer.parseInt(value));
        } catch (RuntimeException exception) {
            return fallback;
        }
    }

    private Long safeLong(String value) {
        try { return value == null ? null : Long.valueOf(value); }
        catch (RuntimeException exception) { return null; }
    }

    private LocalDate safeDate(String value) {
        try { return value == null || value.isBlank() ? null : LocalDate.parse(value); }
        catch (RuntimeException exception) { return null; }
    }

    private LocalDateTime safeDateTime(String value) {
        try { return value == null || value.isBlank() ? null : LocalDateTime.parse(value); }
        catch (RuntimeException exception) { return null; }
    }

    private String trim(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }

    private void flash(HttpServletRequest request, String value) {
        request.getSession().setAttribute("flash", value);
    }

    private void view(HttpServletRequest request, HttpServletResponse response, String name)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/receptionist/customers/" + name)
                .forward(request, response);
    }
}
