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
    <style>
        /* Modernized Info List spacing and design */
        .info-list dt {
            color: #4b5563 !important;
            font-weight: 500;
        }
        .info-list dd {
            color: #111827;
        }
        .status.success {
            background-color: #ecfdf5 !important;
            color: #065f46 !important;
            padding: 4px 12px !important;
            border-radius: 9999px !important;
            font-size: 12px !important;
            font-weight: 600 !important;
            display: inline-block;
        }
        .status.muted {
            background-color: #f3f4f6 !important;
            color: #374151 !important;
            padding: 4px 12px !important;
            border-radius: 9999px !important;
            font-size: 12px !important;
            font-weight: 600 !important;
            display: inline-block;
        }
    </style>
</head>
<body>
<div class="app-shell">
    <%@ include file="../fragments/sidebar.jspf" %>
    <main class="main-panel">
        <%@ include file="../fragments/header.jspf" %>
        <div class="content narrow-content">
            <%
                com.hotel.dto.EmailTemplateDetailDto detailItem = (com.hotel.dto.EmailTemplateDetailDto) request.getAttribute("item");
                java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                String formattedCreated = detailItem != null && detailItem.createdAt() != null ? detailItem.createdAt().format(dtf) : "";
                String formattedUpdated = detailItem != null && detailItem.updatedAt() != null ? detailItem.updatedAt().format(dtf) : "";
                request.setAttribute("formattedCreated", formattedCreated);
                request.setAttribute("formattedUpdated", formattedUpdated);
            %>
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

                        <dt>Nội dung văn bản (Body Plain Text)</dt>
                        <dd>
                            <div style="border: 1px solid #eeedf4; padding: 14px 18px; border-radius: 8px; background: #f9fafb; max-height: 250px; overflow-y: auto; box-shadow: inset 0 1px 2px rgba(0,0,0,0.02);">
                                <pre style="white-space: pre-wrap; font-family: 'Inter', system-ui, -apple-system, sans-serif; font-size: 13.5px; line-height: 1.6; color: #374151; margin: 0;"><c:out value="${item.bodyText}"/></pre>
                            </div>
                        </dd>

                        <dt>Trạng thái kích hoạt</dt>
                        <dd>
                            <span class="status ${item.active?'success':'muted'}">
                                ${item.active?'ACTIVE':'INACTIVE'}
                            </span>
                        </dd>

                        <dt>Ngày tạo</dt>
                        <dd><c:out value="${formattedCreated}"/></dd>

                        <dt>Cập nhật lần cuối</dt>
                        <dd><c:out value="${formattedUpdated}"/></dd>
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
                    
                    <hr style="border: none; border-top: 1px solid #eeedf4; margin: 20px 0;">
                    
                    <h2>Gỡ bỏ cấu hình</h2>
                    <p>Xóa vĩnh viễn mẫu email này khỏi hệ thống cơ sở dữ liệu.</p>
                    <form class="stack-form" method="post" action="${pageContext.request.contextPath}/admin/email-templates/delete" onsubmit="return confirm('Bạn có chắc chắn muốn xóa mẫu email này không? Thao tác này không thể khôi phục.');">
                        <input type="hidden" name="id" value="${item.id}">
                        <button class="secondary-button" style="width: 100%; border-color: var(--danger); color: var(--danger); background: transparent;" type="submit">Xóa mẫu email</button>
                    </form>
                </aside>
            </section>
        </div>
    </main>
</div>
</body>
</html>
