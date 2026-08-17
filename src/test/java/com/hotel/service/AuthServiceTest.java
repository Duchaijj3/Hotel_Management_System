package com.hotel.service;

import com.hotel.dao.UserDao;
import com.hotel.model.User;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AuthServiceTest {
    private static final Clock CLOCK = Clock.fixed(
            Instant.parse("2026-08-17T03:00:00Z"), ZoneOffset.UTC);

    @Test
    void activeManagerCanLogInWithValidCredentials() {
        User manager = manager("ACTIVE");
        UserDao users = email -> Optional.of(manager);

        AuthenticationResult result = new AuthService(users, CLOCK)
                .login(" MANAGER@HOTEL.LOCAL ", "correct-password");

        assertEquals(AuthenticationResult.Status.SUCCESS, result.status());
        assertNotNull(result.user());
        assertEquals("MANAGER", result.user().roleCode());
    }

    @Test
    void emptyCredentialsUseRequiredFieldsMessage() {
        UserDao users = email -> Optional.empty();

        AuthenticationResult result = new AuthService(users, CLOCK).login(" ", "");

        assertEquals(AuthenticationResult.Status.REQUIRED, result.status());
        assertEquals("MSG10", result.messageCode());
    }

    @Test
    void inactiveManagerIsDeniedWithAccountStatusMessage() {
        UserDao users = email -> Optional.of(manager("INACTIVE"));

        AuthenticationResult result = new AuthService(users, CLOCK)
                .login("manager@hotel.local", "correct-password");

        assertEquals(AuthenticationResult.Status.INACTIVE_OR_BLOCKED, result.status());
        assertEquals("MSG12", result.messageCode());
    }

    @Test
    void fifthConsecutiveFailureLocksAccountForThirtyMinutes() {
        InMemoryUserDao users = new InMemoryUserDao(manager("ACTIVE"));
        AuthService service = new AuthService(users, CLOCK);

        for (int attempt = 1; attempt < 5; attempt++) {
            assertEquals(AuthenticationResult.Status.INVALID_CREDENTIALS,
                    service.login("manager@hotel.local", "wrong-password").status());
        }
        AuthenticationResult fifth = service.login(
                "manager@hotel.local", "wrong-password");

        assertEquals(AuthenticationResult.Status.TEMPORARILY_LOCKED, fifth.status());
        assertEquals("MSG13", fifth.messageCode());
        assertEquals(5, users.user.getFailedLoginAttempts());
        assertEquals(LocalDateTime.of(2026, 8, 17, 3, 30), users.user.getLockedUntil());
    }

    private User manager(String status) {
        User user = new User();
        user.setUserId(7);
        user.setEmail("manager@hotel.local");
        user.setPasswordHash(BCrypt.hashpw("correct-password", BCrypt.gensalt(4)));
        user.setFullName("Hotel Manager");
        user.setRoleCode("MANAGER");
        user.setStatusCode(status);
        return user;
    }

    private static final class InMemoryUserDao implements UserDao {
        private final User user;

        private InMemoryUserDao(User user) {
            this.user = user;
        }

        @Override
        public Optional<User> findByEmail(String email) {
            return Optional.of(user);
        }

        @Override
        public void updateLoginFailure(long userId, int failedAttempts,
                                       LocalDateTime lockedUntil) {
            user.setFailedLoginAttempts(failedAttempts);
            user.setLockedUntil(lockedUntil);
        }

        @Override
        public void updateLoginSuccess(long userId, LocalDateTime loggedInAt) {
            user.setFailedLoginAttempts(0);
            user.setLockedUntil(null);
            user.setLastLoginAt(loggedInAt);
        }
    }
}
