package com.hotel.dao;

import com.hotel.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserDao {
    Optional<User> findByEmail(String email);

    default void updateLoginFailure(long userId, int failedAttempts,
                                    LocalDateTime lockedUntil) {
        // Optional for read-only/fake implementations.
    }

    default void updateLoginSuccess(long userId, LocalDateTime loggedInAt) {
        // Optional for read-only/fake implementations.
    }
}
