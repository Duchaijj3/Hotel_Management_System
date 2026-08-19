<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!doctype html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Tài khoản nhân viên — <c:out value="${user.fullName}"/></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/receptionist.css?v=20260817">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/manager.css?v=20260817">
</head>
<body>
<div class="app-shell">
    <%@ include file="../fragments/sidebar.jspf" %>
    <main class="main-panel">
        <%@ include file="../fragments/header.jspf" %>
        <div class="content narrow-content">
            <a class="back-link" href="${pageContext.request.contextPath}/admin/users">← Danh sách nhân viên</a>
            <div class="page-heading">
                <div>
                    <h1><c:out value="${user.fullName}"/></h1>
                    <p>Vai trò: <span class="badge"><c:out value="${user.roleCode}"/></span> ${user.departmentCode != null ? ' · Bộ phận: ' : ''}<c:out value="${user.departmentCode}"/></p>
                </div>
                <a class="primary" href="${pageContext.request.contextPath}/admin/users/edit?id=${user.id}">Sửa thông tin</a>
            </div>

            <c:if test="${not empty flash}">
                <p class="alert success"><c:out value="${flash}"/></p>
            </c:if>

            <section class="detail-grid">
                <div class="panel">
                    <h2>Thông tin tài khoản</h2>
                    <dl class="info-list">
                        <dt>Mã nhân viên (ID)</dt>
                        <dd>${user.id}</dd>

                        <dt>Địa chỉ Email</dt>
                        <dd><c:out value="${user.email}"/></dd>

                        <dt>Số điện thoại</dt>
                        <dd><c:out value="${user.phone != null ? user.phone : 'Chưa cập nhật'}"/></dd>

                        <dt>Mật khẩu</dt>
                        <dd><c:out value="${user.plainPassword != null ? user.plainPassword : 'Chưa cập nhật'}"/></dd>

                        <dt>Trạng thái tài khoản</dt>
                        <dd>
                            <span class="status ${user.statusCode=='ACTIVE'?'success':(user.statusCode=='LOCKED'?'danger':'muted')}">
                                <c:out value="${user.statusCode}"/>
                            </span>
                        </dd>

                        <dt>Số lần đăng nhập sai liên tiếp</dt>
                        <dd>${user.failedLoginAttempts} / 5</dd>

                        <c:if test="${not empty user.lockedUntil}">
                            <dt>Tạm khóa đăng nhập đến</dt>
                            <dd>
                                <span class="status danger">
                                    <c:out value="${user.lockedUntil}"/>
                                </span>
                            </dd>
                        </c:if>

                        <dt>Đăng nhập gần nhất</dt>
                        <dd><c:out value="${user.lastLoginAt != null ? user.lastLoginAt : 'Chưa từng đăng nhập'}"/></dd>

                        <dt>Ngày tạo tài khoản</dt>
                        <dd><c:out value="${user.createdAt}"/></dd>

                        <dt>Cập nhật lần cuối</dt>
                        <dd><c:out value="${user.updatedAt}"/></dd>
                    </dl>
                </div>

                <aside class="panel action-panel">
                    <h2>Thao tác quản trị</h2>
                    <p>Kiểm soát quyền truy cập của nhân viên này vào hệ thống.</p>
                    
                    <div class="stack-form">
                        <!-- Lock/Unlock actions -->
                        <c:choose>
                            <c:when test="${user.statusCode == 'ACTIVE'}">
                                <form method="post" action="${pageContext.request.contextPath}/admin/users/lock">
                                    <input type="hidden" name="id" value="${user.id}">
                                    <button class="primary wide danger" type="submit">Khóa tài khoản (LOCK)</button>
                                </form>
                            </c:when>
                            <c:when test="${user.statusCode == 'LOCKED'}">
                                <form method="post" action="${pageContext.request.contextPath}/admin/users/unlock">
                                    <input type="hidden" name="id" value="${user.id}">
                                    <button class="primary wide success" type="submit">Mở khóa tài khoản (ACTIVE)</button>
                                </form>
                            </c:when>
                        </c:choose>

                        <!-- Clear temporary lockout -->
                        <c:if test="${user.failedLoginAttempts >= 5 || not empty user.lockedUntil}">
                            <form method="post" action="${pageContext.request.contextPath}/admin/users/clear-lockout">
                                <input type="hidden" name="id" value="${user.id}">
                                <button class="secondary-button wide" type="submit">Mở khóa đăng nhập tạm thời</button>
                            </form>
                        </c:if>

                        <hr>

                        <!-- Reset password action -->
                        <form method="post" action="${pageContext.request.contextPath}/admin/users/reset-password">
                            <input type="hidden" name="id" value="${user.id}">
                            <h3>Khôi phục mật khẩu</h3>
                            <p style="font-size: 0.9em; color: var(--text-muted); margin-bottom: 0.5rem;">
                                Mật khẩu của nhân viên sẽ được reset ngẫu nhiên.
                            </p>
                            <label class="checkbox-field" style="margin-bottom: 1rem;">
                                <input type="checkbox" name="sendEmail" checked>
                                Gửi mật khẩu mới qua email cho nhân viên
                            </label>
                            <button class="primary wide" type="submit">Reset Mật Khẩu</button>
                        </form>
                    </div>
                </aside>
            </section>
        </div>
    </main>
</div>
</body>
</html>
