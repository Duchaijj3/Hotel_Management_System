package com.hotel.dto;
import java.time.LocalDate;
public record ReservationSearchDto(long reservationId,String bookingCode,String customerName,String phone,String email,LocalDate checkInDate,LocalDate checkOutDate,String statusCode){
 public long getReservationId(){return reservationId;}public String getBookingCode(){return bookingCode;}public String getCustomerName(){return customerName;}public String getPhone(){return phone;}public String getEmail(){return email;}public LocalDate getCheckInDate(){return checkInDate;}public LocalDate getCheckOutDate(){return checkOutDate;}public String getStatusCode(){return statusCode;}
}
