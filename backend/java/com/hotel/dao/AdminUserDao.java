package com.hotel.dao;

import com.hotel.dto.EmailDeliveryDto;
import com.hotel.dto.EmailDeliverySearchCriteria;
import com.hotel.dto.EmailTemplateDetailDto;
import com.hotel.dto.EmailTemplateForm;
import com.hotel.dto.EmailTemplateSearchCriteria;
import com.hotel.dto.EmailTemplateSummaryDto;
import com.hotel.dto.PageResult;
import com.hotel.dto.ProfileFormDto;
import com.hotel.dto.UserDetailDto;
import com.hotel.dto.UserFormDto;
import com.hotel.dto.UserSearchCriteria;
import com.hotel.dto.UserSummaryDto;

import java.util.Optional;

public interface AdminUserDao {
    PageResult<UserSummaryDto> search(UserSearchCriteria criteria);

    Optional<UserDetailDto> findById(long id);

    Optional<UserDetailDto> findProfile(long userId);

    boolean emailExists(String email, Long excludeId);

    long create(UserFormDto form, String passwordHash, long actorId);

    boolean update(UserFormDto form, long actorId);

    boolean updateProfile(long userId, ProfileFormDto form);

    boolean updatePassword(long userId, String passwordHash, String plainPassword);

    boolean setStatus(long userId, String statusCode, long actorId);

    boolean resetLockout(long userId, long actorId);

    Optional<String> findPasswordHash(long userId);
}
