<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Quản lý tài khoản nhân viên</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/receptionist.css?v=20260817">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/manager.css?v=20260817">
</head>
<body>
<div class="app-shell">
    <%@ include file="../fragments/sidebar.jspf" %>
    <main class="main-panel">
        <%@ include file="../fragments/header.jspf" %>
        <div class="content">
            <div class="page-heading">
                <div>
                    <p class="eyebrow">F25 · Quản lý tài khoản & Vai trò</p>
                    <h1>Tài khoản nhân viên</h1>
                    <p>Tạo tài khoản, phân chia vai trò và kiểm soát quyền truy cập hệ thống.</p>
                </div>
                <a class="primary" href="${pageContext.request.contextPath}/admin/users/create">Tạo tài khoản</a>
            </div>

            <c:if test="${not empty sessionScope.flash}">
                <p class="alert success"><c:out value="${sessionScope.flash}"/></p>
                <c:remove var="flash" scope="session"/>
            </c:if>

            <section class="panel search-panel">
                <form class="filters manager-filters" method="get">
                    <label class="grow">Tìm kiếm
                        <input name="keyword" value="<c:out value='${criteria.keyword}'/>" placeholder="Email, họ tên hoặc số điện thoại">
                    </label>
                    <label>Vai trò
                        <select name="roleCode">
                            <option value="">Tất cả</option>
                            <option value="ADMIN" ${criteria.roleCode=='ADMIN'?'selected':''}>ADMIN</option>
                            <option value="MANAGER" ${criteria.roleCode=='MANAGER'?'selected':''}>MANAGER</option>
                            <option value="RECEPTIONIST" ${criteria.roleCode=='RECEPTIONIST'?'selected':''}>RECEPTIONIST</option>
                            <option value="SERVICE_STAFF" ${criteria.roleCode=='SERVICE_STAFF'?'selected':''}>SERVICE_STAFF</option>
                        </select>
                    </label>
                    <label>Trạng thái
                        <select name="statusCode">
                            <option value="">Tất cả</option>
                            <option value="ACTIVE" ${criteria.statusCode=='ACTIVE'?'selected':''}>ACTIVE</option>
                            <option value="LOCKED" ${criteria.statusCode=='LOCKED'?'selected':''}>LOCKED</option>
                            <option value="INACTIVE" ${criteria.statusCode=='INACTIVE'?'selected':''}>INACTIVE</option>
                        </select>
                    </label>
                    <button class="primary" type="submit">Lọc</button>
                </form>
            </section>

            <section class="panel table-panel">
                <div class="panel-title">
                    <h2>Danh sách nhân viên</h2>
                    <span class="count-badge">${result.totalItems} tài khoản</span>
                </div>
                <c:choose>
                    <c:when test="${empty result.items}">
                        <div class="empty-state">
                            <h3>Không tìm thấy tài khoản phù hợp</h3>
                            <p>Thay đổi bộ lọc hoặc tạo tài khoản nhân viên mới.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-wrap">
                            <table>
                                <thead>
                                <tr>
                                    <th>Họ tên</th>
                                    <th>Email</th>
                                    <th>Số điện thoại</th>
                                    <th>Vai trò</th>
                                    <th>Bộ phận</th>
                                    <th>Trạng thái</th>
                                    <th>Thao tác</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach items="${result.items}" var="item">
                                    <tr>
                                        <td><strong><c:out value="${item.fullName}"/></strong></td>
                                        <td><c:out value="${item.email}"/></td>
                                        <td><c:out value="${item.phone != null ? item.phone : '-'}"/></td>
                                        <td><span class="badge"><c:out value="${item.roleCode}"/></span></td>
                                        <td><c:out value="${item.departmentCode != null ? item.departmentCode : '-'}"/></td>
                                        <td>
                                            <span class="status ${item.statusCode=='ACTIVE'?'success':(item.statusCode=='LOCKED'?'danger':'muted')}">
                                                <c:out value="${item.statusCode}"/>
                                            </span>
                                        </td>
                                        <td>
                                            <a class="table-link" href="${pageContext.request.contextPath}/admin/users/view?id=${item.id}">Chi tiết</a>
                                            <a class="table-link" href="${pageContext.request.contextPath}/admin/users/edit?id=${item.id}">Sửa</a>
                                        </td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                        <nav class="pagination">
                            <c:if test="${result.page > 1}">
                                <a href="?page=${result.page-1}&keyword=<c:out value='${criteria.keyword}'/>&roleCode=${criteria.roleCode}&statusCode=${criteria.statusCode}">‹ Trước</a>
                            </c:if>
                            <span>Trang ${result.page} / ${result.totalPages()}</span>
                            <c:if test="${result.page < result.totalPages()}">
                                <a href="?page=${result.page+1}&keyword=<c:out value='${criteria.keyword}'/>&roleCode=${criteria.roleCode}&statusCode=${criteria.statusCode}">Sau ›</a>
                            </c:if>
                        </nav>
                    </c:otherwise>
                </c:choose>
            </section>
        </div>
    </main>
</div>
</body>
</html>
