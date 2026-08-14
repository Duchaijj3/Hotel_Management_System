package com.hotel.dto;
import java.util.List;
public record PageResult<T>(List<T> items,int page,int pageSize,long totalItems){ public int totalPages(){return Math.max(1,(int)Math.ceil((double)totalItems/pageSize));} }
