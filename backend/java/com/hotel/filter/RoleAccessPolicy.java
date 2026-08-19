package com.hotel.filter;

public final class RoleAccessPolicy {

    private RoleAccessPolicy() {}

    public static boolean canAccess(String roleCode, String applicationPath) {
// THÊM DÒNG NÀY ĐỂ DEBUG:
        System.out.println(">>> DEBUG FILTER | roleCode: [" + roleCode + "] | path: [" + applicationPath + "]");

        if (roleCode == null || applicationPath == null) {
            return false;
        }
        // ... các đoạn code bên dưới giữ nguyên

        String role = roleCode.trim();
        String path = applicationPath.toLowerCase();

        if (path.startsWith("/admin/")) {
            return "ADMIN".equalsIgnoreCase(role);
        }
        if (path.startsWith("/manager/")) {
            return "MANAGER".equalsIgnoreCase(role);
        }
        if (path.startsWith("/receptionist/")) {
            return "RECEPTIONIST".equalsIgnoreCase(role);
        }
        if (path.startsWith("/staff/housekeeping")) {
            return "HOUSEKEEPING_STAFF".equalsIgnoreCase(role);
        }
        if (path.startsWith("/staff/service-requests")) {
            return "SERVICE_STAFF".equalsIgnoreCase(role);
        }

        return false;
    }
}