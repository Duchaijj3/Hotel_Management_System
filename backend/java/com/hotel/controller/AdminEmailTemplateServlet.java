package com.hotel.controller;

import com.hotel.dto.EmailTemplateDetailDto;
import com.hotel.dto.EmailTemplateForm;
import com.hotel.dto.EmailTemplateSearchCriteria;
import com.hotel.dto.SessionUser;
import com.hotel.exception.DataAccessException;
import com.hotel.exception.ValidationException;
import com.hotel.service.AdminEmailService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.NoSuchElementException;

@WebServlet(urlPatterns = {
        AdminRoutes.EMAIL_TEMPLATES,
        "/admin/email-templates/view",
        "/admin/email-templates/create",
        "/admin/email-templates/edit",
        "/admin/email-templates/toggle-active",
        "/admin/email-templates/delete"
})
public class AdminEmailTemplateServlet extends HttpServlet {
    private final AdminEmailService service = AdminServices.emails();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("activePage", "email-templates");
            switch (request.getServletPath()) {
                case AdminRoutes.EMAIL_TEMPLATES -> list(request, response);
                case "/admin/email-templates/view" -> detail(request, response);
                case "/admin/email-templates/create" -> {
                    request.setAttribute("mode", "create");
                    request.setAttribute("item", new EmailTemplateForm(null, "", "", "", "", "", "", true));
                    view(request, response, "form.jsp");
                }
                case "/admin/email-templates/edit" -> edit(request, response);
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
            if ("/admin/email-templates/toggle-active".equals(path)) {
                long id = requiredId(request);
                boolean active = "true".equals(request.getParameter("active"));
                service.setTemplateActive(id, active, actorId);
                flash(request, active ? "Email template activated." : "Email template deactivated.");
                redirectView(request, response, id);
                return;
            }
            if ("/admin/email-templates/delete".equals(path)) {
                service.deleteTemplate(requiredId(request), actorId);
                flash(request, "Email template deleted.");
                response.sendRedirect(request.getContextPath() + AdminRoutes.EMAIL_TEMPLATES);
                return;
            }

            EmailTemplateForm form = bindForm(request, path.endsWith("/edit"));
            request.setAttribute("item", form);
            long templateId;
            if (path.endsWith("/create")) {
                templateId = service.createTemplate(form, actorId);
                flash(request, "Email template created.");
            } else {
                service.updateTemplate(form, actorId);
                templateId = form.id();
                flash(request, "Email template updated.");
            }
            redirectView(request, response, templateId);
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
        String keyword = trim(request, "keyword");
        String eventCode = trim(request, "eventCode");
        Boolean active = null;
        String activeStr = request.getParameter("active");
        if (activeStr != null && !activeStr.trim().isEmpty()) {
            active = Boolean.valueOf(activeStr);
        }

        EmailTemplateSearchCriteria criteria = new EmailTemplateSearchCriteria(
                keyword, eventCode, active, page, 20);
        request.setAttribute("criteria", criteria);
        request.setAttribute("result", service.searchTemplates(criteria));
        view(request, response, "list.jsp");
    }

    private void detail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        EmailTemplateDetailDto item = service.templateDetail(requiredId(request)).orElseThrow();
        request.setAttribute("item", item);
        HttpSession session = request.getSession();
        request.setAttribute("flash", session.getAttribute("flash"));
        session.removeAttribute("flash");
        view(request, response, "detail.jsp");
    }

    private void edit(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        EmailTemplateDetailDto item = service.templateDetail(requiredId(request)).orElseThrow();
        request.setAttribute("item", new EmailTemplateForm(item.id(), item.templateCode(),
                item.templateName(), item.eventCode(), item.subjectTemplate(),
                item.bodyHtml(), item.bodyText(), item.isActive()));
        request.setAttribute("mode", "edit");
        view(request, response, "form.jsp");
    }

    private EmailTemplateForm bindForm(HttpServletRequest request, boolean update) {
        Long id = update ? requiredId(request) : null;
        String bodyText = trim(request, "bodyText");
        return new EmailTemplateForm(
                id,
                trim(request, "templateCode"),
                trim(request, "templateName"),
                trim(request, "eventCode"),
                trim(request, "subjectTemplate"),
                bodyText,
                bodyText,
                "on".equals(request.getParameter("active")) || "true".equals(request.getParameter("active"))
        );
    }

    private EmailTemplateForm bindLenient(HttpServletRequest request) {
        Long id = safeLong(request.getParameter("id"));
        String bodyText = trim(request, "bodyText");
        return new EmailTemplateForm(
                id,
                trim(request, "templateCode"),
                trim(request, "templateName"),
                trim(request, "eventCode"),
                trim(request, "subjectTemplate"),
                bodyText,
                bodyText,
                "on".equals(request.getParameter("active")) || "true".equals(request.getParameter("active"))
        );
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
        response.sendRedirect(request.getContextPath() + "/admin/email-templates/view?id=" + id);
    }

    private void view(HttpServletRequest request, HttpServletResponse response, String page)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/admin/email-templates/" + page)
                .forward(request, response);
    }

    private void fail(HttpServletResponse response, DataAccessException exception)
            throws IOException {
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception.getMessage());
    }
}
