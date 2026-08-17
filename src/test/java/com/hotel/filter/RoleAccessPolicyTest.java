package com.hotel.filter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoleAccessPolicyTest {
    @Test
    void managerRoutesAreRestrictedToManagers() {
        assertTrue(RoleAccessPolicy.canAccess("MANAGER", "/manager/dashboard"));
        assertFalse(RoleAccessPolicy.canAccess("RECEPTIONIST", "/manager/dashboard"));
        assertTrue(RoleAccessPolicy.canAccess("MANAGER", "/manager/rooms"));
        assertFalse(RoleAccessPolicy.canAccess("RECEPTIONIST", "/manager/rooms"));
        assertFalse(RoleAccessPolicy.canAccess(null, "/manager/rooms"));
    }
}
