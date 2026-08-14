package com.hotel.dto;
import java.io.Serializable;
public record SessionUser(long userId,String email,String fullName,String roleCode) implements Serializable {
    public long getUserId(){return userId;}
    public String getEmail(){return email;}
    public String getFullName(){return fullName;}
    public String getRoleCode(){return roleCode;}
}
