package com.hotel.service;

import java.time.LocalDate;
import java.util.Set;

public final class RoomAvailabilityPolicy {
    private static final Set<String> NON_BLOCKING_STATUSES =
            Set.of("CANCELLED", "NO_SHOW", "CHECKED_OUT");

    private RoomAvailabilityPolicy() {
    }

    public static boolean isRoomCandidate(boolean roomActive, boolean roomTypeActive,
                                          String operationalStatus, String cleaningStatus,
                                          long actualRoomTypeId, long requestedRoomTypeId) {
        return roomActive
                && roomTypeActive
                && "AVAILABLE".equals(operationalStatus)
                && "INSPECTED".equals(cleaningStatus)
                && actualRoomTypeId == requestedRoomTypeId;
    }

    public static boolean assignmentBlocks(boolean currentAssignment, String reservationStatus,
                                           LocalDate existingCheckIn, LocalDate existingCheckOut,
                                           LocalDate targetCheckIn, LocalDate targetCheckOut) {
        return currentAssignment
                && !NON_BLOCKING_STATUSES.contains(reservationStatus)
                && existingCheckIn.isBefore(targetCheckOut)
                && existingCheckOut.isAfter(targetCheckIn);
    }
}
