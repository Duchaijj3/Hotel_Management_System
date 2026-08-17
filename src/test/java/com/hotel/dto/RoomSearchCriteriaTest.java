package com.hotel.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;

class RoomSearchCriteriaTest {
    @Test
    void missingOperationalStatusMeansNoStatusFilter() {
        RoomSearchCriteria criteria = assertDoesNotThrow(() ->
                new RoomSearchCriteria(null, null, null, null, null, 1, 20));

        assertNull(criteria.operationalStatus());
    }
}
