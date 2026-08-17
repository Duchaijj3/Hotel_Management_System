package com.hotel.service;

import com.hotel.dto.SessionUser;

public record AuthenticationResult(Status status, SessionUser user, String messageCode) {
    public enum Status {
        SUCCESS,
        REQUIRED,
        INVALID_CREDENTIALS,
        INACTIVE_OR_BLOCKED,
        TEMPORARILY_LOCKED
    }

    public static AuthenticationResult success(SessionUser user) {
        return new AuthenticationResult(Status.SUCCESS, user, null);
    }

    public static AuthenticationResult failure(Status status, String messageCode) {
        return new AuthenticationResult(status, null, messageCode);
    }
}
