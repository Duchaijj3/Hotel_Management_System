package com.hotel.controller;

import com.hotel.dto.ManagerDashboardCard;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Set;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ManagerDashboardServletTest {
    @Test
    void dashboardProvidesNavigationToEveryImplementedSprintOneFunction()
            throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        when(request.getRequestDispatcher(
                "/WEB-INF/views/manager/dashboard.jsp")).thenReturn(dispatcher);
        ArgumentCaptor<Object> cardsCaptor = ArgumentCaptor.forClass(Object.class);

        new ManagerDashboardServlet().doGet(request, response);

        verify(request).setAttribute("activePage", "dashboard");
        verify(request).setAttribute("pageTitle", "Manager Dashboard");
        verify(request).setAttribute(eq("dashboardCards"), cardsCaptor.capture());
        verify(dispatcher).forward(request, response);

        @SuppressWarnings("unchecked")
        List<ManagerDashboardCard> cards = (List<ManagerDashboardCard>) cardsCaptor.getValue();
        assertEquals(List.of("Room Types", "Amenities", "Rooms", "Room Pricing"),
                cards.stream().map(ManagerDashboardCard::title).toList());
        assertEquals(List.of(ManagerRoutes.ROOM_TYPES, ManagerRoutes.ROOM_TYPES,
                        ManagerRoutes.ROOMS, ManagerRoutes.PRICING),
                cards.stream().map(ManagerDashboardCard::targetPath).toList());
        assertTrue(cards.stream().allMatch(ManagerDashboardCard::enabled));
    }

    @Test
    void everyDashboardTargetIsRegisteredByAnExistingServlet() {
        assertTrue(routesOf(ManagerDashboardServlet.class).contains(ManagerRoutes.DASHBOARD));
        assertTrue(routesOf(ManagerRoomTypeServlet.class).contains(ManagerRoutes.ROOM_TYPES));
        assertTrue(routesOf(ManagerRoomServlet.class).contains(ManagerRoutes.ROOMS));
        assertTrue(routesOf(ManagerPricingServlet.class).contains(ManagerRoutes.PRICING));
    }

    private Set<String> routesOf(Class<?> servletClass) {
        WebServlet mapping = servletClass.getAnnotation(WebServlet.class);
        return Stream.concat(Arrays.stream(mapping.value()),
                        Arrays.stream(mapping.urlPatterns()))
                .collect(Collectors.toSet());
    }
}
