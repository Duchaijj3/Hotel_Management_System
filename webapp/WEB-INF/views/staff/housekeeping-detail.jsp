<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi tiết dọn phòng - Phòng ${task.roomNumber}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet">
    <style>
        body { background-color: #f4f6f9; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; }
        .card-custom { border-radius: 12px; border: none; box-shadow: 0 2px 10px rgba(0,0,0,0.03); }
        .info-label { font-size: 0.85rem; color: #6c757d; text-transform: uppercase; font-weight: 600; margin-bottom: 0.2rem; }
        .info-value { font-size: 1.1rem; color: #212529; font-weight: 500; }
    </style>
</head>
<body>

<div class="container py-5" style="max-width: 800px;">
    <!-- Nút quay lại -->
    <a href="${pageContext.request.contextPath}/staff/housekeeping" class="text-decoration-none text-secondary d-inline-block mb-3 fw-bold">
        <i class="bi bi-arrow-left me-1"></i> Quay lại danh sách
    </a>

    <!-- Thông báo lỗi (nếu có) -->
    <c:if test="${not empty error}">
        <div class="alert alert-danger mb-4 border-0 shadow-sm"><i class="bi bi-x-circle me-2"></i>${error}</div>
    </c:if>
    <c:if test="${not empty success}">
        <div class="alert alert-success mb-4 border-0 shadow-sm"><i class="bi bi-check-circle me-2"></i>${success}</div>
    </c:if>

    <div class="card card-custom p-4">
        <div class="d-flex justify-content-between align-items-start mb-4 border-bottom pb-3">
            <div>
                <h3 class="fw-bold mb-1">Phòng ${task.roomNumber}</h3>
                <span class="text-muted">Mã yêu cầu: #${task.taskId}</span>
            </div>
            <div>
                <span class="badge bg-primary px-3 py-2 rounded-pill fs-6">${task.statusCode}</span>
            </div>
        </div>

        <div class="row g-4 mb-4">
            <div class="col-md-6">
                <div class="info-label">Loại Công Việc</div>
                <div class="info-value">${task.taskType}</div>
            </div>

            <div class="col-md-6">
                <div class="info-label">Tình trạng phòng</div>
                <div class="info-value">
                    <c:choose>
                        <c:when test="${task.cleaningStatus == 'CLEANING'}">
                            <span class="text-warning fw-bold">CLEANING — Đang dọn</span>
                        </c:when>
                        <c:when test="${task.cleaningStatus == 'CLEAN'}">
                            <span class="text-success fw-bold">CLEAN — Sạch</span>
                        </c:when>
                        <c:otherwise>
                            <span class="text-danger fw-bold">
                                <c:out value="${task.cleaningStatus}"/>
                            </span>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <div class="col-md-6">
                <div class="info-label">Mức Độ Ưu Tiên</div>
                <div class="info-value">
                    <c:choose>
                        <c:when test="${task.priorityCode == 'HIGH' || task.priorityCode == 'URGENT'}">
                            <span class="text-danger fw-bold"><i class="bi bi-exclamation-triangle-fill me-1"></i> Ưu tiên cao</span>
                        </c:when>
                        <c:otherwise>Bình thường</c:otherwise>
                    </c:choose>
                </div>
            </div>
            <div class="col-md-12">
                <div class="info-label">Ghi Chú Yêu Cầu (Từ lễ tân)</div>
                <div class="info-value bg-light p-3 rounded mt-1 border">
                    ${empty task.notes ? 'Không có ghi chú đặc biệt.' : task.notes}
                </div>
            </div>
        </div>

        <!-- Form Thao tác -->
        <form action="${pageContext.request.contextPath}/staff/housekeeping/complete"
              method="post"
              class="mt-4 border-top pt-4">
            <input type="hidden" name="id" value="${task.taskId}">

            <div class="d-flex justify-content-end">
                <c:if test="${task.statusCode == 'IN_PROGRESS'}">
                    <button type="submit" class="btn btn-success px-4 py-2 fw-bold">
                        <i class="bi bi-check-lg me-1"></i>
                        Hoàn thành dọn phòng
                    </button>
                </c:if>

                <c:if test="${task.statusCode == 'COMPLETED'}">
                    <span class="text-success fw-bold">
                        <i class="bi bi-check-circle-fill me-1"></i>
                        Công việc đã hoàn thành
                    </span>
                </c:if>
            </div>
        </form>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>