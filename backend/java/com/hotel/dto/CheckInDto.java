package com.hotel.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CheckInDto(long reservationId, String bookingCode, String statusCode,
                         long customerId, String bookingHolderName, boolean bookingHolderActive,
                         LocalDate checkInDate, LocalDate checkOutDate,
                         boolean primaryGuestPresent, boolean primaryGuestHasDocument,
                         BigDecimal depositRequired, BigDecimal depositPaid,
                         List<StayingGuestDto> stayingGuests, List<RoomSelectionDto> roomSelections,
                         int existingAssignmentCount) {
    public long getReservationId(){return reservationId;} public String getBookingCode(){return bookingCode;}
    public String getStatusCode(){return statusCode;} public long getCustomerId(){return customerId;}
    public String getBookingHolderName(){return bookingHolderName;} public boolean isBookingHolderActive(){return bookingHolderActive;}
    public LocalDate getCheckInDate(){return checkInDate;} public LocalDate getCheckOutDate(){return checkOutDate;}
    public boolean isPrimaryGuestPresent(){return primaryGuestPresent;} public boolean isPrimaryGuestHasDocument(){return primaryGuestHasDocument;}
    public BigDecimal getDepositRequired(){return depositRequired;} public BigDecimal getDepositPaid(){return depositPaid;}
    public BigDecimal getRemainingDeposit(){return depositRequired.subtract(depositPaid).max(BigDecimal.ZERO);}
    public List<StayingGuestDto> getStayingGuests(){return stayingGuests;} public List<RoomSelectionDto> getRoomSelections(){return roomSelections;}
    public int getExistingAssignmentCount(){return existingAssignmentCount;}
}
