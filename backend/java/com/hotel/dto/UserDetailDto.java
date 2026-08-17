package com.hotel.dto;

import java.time.LocalDateTime;

public record UserDetailDto(long id, String email, String fullName, String phone,
                            String roleCode, String departmentCode, String statusCode,
                            int failedLoginAttempts, LocalDateTime lockedUntil,
                            LocalDateTime lastLoginAt, LocalDateTime createdAt,
                            LocalDateTime updatedAt, String plainPassword) {
    public long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public LocalDateTime getLockedUntil() {
        return lockedUntil;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getPlainPassword() {
        return plainPassword;
    }
}
