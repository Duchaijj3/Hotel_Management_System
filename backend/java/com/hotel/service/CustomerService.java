package com.hotel.service;
import com.hotel.dto.*; import com.hotel.exception.ValidationException; import java.util.Optional;
public interface CustomerService { PageResult<CustomerSummaryDto> search(CustomerSearchCriteria c); Optional<CustomerDetailDto> detail(long id); long create(CustomerFormDto f,long creator)throws ValidationException; void update(CustomerFormDto f)throws ValidationException; }
