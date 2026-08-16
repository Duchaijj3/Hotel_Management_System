package com.hotel.service;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoomAvailabilityPolicyTest {
    private static final LocalDate MAY_10 = LocalDate.of(2026, 5, 10);
    private static final LocalDate MAY_12 = LocalDate.of(2026, 5, 12);

    @Test void inactiveRoomIsExcluded() {
        assertFalse(candidate(false, true, "AVAILABLE", "CLEAN", 1, 1));
    }

    @Test void maintenanceRoomIsExcluded() {
        assertFalse(candidate(true, true, "MAINTENANCE", "CLEAN", 1, 1));
    }

    @Test void dirtyOrCleaningRoomIsExcluded() {
        assertFalse(candidate(true, true, "AVAILABLE", "DIRTY", 1, 1));
        assertFalse(candidate(true, true, "AVAILABLE", "CLEANING", 1, 1));
    }

    @Test void cleanButNotInspectedRoomIsExcluded() {
        assertFalse(candidate(true, true, "AVAILABLE", "CLEAN", 1, 1));
        assertTrue(candidate(true, true, "AVAILABLE", "INSPECTED", 1, 1));
    }

    @Test void wrongRoomTypeIsExcluded() {
        assertFalse(candidate(true, true, "AVAILABLE", "INSPECTED", 2, 1));
    }

    @Test void overlappingActiveAssignmentBlocksRoom() {
        assertTrue(blocks("CONFIRMED", LocalDate.of(2026, 5, 9), LocalDate.of(2026, 5, 11)));
    }

    @Test void cancelledReservationDoesNotBlockRoom() {
        assertFalse(blocks("CANCELLED", LocalDate.of(2026, 5, 9), LocalDate.of(2026, 5, 11)));
    }

    @Test void touchingCheckoutAndCheckinDoNotOverlap() {
        assertFalse(blocks("CONFIRMED", LocalDate.of(2026, 5, 8), MAY_10));
        assertFalse(blocks("CONFIRMED", MAY_12, LocalDate.of(2026, 5, 14)));
    }

    private static boolean candidate(boolean roomActive, boolean typeActive, String operational,
                                     String cleaning, long actualType, long requestedType) {
        return RoomAvailabilityPolicy.isRoomCandidate(roomActive, typeActive, operational, cleaning,
                actualType, requestedType);
    }

    private static boolean blocks(String status, LocalDate existingIn, LocalDate existingOut) {
        return RoomAvailabilityPolicy.assignmentBlocks(true, status, existingIn, existingOut,
                MAY_10, MAY_12);
    }
}
