<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %><%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html><html lang="vi"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>LuxeStay HMS — Đăng nhập</title><link rel="stylesheet" href="${pageContext.request.contextPath}/css/receptionist.css?v=20260819"></head>
<body class="login-page">
<header class="login-topbar"><span class="login-brand">LuxeStay HMS</span><div class="login-icons"><span class="material-symbols-outlined">search</span><span class="material-symbols-outlined">notifications</span><span class="material-symbols-outlined">help_outline</span><span class="material-symbols-outlined">account_circle</span></div></header>
<div class="login-wrap"><main class="login-card">
    <section class="login-hero"><span class="hotel-mark">domain</span><h1>Welcome Back</h1><p>Đăng nhập để truy cập cổng quản lý</p></section>
    <section class="login-body">
        <div class="login-tabs"><span class="active">Đăng nhập</span><span>Đăng ký</span></div>
        <c:if test="${not empty error}"><p class="alert error" data-message-code="${errorCode}"><c:out value="${error}"/></p></c:if>
        <form method="post" class="stack-form">
            <label>Email<div class="input-icon"><span class="material-symbols-outlined">person</span><input type="email" name="email" value="<c:out value='${email}'/>" autocomplete="username" required placeholder="Nhập Email"></div></label>
            <label>Mật khẩu<div class="input-icon"><span class="material-symbols-outlined">lock</span><input type="password" name="password" autocomplete="current-password" required placeholder="Nhập mật khẩu"></div></label>
            <div class="login-options"><label><input type="checkbox" name="remember"> Ghi nhớ tôi</label><a href="#">Quên mật khẩu?</a></div>
            <button class="primary wide" type="submit">Đăng nhập <span class="material-symbols-outlined">arrow_forward</span></button>
        </form>
        <p class="login-help">LuxeStay Operational Systems • Team 6</p>
    </section>
</main></div></body></html>
