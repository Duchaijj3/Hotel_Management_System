package com.hotel.dto;
import java.time.LocalDate; import java.time.LocalDateTime;
public record CustomerFormDto(Long id,String fullName,String email,String phone,LocalDate dateOfBirth,String idDocumentType,String idDocumentNumber,String nationality,String address,LocalDateTime version){}
