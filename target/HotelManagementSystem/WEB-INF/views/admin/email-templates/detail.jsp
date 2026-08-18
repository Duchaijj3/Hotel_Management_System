<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Mẫu email: <c:out value="${item.templateCode}"/></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/receptionist.css?v=20260817">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/manager.css?v=20260817">
</head>
<body>
<div class="app-shell">
    <%@ include file="../fragments/sidebar.jspf" %>
    <main class="main-panel">
        <%@ include file="../fragments/header.jspf" %>
        <div class="content narrow-content">
            <a class="back-link" href="${pageContext.request.contextPath}/admin/email-templates">← Danh sách mẫu email</a>
            <div class="page-heading">
                <div>
                    <h1><c:out value="${item.templateCode}"/></h1>
                    <p><c:out value="${item.templateName}"/> · Sự kiện: <span class="badge"><c:out value="${item.eventCode}"/></span></p>
                </div>
                <a class="primary" href="${pageContext.request.contextPath}/admin/email-templates/edit?id=${item.id}">Sửa cấu hình</a>
            </div>

            <c:if test="${not empty flash}">
                <p class="alert success"><c:out value="${flash}"/></p>
            </c:if>

            <section class="detail-grid">
                <div class="panel span-2">
                    <h2>Nội dung cấu hình email</h2>
                    <dl class="info-list">
                        <dt>Tiêu đề (Subject)</dt>
                        <dd><strong><c:out value="${item.subjectTemplate}"/></strong></dd>

                        <dt>Nội dung HTML (Body HTML)</dt>
                        <dd>
                            <div class="html-preview-container" style="border: 1px solid var(--border-color); padding: 1rem; border-radius: 4px; background: white; max-height: 300px; overflow-y: auto;">
                                <pre style="white-space: pre-wrap; font-family: monospace; font-size: 0.9em; margin: 0;"><c:out value="${item.bodyHtml}"/></pre>
                            </div>
                        </dd>

                        <dt>Nội dung văn bản (Body Plain Text)</dt>
                        <dd>
                            <div style="border: 1px solid var(--border-color); padding: 1rem; border-radius: 4px; background: #fafafa; max-height: 200px; overflow-y: auto;">
                                <pre style="white-space: pre-wrap; font-family: inherit; font-size: 0.9em; margin: 0;"><c:out value="${item.bodyText}"/></pre>
                            </div>
                        </dd>

                        <dt>Trạng thái kích hoạt</dt>
                        <dd>
                            <span class="status ${item.active?'success':'muted'}">
                                ${item.active?'ACTIVE':'INACTIVE'}
                            </span>
                        </dd>

                        <dt>Ngày tạo</dt>
                        <dd><c:out value="${item.createdAt}"/></dd>

                        <dt>Cập nhật lần cuối</dt>
                        <dd><c:out value="${item.updatedAt}"/></dd>
                    </dl>
                </div>

                <aside class="panel action-panel">
                    <h2>Thay đổi trạng thái</h2>
                    <p>Nếu tắt hoạt động, email tự động tương ứng với sự kiện này sẽ không được kích hoạt gửi.</p>
                    <form class="stack-form" method="post" action="${pageContext.request.contextPath}/admin/email-templates/toggle-active">
                        <input type="hidden" name="id" value="${item.id}">
                        <label>Trạng thái hoạt động
                            <select name="active">
                                <option value="true" ${item.active?'selected':''}>ACTIVE</option>
                                <option value="false" ${!item.active?'selected':''}>INACTIVE</option>
                            </select>
                        </label>
                        <button class="primary" type="submit">Cập nhật</button>
                    </form>
                </aside>
            </section>
        </div>
    </main>
</div>
</body>
</html>
