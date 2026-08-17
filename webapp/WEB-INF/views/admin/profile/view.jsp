<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Thông tin cá nhân Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/receptionist.css?v=20260817">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/manager.css?v=20260817">
    <style>
        .profile-container {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 32px;
            margin-top: 24px;
        }
        .profile-card-premium {
            background: #ffffff;
            border: 1px solid var(--line);
            border-radius: 16px;
            padding: 32px;
            box-shadow: 0 4px 20px rgba(0, 35, 111, 0.05);
            transition: all 0.3s ease;
        }
        .profile-card-premium:hover {
            transform: translateY(-4px);
            box-shadow: 0 10px 30px rgba(0, 35, 111, 0.08);
        }
        .profile-header-section {
            display: flex;
            align-items: center;
            gap: 20px;
            margin-bottom: 28px;
            padding-bottom: 20px;
            border-bottom: 1px solid #eeedf4;
        }
        .profile-avatar-large {
            width: 64px;
            height: 64px;
            border-radius: 50%;
            background: linear-gradient(135deg, var(--primary) 0%, var(--primary-2) 100%);
            color: #ffffff;
            display: grid;
            place-items: center;
            font-size: 24px;
            font-weight: 700;
            box-shadow: 0 4px 10px rgba(0, 35, 111, 0.2);
        }
        .profile-title-group h2 {
            margin: 0;
            font-size: 20px;
            color: var(--primary);
            font-weight: 700;
        }
        .profile-title-group p {
            margin: 4px 0 0;
            font-size: 13px;
            color: var(--muted);
        }
        .form-group-custom {
            margin-bottom: 20px;
            display: grid;
            gap: 8px;
        }
        .form-group-custom label {
            font-size: 13px;
            color: #39485a;
            font-weight: 600;
            display: block;
        }
        .input-icon-custom {
            position: relative;
            width: 100%;
        }
        .input-icon-custom span {
            position: absolute;
            left: 14px;
            top: 12px;
            color: #757682;
            font-size: 20px;
            transition: color 0.2s ease;
        }
        .input-icon-custom input {
            padding-left: 46px;
            border: 1px solid #c5c5d3;
            border-radius: 10px;
            height: 46px;
            background: #ffffff;
            font-size: 14px;
            transition: all 0.2s ease;
            width: 100%;
            box-sizing: border-box;
        }
        .input-icon-custom input:focus {
            border-color: var(--primary);
            box-shadow: 0 0 0 3px rgba(0, 35, 111, 0.1);
        }
        .input-icon-custom input:focus + span {
            color: var(--primary);
        }
        .input-icon-custom input[disabled] {
            background: #f4f3fa;
            color: #757682;
            cursor: not-allowed;
        }
        .btn-submit-premium {
            background: linear-gradient(135deg, var(--primary) 0%, var(--primary-2) 100%);
            border: none;
            color: #ffffff;
            font-weight: 600;
            font-size: 14px;
            height: 48px;
            border-radius: 10px;
            cursor: pointer;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 8px;
            box-shadow: 0 4px 12px rgba(0, 35, 111, 0.15);
            transition: all 0.2s ease;
            width: 100%;
            margin-top: 12px;
            box-sizing: border-box;
        }
        .btn-submit-premium:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(0, 35, 111, 0.25);
            filter: brightness(1.1);
        }
        .btn-submit-premium:active {
            transform: translateY(0);
        }
        @media(max-width: 900px) {
            .profile-container {
                grid-template-columns: 1fr;
                gap: 24px;
            }
        }
    </style>
</head>
<body>
<div class="app-shell">
    <%@ include file="../fragments/sidebar.jspf" %>
    <main class="main-panel">
        <%@ include file="../fragments/header.jspf" %>
        <div class="content narrow-content">
            <div class="page-heading">
                <div>
                    <p class="eyebrow">F05 · Thông tin cá nhân</p>
                    <h1>Hồ sơ của bạn</h1>
                    <p>Quản lý thông tin liên hệ và đổi mật khẩu bảo mật tài khoản Admin.</p>
                </div>
            </div>

            <c:if test="${not empty flash}">
                <p class="alert success"><c:out value="${flash}"/></p>
            </c:if>

            <c:if test="${not empty errors.general}">
                <p class="alert error"><c:out value="${errors.general}"/></p>
            </c:if>

            <div class="profile-container">
                <!-- Update profile info form -->
                <div class="profile-card-premium">
                    <div class="profile-header-section">
                        <div class="profile-avatar-large">AD</div>
                        <div class="profile-title-group">
                            <h2>Thông tin liên hệ</h2>
                            <p>Cập nhật họ tên và số điện thoại của bạn</p>
                        </div>
                    </div>
                    
                    <form method="post" action="${pageContext.request.contextPath}/admin/profile">
                        <div class="form-group-custom">
                            <label>Địa chỉ Email</label>
                            <div class="input-icon-custom">
                                <input type="email" value="<c:out value='${user.email}'/>" readonly disabled>
                                <span class="material-symbols-outlined">alternate_email</span>
                            </div>
                        </div>
                        
                        <div class="form-group-custom">
                            <label>Họ tên *</label>
                            <div class="input-icon-custom">
                                <input name="fullName" value="<c:out value='${user.fullName}'/>" required placeholder="Nhập họ và tên">
                                <span class="material-symbols-outlined">person</span>
                            </div>
                            <span class="field-error"><c:out value="${errors.fullName}"/></span>
                        </div>

                        <div class="form-group-custom">
                            <label>Số điện thoại</label>
                            <div class="input-icon-custom">
                                <input name="phone" value="<c:out value='${user.phone}'/>" placeholder="Nhập số điện thoại">
                                <span class="material-symbols-outlined">phone</span>
                            </div>
                            <span class="field-error"><c:out value="${errors.phone}"/></span>
                        </div>

                        <button class="btn-submit-premium" type="submit">
                            <span class="material-symbols-outlined" style="font-size:18px;">save</span> Cập nhật hồ sơ
                        </button>
                    </form>
                </div>

                <!-- Update password form -->
                <div class="profile-card-premium">
                    <div class="profile-header-section">
                        <div class="profile-avatar-large" style="background: linear-gradient(135deg, var(--gold) 0%, #a4811c 100%);">key</div>
                        <div class="profile-title-group">
                            <h2>Đổi mật khẩu</h2>
                            <p>Thay đổi mật khẩu đăng nhập tài khoản</p>
                        </div>
                    </div>
                    
                    <form method="post" action="${pageContext.request.contextPath}/admin/profile">
                        <!-- We pass existing contact values to satisfy required validation in backend -->
                        <input type="hidden" name="fullName" value="<c:out value='${user.fullName}'/>">
                        <input type="hidden" name="phone" value="<c:out value='${user.phone}'/>">

                        <div class="form-group-custom">
                            <label>Mật khẩu hiện tại *</label>
                            <div class="input-icon-custom">
                                <input type="password" name="currentPassword" required autocomplete="current-password" placeholder="Nhập mật khẩu hiện tại">
                                <span class="material-symbols-outlined">lock</span>
                            </div>
                            <span class="field-error"><c:out value="${errors.currentPassword}"/></span>
                        </div>

                        <div class="form-group-custom">
                            <label>Mật khẩu mới *</label>
                            <div class="input-icon-custom">
                                <input type="password" name="newPassword" required autocomplete="new-password" placeholder="Nhập mật khẩu mới">
                                <span class="material-symbols-outlined">key</span>
                            </div>
                            <span class="field-error"><c:out value="${errors.newPassword}"/></span>
                        </div>

                        <div class="form-group-custom">
                            <label>Xác nhận mật khẩu mới *</label>
                            <div class="input-icon-custom">
                                <input type="password" name="confirmPassword" required autocomplete="new-password" placeholder="Xác nhận mật khẩu mới">
                                <span class="material-symbols-outlined">lock_reset</span>
                            </div>
                            <span class="field-error"><c:out value="${errors.confirmPassword}"/></span>
                        </div>

                        <button class="btn-submit-premium" type="submit" style="background: linear-gradient(135deg, var(--gold) 0%, #a4811c 100%); box-shadow: 0 4px 12px rgba(204, 167, 48, 0.15);">
                            <span class="material-symbols-outlined" style="font-size:18px;">vpn_key</span> Thay đổi mật khẩu
                        </button>
                    </form>
                </div>
            </div>
        </div>
    </main>
</div>
</body>
</html>
