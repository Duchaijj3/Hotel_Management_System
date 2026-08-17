package com.hotel.dto;

public record ProfileFormDto(String fullName, String phone, String currentPassword,
                             String newPassword, String confirmPassword) {
    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }
}
