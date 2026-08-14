<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html><html lang="vi"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Grand Horizon Resort — Đăng nhập</title><link rel="stylesheet" href="${pageContext.request.contextPath}/css/receptionist.css?v=20260814"></head>
<body class="login-page"><main class="login-card">
    <section class="login-hero"><span class="hotel-mark">H</span><h1>Grand Horizon Resort</h1><p>Cổng Đăng Nhập Hệ Thống Quản Lý Khách Sạn</p></section>
    <section class="login-body">
        <div class="login-note"><span>◆</span><div><strong>Cổng đăng nhập nhân viên</strong><small>Sử dụng tài khoản được quản trị viên cấp.</small></div></div>
        <c:if test="${not empty error}"><p class="alert error"><c:out value="${error}"/></p></c:if>
        <form method="post" class="stack-form">
            <label>Email đăng nhập<div class="input-icon"><span>●</span><input type="email" name="email" autocomplete="username" required placeholder="receptionist@hotel.local"></div></label>
            <label>Mật khẩu<div class="input-icon"><span>◆</span><input type="password" name="password" autocomplete="current-password" required placeholder="Nhập mật khẩu"></div></label>
            <button class="primary wide" type="submit">↪ ĐĂNG NHẬP HỆ THỐNG</button>
        </form>
        <p class="login-help">Hotel Management System • Team 6</p>
    </section>
</main></body></html>
