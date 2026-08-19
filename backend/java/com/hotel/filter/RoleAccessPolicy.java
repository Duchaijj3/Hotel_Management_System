package com.hotel.filter;

public final class RoleAccessPolicy {

    private RoleAccessPolicy() {
    }

    public static boolean canAccess(String roleCode, String applicationPath) {
        if (roleCode == null || applicationPath == null) {
            return false;
        }

        if (applicationPath.startsWith("/admin/")) {
            return "ADMIN".equals(roleCode);
        }

        if (applicationPath.startsWith("/manager/")) {
            return "MANAGER".equals(roleCode);
        }

        if (applicationPath.startsWith("/receptionist/")) {
            return "RECEPTIONIST".equals(roleCode);
        }

        if (applicationPath.startsWith("/staff/housekeeping")) {
            return "HOUSEKEEPING_STAFF".equals(roleCode);
        }

        if (applicationPath.startsWith("/staff/service-requests")) {
            return "SERVICE_STAFF".equals(roleCode);
        }

        return false;
    }
}