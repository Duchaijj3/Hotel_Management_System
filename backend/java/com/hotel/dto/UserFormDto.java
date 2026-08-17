package com.hotel.dto;

public record UserFormDto(Long id, String email, String fullName, String phone,
                          String roleCode, String departmentCode, String statusCode,
                          boolean sendActivationEmail, String password) {
    public Long getId() {
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

    public boolean isSendActivationEmail() {
        return sendActivationEmail;
    }

    public String getPassword() {
        return password;
    }
}
