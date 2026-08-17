package com.hotel.controller;

import com.hotel.dao.impl.AdminUserDaoImpl;
import com.hotel.dao.impl.EmailDeliveryDaoImpl;
import com.hotel.dao.impl.EmailTemplateDaoImpl;
import com.hotel.service.AdminEmailService;
import com.hotel.service.AdminUserService;
import com.hotel.service.impl.AdminEmailServiceImpl;
import com.hotel.service.impl.AdminUserServiceImpl;

public final class AdminServices {
    private static final AdminEmailService EMAILS = new AdminEmailServiceImpl(
            new EmailTemplateDaoImpl(), new EmailDeliveryDaoImpl());
    private static final AdminUserService USERS = new AdminUserServiceImpl(
            new AdminUserDaoImpl(), EMAILS);

    private AdminServices() {
    }

    public static AdminUserService users() {
        return USERS;
    }

    public static AdminEmailService emails() {
        return EMAILS;
    }
}
