<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>${mode=='create'?'Tạo':'Sửa'} tài khoản nhân viên</title>
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
                    <h1>${mode=='create'?'Tạo tài khoản nhân viên':'Cập nhật tài khoản'}</h1>
                    <p>Điền đầy đủ thông tin nhân sự và phân quyền tương ứng.</p>
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
                    <label>Email *
                        <input type="email" name="email" value="<c:out value='${item.email}'/>" required ${mode=='edit'?'readonly':''} placeholder="example@luxestay.com">
                        <span class="field-error"><c:out value="${errors.email}"/></span>
                    </label>

                    <label>Họ tên *
                        <input name="fullName" value="<c:out value='${item.fullName}'/>" required placeholder="Nhập họ và tên">
                        <span class="field-error"><c:out value="${errors.fullName}"/></span>
                    </label>

                    <label>Số điện thoại
                        <input name="phone" value="<c:out value='${item.phone}'/>" placeholder="Nhập số điện thoại">
                        <span class="field-error"><c:out value="${errors.phone}"/></span>
                    </label>

                    <label>Vai trò *
                        <select name="roleCode" required>
                            <option value="">Chọn vai trò</option>
                            <option value="ADMIN" ${item.roleCode=='ADMIN'?'selected':''}>System Administrator (ADMIN)</option>
                            <option value="MANAGER" ${item.roleCode=='MANAGER'?'selected':''}>Hotel Manager (MANAGER)</option>
                            <option value="RECEPTIONIST" ${item.roleCode=='RECEPTIONIST'?'selected':''}>Receptionist (RECEPTIONIST)</option>
                            <option value="SERVICE_STAFF" ${item.roleCode=='SERVICE_STAFF'?'selected':''}>Service Staff (SERVICE_STAFF)</option>
                        </select>
                        <span class="field-error"><c:out value="${errors.roleCode}"/></span>
                    </label>

                    <label>Bộ phận (Dành cho nhân viên dịch vụ)
                        <select name="departmentCode">
                            <option value="">Không có / Bộ phận khác</option>
                            <option value="GENERAL_SERVICE" ${item.departmentCode=='GENERAL_SERVICE'?'selected':''}>General Service</option>
                            <option value="HOUSEKEEPING" ${item.departmentCode=='HOUSEKEEPING'?'selected':''}>Housekeeping</option>
                            <option value="MAINTENANCE" ${item.departmentCode=='MAINTENANCE'?'selected':''}>Maintenance</option>
                        </select>
                        <span class="field-error"><c:out value="${errors.departmentCode}"/></span>
                    </label>

                    <label>Trạng thái tài khoản *
                        <select name="statusCode" required>
                            <option value="ACTIVE" ${item.statusCode=='ACTIVE'?'selected':''}>ACTIVE</option>
                            <option value="LOCKED" ${item.statusCode=='LOCKED'?'selected':''}>LOCKED</option>
                            <option value="INACTIVE" ${item.statusCode=='INACTIVE'?'selected':''}>INACTIVE</option>
                        </select>
                        <span class="field-error"><c:out value="${errors.statusCode}"/></span>
                    </label>

                    <label class="span-2">Mật khẩu ${mode=='create'?'(Để trống để tự động tạo ngẫu nhiên)':'(Để trống nếu không muốn thay đổi)'}
                        <input type="password" name="password" value="<c:out value='${item.password}'/>" placeholder="${mode=='create'?'Nhập mật khẩu hoặc để trống để tự động tạo':'Nhập mật khẩu mới nếu muốn thay đổi'}">
                        <span class="field-error"><c:out value="${errors.password}"/></span>
                    </label>

                    <c:if test="${mode=='create'}">
                        <label class="checkbox-field span-2">
                            <input type="checkbox" name="sendActivationEmail" ${item.sendActivationEmail?'checked':''}>
                            Gửi email kích hoạt tài khoản kèm mật khẩu cho nhân viên.
                        </label>
                    </c:if>
                </div>
                
                <div class="form-actions">
                    <a class="secondary-button" href="${pageContext.request.contextPath}/admin/users">Hủy</a>
                    <button class="primary" type="submit">Lưu tài khoản</button>
                </div>
            </form>
        </div>
    </main>
</div>
</body>
</html>
