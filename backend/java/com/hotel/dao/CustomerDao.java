package com.hotel.dao;
import com.hotel.dto.*; import java.time.LocalDateTime; import java.util.*;
public interface CustomerDao { PageResult<CustomerSummaryDto> search(CustomerSearchCriteria c); Optional<CustomerDetailDto> findDetail(long id); boolean documentExists(String type,String number,Long excludeId); long create(CustomerFormDto form,long creator,String code); boolean update(CustomerFormDto form); }
