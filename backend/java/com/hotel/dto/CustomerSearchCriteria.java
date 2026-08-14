package com.hotel.dto;
public record CustomerSearchCriteria(String keyword,String status,int page,int pageSize){ public CustomerSearchCriteria{keyword=normalize(keyword);status=normalize(status);page=Math.max(1,page);pageSize=pageSize<=0?20:Math.min(pageSize,100);} private static String normalize(String v){return v==null?null:v.trim().replaceAll("\\s+"," ");} }
