package com.hotel.dto;

public record UserSummaryDto(long id, String email, String fullName, String phone,
                             String roleCode, String departmentCode, String statusCode) {
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
}
