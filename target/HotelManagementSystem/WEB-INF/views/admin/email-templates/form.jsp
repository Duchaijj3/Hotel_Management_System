<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>${mode=='create'?'Tạo':'Sửa'} mẫu email</title>
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
                    <h1>${mode=='create'?'Tạo mẫu email thông báo':'Cập nhật mẫu email'}</h1>
                    <p>Thiết lập nội dung và cấu trúc mẫu email gửi tự động.</p>
                </div>
            </div>

            <c:if test="${not empty errors.general}">
                <p class="alert error"><c:out value="${errors.general}"/></p>
            </c:if>

            <form class="panel form-panel" method="post">
                <c:if test="${mode=='edit'}">
                    <input type="hidden" name="id" value="${item.id}">
                </c:if>
                <div class="form-grid">
                    <label>Mã mẫu *
                        <input name="templateCode" value="<c:out value='${item.templateCode}'/>" required ${mode=='edit'?'readonly':''} placeholder="ví dụ: TEMP_ACTIVATE_ACCOUNT">
                        <span class="field-error"><c:out value="${errors.templateCode}"/></span>
                    </label>

                    <label>Tên mẫu *
                        <input name="templateName" value="<c:out value='${item.templateName}'/>" required placeholder="Nhập tên mô tả mẫu">
                        <span class="field-error"><c:out value="${errors.templateName}"/></span>
                    </label>

                    <label>Mã sự kiện Trigger *
                        <input name="eventCode" value="<c:out value='${item.eventCode}'/>" required placeholder="ví dụ: F25 hoặc F06">
                        <span class="field-error"><c:out value="${errors.eventCode}"/></span>
                    </label>

                    <label class="checkbox-field" style="align-self: end; margin-bottom: 1.5rem;">
                        <input type="checkbox" name="active" value="true" ${item.active?'checked':''}>
                        Kích hoạt mẫu sử dụng ngay
                    </label>

                    <label class="span-2">Tiêu đề Email (Subject) *
                        <input name="subjectTemplate" value="<c:out value='${item.subjectTemplate}'/>" required placeholder="Nhập tiêu đề thư (hỗ trợ placeholder ${placeholder})">
                        <span class="field-error"><c:out value="${errors.subjectTemplate}"/></span>
                    </label>

                    <label class="span-2">Nội dung HTML (Body HTML) *
                        <textarea name="bodyHtml" rows="10" required placeholder="Nhập mã HTML của thư..."><c:out value="${item.bodyHtml}"/></textarea>
                        <span class="field-error"><c:out value="${errors.bodyHtml}"/></span>
                    </label>

                    <label class="span-2">Nội dung văn bản thường (Body Plain Text) *
                        <textarea name="bodyText" rows="6" required placeholder="Nhập nội dung văn bản thường của thư để dự phòng..."><c:out value="${item.bodyText}"/></textarea>
                        <span class="field-error"><c:out value="${errors.bodyText}"/></span>
                    </label>
                </div>

                <div class="form-actions">
                    <a class="secondary-button" href="${pageContext.request.contextPath}/admin/email-templates">Hủy</a>
                    <button class="primary" type="submit">Lưu mẫu email</button>
                </div>
            </form>
        </div>
    </main>
</div>
</body>
</html>
