package com.hotel.dto;
import java.util.List;
public record PageResult<T>(List<T> items,int page,int pageSize,long totalItems){ public List<T> getItems(){return items;} public int getPage(){return page;} public int getPageSize(){return pageSize;} public long getTotalItems(){return totalItems;} public int totalPages(){return Math.max(1,(int)Math.ceil((double)totalItems/pageSize));} }
