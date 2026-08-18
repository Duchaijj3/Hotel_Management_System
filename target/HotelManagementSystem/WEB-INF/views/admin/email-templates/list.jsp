<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Quản lý mẫu email</title>
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
                    <p class="eyebrow">F26 · Quản lý Email & Mẫu</p>
                    <h1>Mẫu email thông báo</h1>
                    <p>Chuẩn hóa nội dung các email tự động gửi cho khách hàng và nhân viên.</p>
                </div>
                <a class="primary" href="${pageContext.request.contextPath}/admin/email-templates/create">Tạo mẫu mới</a>
            </div>

            <c:if test="${not empty sessionScope.flash}">
                <p class="alert success"><c:out value="${sessionScope.flash}"/></p>
                <c:remove var="flash" scope="session"/>
            </c:if>

            <section class="panel search-panel">
                <form class="filters manager-filters" method="get">
                    <label class="grow">Tìm kiếm
                        <input name="keyword" value="<c:out value='${criteria.keyword}'/>" placeholder="Mã mẫu hoặc tiêu đề mẫu">
                    </label>
                    <label>Sự kiện (Event)
                        <input name="eventCode" value="<c:out value='${criteria.eventCode}'/>" placeholder="Mã sự kiện (ví dụ: F06)">
                    </label>
                    <label>Kích hoạt
                        <select name="active">
                            <option value="">Tất cả</option>
                            <option value="true" ${criteria.active==true?'selected':''}>Active</option>
                            <option value="false" ${criteria.active==false?'selected':''}>Inactive</option>
                        </select>
                    </label>
                    <button class="primary" type="submit">Lọc</button>
                </form>
            </section>

            <section class="panel table-panel">
                <div class="panel-title">
                    <h2>Danh sách mẫu email</h2>
                    <span class="count-badge">${result.totalItems} mẫu</span>
                </div>
                <c:choose>
                    <c:when test="${empty result.items}">
                        <div class="empty-state">
                            <h3>Không tìm thấy mẫu email phù hợp</h3>
                            <p>Thay đổi bộ lọc hoặc tạo mẫu email mới.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-wrap">
                            <table>
                                <thead>
                                <tr>
                                    <th>Mã mẫu</th>
                                    <th>Tên mẫu</th>
                                    <th>Sự kiện Trigger</th>
                                    <th>Kích hoạt</th>
                                    <th>Thao tác</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach items="${result.items}" var="item">
                                    <tr>
                                        <td><strong><c:out value="${item.templateCode}"/></strong></td>
                                        <td><c:out value="${item.templateName}"/></td>
                                        <td><span class="badge"><c:out value="${item.eventCode}"/></span></td>
                                        <td>
                                            <span class="status ${item.active?'success':'muted'}">
                                                ${item.active?'ACTIVE':'INACTIVE'}
                                            </span>
                                        </td>
                                        <td>
                                            <a class="table-link" href="${pageContext.request.contextPath}/admin/email-templates/view?id=${item.id}">Chi tiết</a>
                                            <a class="table-link" href="${pageContext.request.contextPath}/admin/email-templates/edit?id=${item.id}">Sửa</a>
                                        </td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                        <nav class="pagination">
                            <c:if test="${result.page > 1}">
                                <a href="?page=${result.page-1}&keyword=<c:out value='${criteria.keyword}'/>&eventCode=<c:out value='${criteria.eventCode}'/>&active=${criteria.active}">‹ Trước</a>
                            </c:if>
                            <span>Trang ${result.page} / ${result.totalPages()}</span>
                            <c:if test="${result.page < result.totalPages()}">
                                <a href="?page=${result.page+1}&keyword=<c:out value='${criteria.keyword}'/>&eventCode=<c:out value='${criteria.eventCode}'/>&active=${criteria.active}">Sau ›</a>
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
