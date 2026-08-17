<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Nhật ký gửi email</title>
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
                    <p class="eyebrow">F26 · Lịch sử gửi Email & Log</p>
                    <h1>Nhật ký gửi email</h1>
                    <p>Theo dõi trạng thái và kết quả của các thông báo gửi qua email từ hệ thống.</p>
                </div>
            </div>

            <c:if test="${not empty flash}">
                <p class="alert info"><c:out value="${flash}"/></p>
            </c:if>

            <section class="panel search-panel">
                <form class="filters manager-filters" method="get">
                    <label>Trạng thái gửi
                        <select name="statusCode">
                            <option value="">Tất cả</option>
                            <option value="SUCCESS" ${criteria.statusCode=='SUCCESS'?'selected':''}>Thành công (SUCCESS)</option>
                            <option value="FAILED" ${criteria.statusCode=='FAILED'?'selected':''}>Thất bại (FAILED)</option>
                            <option value="PENDING" ${criteria.statusCode=='PENDING'?'selected':''}>Đang chờ (PENDING)</option>
                        </select>
                    </label>
                    <label>Sự kiện (Event)
                        <input name="eventCode" value="<c:out value='${criteria.eventCode}'/>" placeholder="Mã sự kiện (ví dụ: F06)">
                    </label>
                    <button class="primary" type="submit">Lọc</button>
                </form>
            </section>

            <section class="panel table-panel">
                <div class="panel-title">
                    <h2>Nhật ký email</h2>
                    <span class="count-badge">${result.totalItems} bản ghi</span>
                </div>
                <c:choose>
                    <c:when test="${empty result.items}">
                        <div class="empty-state">
                            <h3>Không có nhật ký email nào</h3>
                            <p>Thay đổi bộ lọc hoặc kiểm tra lại các thiết lập trigger.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-wrap">
                            <table>
                                <thead>
                                <tr>
                                    <th>Thời gian tạo</th>
                                    <th>Người nhận</th>
                                    <th>Tiêu đề</th>
                                    <th>Sự kiện</th>
                                    <th>Trạng thái</th>
                                    <th>Thử lại (Retry)</th>
                                    <th>Thao tác</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach items="${result.items}" var="item">
                                    <tr>
                                        <td><c:out value="${item.createdAt}"/></td>
                                        <td><strong><c:out value="${item.recipientEmail}"/></strong></td>
                                        <td><c:out value="${item.subject}"/></td>
                                        <td><span class="badge"><c:out value="${item.eventCode}"/></span></td>
                                        <td>
                                            <span class="status ${item.statusCode=='SUCCESS'?'success':'danger'}">
                                                <c:out value="${item.statusCode}"/>
                                            </span>
                                            <c:if test="${not empty item.errorMessage}">
                                                <div style="font-size: 0.8em; color: var(--text-danger); max-width: 250px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;" title="<c:out value='${item.errorMessage}'/>">
                                                    Lỗi: <c:out value="${item.errorMessage}"/>
                                                </div>
                                            </c:if>
                                        </td>
                                        <td>${item.retryCount} lần</td>
                                        <td>
                                            <c:if test="${item.statusCode == 'FAILED'}">
                                                <form style="display:inline;" method="post" action="${pageContext.request.contextPath}/admin/email-deliveries/retry">
                                                    <input type="hidden" name="id" value="${item.id}">
                                                    <button class="primary" style="padding: 0.25rem 0.5rem; font-size: 0.85em;" type="submit">Gửi lại</button>
                                                </form>
                                            </c:if>
                                            <c:if test="${item.statusCode != 'FAILED'}">
                                                -
                                            </c:if>
                                        </td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                        <nav class="pagination">
                            <c:if test="${result.page > 1}">
                                <a href="?page=${result.page-1}&statusCode=${criteria.statusCode}&eventCode=<c:out value='${criteria.eventCode}'/>">‹ Trước</a>
                            </c:if>
                            <span>Trang ${result.page} / ${result.totalPages()}</span>
                            <c:if test="${result.page < result.totalPages()}">
                                <a href="?page=${result.page+1}&statusCode=${criteria.statusCode}&eventCode=<c:out value='${criteria.eventCode}'/>">Sau ›</a>
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
