<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Admin Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/receptionist.css?v=20260817">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/manager.css?v=20260817">
</head>
<body>
<a class="skip-link" href="#dashboard-content">Bỏ qua đến nội dung chính</a>
<div class="app-shell">
    <%@ include file="fragments/sidebar.jspf" %>
    <main class="main-panel" id="dashboard-content">
        <%@ include file="fragments/header.jspf" %>
        <div class="content dashboard-content">
            <section class="dashboard-hero" aria-labelledby="dashboard-welcome">
                <p class="dashboard-kicker">ADMINISTRATOR HOME</p>
                <c:choose>
                    <c:when test="${not empty sessionScope.sessionUser.fullName}">
                        <h1 id="dashboard-welcome">Chào mừng, <c:out value="${sessionScope.sessionUser.fullName}"/></h1>
                        <p>Chọn một tác vụ quản trị hệ thống để tiếp tục.</p>
                    </c:when>
                    <c:otherwise>
                        <h1 id="dashboard-welcome">Admin Dashboard</h1>
                        <p class="dashboard-user-error" role="alert">Không thể tải thông tin Administrator. Bạn vẫn có thể sử dụng các chức năng bên dưới.</p>
                    </c:otherwise>
                </c:choose>
            </section>

            <section class="dashboard-section" aria-labelledby="management-functions-title">
                <div class="dashboard-section-heading">
                    <div>
                        <h2 id="management-functions-title">Chức năng quản trị hệ thống</h2>
                        <p>Quản trị tài khoản nhân viên, thiết lập email và nhật ký hệ thống.</p>
                    </div>
                </div>
                <div class="dashboard-grid">
                    <c:forEach items="${dashboardCards}" var="card">
                        <c:choose>
                            <c:when test="${card.enabled}">
                                <a class="dashboard-card" data-card-id="${card.id}"
                                   href="${pageContext.request.contextPath}${card.targetPath}">
                                    <span class="dashboard-card-icon material-symbols-outlined" aria-hidden="true"><c:out value="${card.icon}"/></span>
                                    <span class="dashboard-card-copy">
                                        <strong><c:out value="${card.title}"/></strong>
                                        <span><c:out value="${card.description}"/></span>
                                    </span>
                                    <span class="dashboard-use-cases">
                                        <c:forEach items="${card.useCases}" var="useCase">
                                            <small><c:out value="${useCase}"/></small>
                                        </c:forEach>
                                    </span>
                                    <span class="dashboard-card-action">Mở chức năng <span class="material-symbols-outlined" aria-hidden="true">arrow_forward</span></span>
                                </a>
                            </c:when>
                            <c:otherwise>
                                <article class="dashboard-card disabled" data-card-id="${card.id}" aria-disabled="true">
                                    <span class="dashboard-card-icon material-symbols-outlined" aria-hidden="true"><c:out value="${card.icon}"/></span>
                                    <span class="dashboard-card-copy">
                                        <strong><c:out value="${card.title}"/></strong>
                                        <span><c:out value="${card.description}"/></span>
                                    </span>
                                    <span class="dashboard-card-action">Chưa kích hoạt</span>
                                </article>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </div>
            </section>
        </div>
    </main>
</div>
</body>
</html>
