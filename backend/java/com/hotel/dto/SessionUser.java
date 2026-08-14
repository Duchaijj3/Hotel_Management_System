package com.hotel.dto;
import java.io.Serializable;
public record SessionUser(long userId,String email,String fullName,String roleCode) implements Serializable {}
