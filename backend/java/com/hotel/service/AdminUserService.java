package com.hotel.service;

import com.hotel.dto.PageResult;
import com.hotel.dto.ProfileFormDto;
import com.hotel.dto.UserDetailDto;
import com.hotel.dto.UserFormDto;
import com.hotel.dto.UserSearchCriteria;
import com.hotel.exception.ValidationException;

import java.util.Optional;

public interface AdminUserService {
    PageResult<com.hotel.dto.UserSummaryDto> search(UserSearchCriteria criteria);

    Optional<UserDetailDto> detail(long id);

    Optional<UserDetailDto> profile(long userId);

    long create(UserFormDto form, long actorId) throws ValidationException;

    void update(UserFormDto form, long actorId) throws ValidationException;

    void updateProfile(long userId, ProfileFormDto form) throws ValidationException;

    void lock(long userId, long actorId) throws ValidationException;

    void unlock(long userId, long actorId) throws ValidationException;

    String resetPassword(long userId, long actorId, boolean sendEmail) throws ValidationException;

    void clearLockout(long userId, long actorId) throws ValidationException;
}
