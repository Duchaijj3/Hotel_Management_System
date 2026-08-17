package com.hotel.service.impl;

import com.hotel.dao.AdminUserDao;
import com.hotel.dao.EmailDeliveryDao;
import com.hotel.dao.EmailTemplateDao;
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
import com.hotel.exception.ValidationException;
import com.hotel.service.AdminEmailService;
import com.hotel.service.AdminUserService;
import org.mindrot.jbcrypt.BCrypt;

import java.security.SecureRandom;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class AdminUserServiceImpl implements AdminUserService {
    private static final Set<String> ROLES = Set.of(
            "CUSTOMER", "RECEPTIONIST", "SERVICE_STAFF", "MANAGER", "ADMIN");
    private static final Set<String> DEPARTMENTS = Set.of(
            "GENERAL_SERVICE", "HOUSEKEEPING", "MAINTENANCE");
    private static final Set<String> STATUSES = Set.of("ACTIVE", "LOCKED", "INACTIVE");
    private static final String TEMP_PASSWORD_CHARS =
            "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";

    private final AdminUserDao users;
    private final AdminEmailService emails;
    private final SecureRandom random = new SecureRandom();

    public AdminUserServiceImpl(AdminUserDao users, AdminEmailService emails) {
        this.users = users;
        this.emails = emails;
    }

    @Override
    public PageResult<UserSummaryDto> search(UserSearchCriteria criteria) {
        return users.search(criteria);
    }

    @Override
    public Optional<UserDetailDto> detail(long id) {
        return id > 0 ? users.findById(id) : Optional.empty();
    }

    @Override
    public Optional<UserDetailDto> profile(long userId) {
        return userId > 0 ? users.findProfile(userId) : Optional.empty();
    }

    @Override
    public long create(UserFormDto form, long actorId) throws ValidationException {
        validateUserForm(form, false);
        String normalizedEmail = normalizeEmail(form.email());
        if (users.emailExists(normalizedEmail, null)) {
            throw new ValidationException(Map.of("email", "Email is already registered."));
        }
        String password = form.password();
        if (password == null || password.isBlank()) {
            password = generateTemporaryPassword();
        }
        long userId = users.create(copyWithEmail(form, normalizedEmail),
                BCrypt.hashpw(password, BCrypt.gensalt(10)), actorId);
        if (form.sendActivationEmail()) {
            emails.sendTemplatedEmail("ACCOUNT_ACTIVATION", normalizedEmail, Map.of(
                    "fullName", form.fullName(),
                    "temporaryPassword", password));
        }
        return userId;
    }

    @Override
    public void update(UserFormDto form, long actorId) throws ValidationException {
        validateUserForm(form, true);
        if (form.id() == null || form.id() <= 0 || !users.update(form, actorId)) {
            throw new ValidationException(Map.of("general", "User not found."));
        }
        String password = form.password();
        if (password != null && !password.isBlank()) {
            users.updatePassword(form.id(), BCrypt.hashpw(password, BCrypt.gensalt(10)), password);
        }
    }

    @Override
    public void updateProfile(long userId, ProfileFormDto form) throws ValidationException {
        Map<String, String> errors = new LinkedHashMap<>();
        if (form.fullName() == null || form.fullName().isBlank()) {
            errors.put("fullName", "Full name is required.");
        }
        if (form.phone() != null && form.phone().length() > 20) {
            errors.put("phone", "Phone number is too long.");
        }
        boolean changingPassword = hasText(form.newPassword()) || hasText(form.currentPassword());
        if (changingPassword) {
            validatePasswordChange(userId, form, errors);
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
        if (!users.updateProfile(userId, form)) {
            throw new ValidationException(Map.of("general", "Profile not found."));
        }
        if (changingPassword) {
            users.updatePassword(userId, BCrypt.hashpw(form.newPassword(), BCrypt.gensalt(10)), form.newPassword());
        }
    }

    @Override
    public void lock(long userId, long actorId) throws ValidationException {
        ensureExists(userId);
        users.setStatus(userId, "LOCKED", actorId);
    }

    @Override
    public void unlock(long userId, long actorId) throws ValidationException {
        ensureExists(userId);
        users.setStatus(userId, "ACTIVE", actorId);
        users.resetLockout(userId, actorId);
    }

    @Override
    public String resetPassword(long userId, long actorId, boolean sendEmail)
            throws ValidationException {
        UserDetailDto user = ensureExists(userId);
        String temporaryPassword = generateTemporaryPassword();
        users.updatePassword(userId, BCrypt.hashpw(temporaryPassword, BCrypt.gensalt(10)), temporaryPassword);
        if (sendEmail) {
            emails.sendTemplatedEmail("PASSWORD_RESET", user.email(), Map.of(
                    "fullName", user.fullName(),
                    "temporaryPassword", temporaryPassword));
        }
        return temporaryPassword;
    }

    @Override
    public void clearLockout(long userId, long actorId) throws ValidationException {
        ensureExists(userId);
        users.resetLockout(userId, actorId);
    }

    private UserDetailDto ensureExists(long userId) throws ValidationException {
        return detail(userId).orElseThrow(() ->
                new ValidationException(Map.of("general", "User not found.")));
    }

    private void validateUserForm(UserFormDto form, boolean update) throws ValidationException {
        Map<String, String> errors = new LinkedHashMap<>();
        if (!update) {
            if (form.email() == null || !form.email().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
                errors.put("email", "Valid email is required.");
            }
        }
        if (form.fullName() == null || form.fullName().isBlank()) {
            errors.put("fullName", "Full name is required.");
        }
        if (form.roleCode() == null || !ROLES.contains(form.roleCode())) {
            errors.put("roleCode", "Select a valid role.");
        }
        if ("SERVICE_STAFF".equals(form.roleCode())
                && (form.departmentCode() == null || !DEPARTMENTS.contains(form.departmentCode()))) {
            errors.put("departmentCode", "Department is required for service staff.");
        }
        if (form.statusCode() == null || !STATUSES.contains(form.statusCode())) {
            errors.put("statusCode", "Select a valid status.");
        }
        if (update && (form.id() == null || form.id() <= 0)) {
            errors.put("general", "User id is missing.");
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    private void validatePasswordChange(long userId, ProfileFormDto form,
                                        Map<String, String> errors) {
        if (!hasText(form.currentPassword())) {
            errors.put("currentPassword", "Current password is required.");
        }
        if (!hasText(form.newPassword()) || form.newPassword().length() < 8) {
            errors.put("newPassword", "New password must be at least 8 characters.");
        }
        if (!hasText(form.confirmPassword())
                || !form.newPassword().equals(form.confirmPassword())) {
            errors.put("confirmPassword", "Password confirmation does not match.");
        }
        if (errors.containsKey("currentPassword") || errors.containsKey("newPassword")) {
            return;
        }
        String hash = users.findPasswordHash(userId).orElse(null);
        if (hash == null || !BCrypt.checkpw(form.currentPassword(), hash)) {
            errors.put("currentPassword", "Current password is incorrect.");
        }
    }

    private static UserFormDto copyWithEmail(UserFormDto form, String email) {
        return new UserFormDto(form.id(), email, form.fullName(), form.phone(),
                form.roleCode(), form.departmentCode(), form.statusCode(),
                form.sendActivationEmail(), form.password());
    }

    private static String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String generateTemporaryPassword() {
        StringBuilder builder = new StringBuilder(12);
        for (int index = 0; index < 12; index++) {
            builder.append(TEMP_PASSWORD_CHARS.charAt(
                    random.nextInt(TEMP_PASSWORD_CHARS.length())));
        }
        return builder.toString();
    }
}
