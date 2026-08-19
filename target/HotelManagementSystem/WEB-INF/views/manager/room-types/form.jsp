<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html><html lang="vi"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>${mode=='create'?'Tạo':'Sửa'} loại phòng</title><link rel="stylesheet" href="${pageContext.request.contextPath}/css/receptionist.css?v=20260817"><link rel="stylesheet" href="${pageContext.request.contextPath}/css/manager.css?v=20260817"></head>
<body><div class="app-shell"><%@ include file="../fragments/sidebar.jspf" %><main class="main-panel"><%@ include file="../fragments/header.jspf" %><div class="content narrow-content">
<a class="back-link" href="${pageContext.request.contextPath}/manager/room-types">← Danh sách loại phòng</a><div class="page-heading"><div><h1>${mode=='create'?'Tạo loại phòng':'Cập nhật loại phòng'}</h1><p>Loại phòng mới luôn ở trạng thái inactive cho đến khi Manager kích hoạt.</p></div></div>
<c:if test="${not empty errors.general}"><p class="alert error"><c:out value="${errors.general}"/></p></c:if>
<form class="panel form-panel" method="post"><c:if test="${mode=='edit'}"><input type="hidden" name="id" value="${item.id}"><input type="hidden" name="version" value="${item.updatedAt}"></c:if><div class="form-grid">
<label>Mã loại phòng *<input name="typeCode" maxlength="20" required value="<c:out value='${item.typeCode}'/>"><span class="field-error"><c:out value="${errors.typeCode}"/></span></label>
<label>Tên loại phòng *<input name="typeName" maxlength="100" required value="<c:out value='${item.typeName}'/>"><span class="field-error"><c:out value="${errors.typeName}"/></span></label>
<label>Sức chứa người lớn *<input type="number" name="maxAdults" min="1" required value="${item.maxAdults}"><span class="field-error"><c:out value="${errors.maxAdults}"/></span></label>
<label>Sức chứa trẻ em *<input type="number" name="maxChildren" min="0" required value="${item.maxChildren}"><span class="field-error"><c:out value="${errors.maxChildren}"/></span></label>
<label>Loại giường<input name="bedType" maxlength="50" value="<c:out value='${item.bedType}'/>"></label>
<label>Diện tích (m²)<input type="number" step="0.01" min="0.01" name="roomSizeM2" value="${item.roomSizeM2}"><span class="field-error"><c:out value="${errors.roomSizeM2}"/></span></label>
<label>Giá cơ bản *<input type="number" step="0.01" min="0" name="basePrice" required value="${item.basePrice}"><span class="field-error"><c:out value="${errors.basePrice}"/></span></label>
<label>Tiện nghi<input name="amenities" value="<c:out value='${empty amenitiesText?item.amenities:amenitiesText}'/>" placeholder="Wi-Fi, Bathtub, Minibar"><small>Ngăn cách bằng dấu phẩy; thêm, đổi tên hoặc bỏ mục để attach/detach.</small></label>
<label class="span-2">Mô tả<textarea name="description" maxlength="4000"><c:out value="${item.description}"/></textarea></label>
</div><div class="form-actions"><a class="secondary-button" href="${pageContext.request.contextPath}/manager/room-types">Hủy</a><button class="primary" type="submit">Lưu loại phòng</button></div></form>
</div></main></div></body></html>
