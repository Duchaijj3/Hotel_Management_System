package com.hotel.service;

import com.hotel.dao.UserDao;
import com.hotel.dto.SessionUser;
import com.hotel.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Locale;

public class AuthService {
    private final UserDao users;
    private final Clock clock;

    public AuthService(UserDao users) {
        this(users, Clock.systemUTC());
    }

    public AuthService(UserDao users, Clock clock) {
        this.users = users;
        this.clock = clock;
    }

    public AuthenticationResult login(String email, String password) {
        String normalized = email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
        if (normalized.isBlank() || password == null || password.isBlank()) {
            return AuthenticationResult.failure(AuthenticationResult.Status.REQUIRED, "MSG10");
        }

        User user = users.findByEmail(normalized).orElse(null);
        if (user == null || user.getPasswordHash() == null) {
            return AuthenticationResult.failure(
                    AuthenticationResult.Status.INVALID_CREDENTIALS, "MSG09");
        }
        if (!"ACTIVE".equals(user.getStatusCode())) {
            return AuthenticationResult.failure(
                    AuthenticationResult.Status.INACTIVE_OR_BLOCKED, "MSG12");
        }

        LocalDateTime now = LocalDateTime.now(clock);
        if (user.getLockedUntil() != null && now.isBefore(user.getLockedUntil())) {
            return AuthenticationResult.failure(
                    AuthenticationResult.Status.TEMPORARILY_LOCKED, "MSG13");
        }

        try {
            if (!BCrypt.checkpw(password, user.getPasswordHash())) {
                return failedLogin(user, now);
            }
        } catch (IllegalArgumentException invalidHash) {
            return failedLogin(user, now);
        }

        users.updateLoginSuccess(user.getUserId(), now);
        SessionUser sessionUser = new SessionUser(user.getUserId(), user.getEmail(),
                user.getFullName(), user.getRoleCode());
        return AuthenticationResult.success(sessionUser);
    }

    public SessionUser authenticate(String email, String password) {
        return login(email, password).user();
    }

    private AuthenticationResult failedLogin(User user, LocalDateTime now) {
        boolean previousLockExpired = user.getLockedUntil() != null
                && !now.isBefore(user.getLockedUntil());
        int failedAttempts = previousLockExpired ? 1 : user.getFailedLoginAttempts() + 1;
        LocalDateTime lockedUntil = failedAttempts >= 5 ? now.plusMinutes(30) : null;
        users.updateLoginFailure(user.getUserId(), failedAttempts, lockedUntil);
        if (lockedUntil != null) {
            return AuthenticationResult.failure(
                    AuthenticationResult.Status.TEMPORARILY_LOCKED, "MSG13");
        }
        return AuthenticationResult.failure(
                AuthenticationResult.Status.INVALID_CREDENTIALS, "MSG09");
    }
}
