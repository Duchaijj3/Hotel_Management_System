package com.hotel.dto;

import java.time.LocalDate;

public record StayingGuestDto(long reservationGuestId, String fullName, LocalDate dateOfBirth,
                              String documentType, String documentNumber, String nationality,
                              boolean primary) {
    public long getReservationGuestId(){return reservationGuestId;} public String getFullName(){return fullName;}
    public LocalDate getDateOfBirth(){return dateOfBirth;} public String getDocumentType(){return documentType;}
    public String getDocumentNumber(){return documentNumber;} public String getNationality(){return nationality;}
    public boolean isPrimary(){return primary;}
}