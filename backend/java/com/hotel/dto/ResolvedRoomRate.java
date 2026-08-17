package com.hotel.dto;

import java.math.BigDecimal;

public record ResolvedRoomRate(BigDecimal nightlyPrice, boolean stopSell,
                               boolean dailyOverride) {
}
