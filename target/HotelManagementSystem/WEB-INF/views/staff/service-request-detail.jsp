<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Chi tiết yêu cầu dịch vụ</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
          rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css"
          rel="stylesheet">

    <style>
        body {
            background: #f4f6f9;
            font-family: "Segoe UI", sans-serif;
        }

        .detail-card {
            border: 0;
            border-radius: 12px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, .04);
        }

        .info-label {
            color: #6c757d;
            font-size: .78rem;
            font-weight: 700;
            text-transform: uppercase;
        }

        .info-value {
            color: #212529;
            font-size: 1rem;
            font-weight: 500;
        }

        .badge-soft-primary {
            background: #e7f1ff;
            color: #0d6efd;
        }

        .badge-soft-warning {
            background: #fff3cd;
            color: #8a6500;
        }

        .badge-soft-success {
            background: #d1e7dd;
            color: #146c43;
        }

        .badge-soft-danger {
            background: #f8d7da;
            color: #b02a37;
        }
    </style>
</head>

<body>
<div class="container py-5" style="max-width: 900px;">

    <a href="${pageContext.request.contextPath}/staff/service-requests"
       class="text-decoration-none text-secondary fw-bold d-inline-block mb-3">
        <i class="bi bi-arrow-left me-1"></i>
        Quay lại danh sách
    </a>

    <c:if test="${not empty success}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="bi bi-check-circle-fill me-2"></i>
            <c:out value="${success}"/>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <c:if test="${not empty error}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="bi bi-exclamation-triangle-fill me-2"></i>
            <c:out value="${error}"/>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <div class="card detail-card">
        <div class="card-body p-4">

            <div class="d-flex justify-content-between align-items-start border-bottom pb-3 mb-4">
                <div>
                    <h3 class="fw-bold mb-1">
                        <c:out value="${serviceRequest.serviceName}"/>
                    </h3>
                    <span class="text-muted">
                        Mã yêu cầu #<c:out value="${serviceRequest.requestId}"/>
                    </span>
                </div>

                <c:choose>
                    <c:when test="${serviceRequest.status == 'ASSIGNED'}">
                        <span class="badge badge-soft-primary rounded-pill px-3 py-2">
                            Đã nhận
                        </span>
                    </c:when>

                    <c:when test="${serviceRequest.status == 'IN_PROGRESS'}">
                        <span class="badge badge-soft-warning rounded-pill px-3 py-2">
                            Đang thực hiện
                        </span>
                    </c:when>

                    <c:when test="${serviceRequest.status == 'COMPLETED'}">
                        <span class="badge badge-soft-success rounded-pill px-3 py-2">
                            Hoàn thành
                        </span>
                    </c:when>

                    <c:when test="${serviceRequest.status == 'CANCELLED'}">
                        <span class="badge badge-soft-danger rounded-pill px-3 py-2">
                            Đã hủy
                        </span>
                    </c:when>
                </c:choose>
            </div>

            <div class="row g-4 mb-4">
                <div class="col-md-4">
                    <div class="info-label">Số lượng</div>
                    <div class="info-value">
                        <c:out value="${serviceRequest.quantity}"/>
                    </div>
                </div>

                <div class="col-md-4">
                    <div class="info-label">Đơn giá</div>
                    <div class="info-value">
                        <c:out value="${serviceRequest.unitPrice}"/> VND
                    </div>
                </div>

                <div class="col-md-4">
                    <div class="info-label">Tổng tiền</div>
                    <div class="info-value text-primary">
                        <c:out value="${serviceRequest.totalAmount}"/> VND
                    </div>
                </div>

                <div class="col-md-6">
                    <div class="info-label">Khách gửi yêu cầu lúc</div>
                    <div class="info-value">
                        <c:out value="${serviceRequest.requestedAt}"/>
                    </div>
                </div>

                <div class="col-md-6">
                    <div class="info-label">Thời điểm cần phục vụ</div>
                    <div class="info-value">
                        <c:out value="${serviceRequest.requestedForAt}"/>
                    </div>
                </div>

                <div class="col-md-6">
                    <div class="info-label">Thời điểm nhận việc</div>
                    <div class="info-value">
                        <c:out value="${empty serviceRequest.assignedAt
                                ? 'Chưa nhận'
                                : serviceRequest.assignedAt}"/>
                    </div>
                </div>

                <div class="col-md-6">
                    <div class="info-label">Thời điểm hoàn thành</div>
                    <div class="info-value">
                        <c:out value="${empty serviceRequest.completedAt
                                ? 'Chưa hoàn thành'
                                : serviceRequest.completedAt}"/>
                    </div>
                </div>
            </div>

            <div class="mb-4">
                <div class="info-label mb-2">Ghi chú của khách</div>
                <div class="bg-light border rounded p-3">
                    <c:out value="${empty serviceRequest.notes
                            ? 'Khách không có ghi chú thêm.'
                            : serviceRequest.notes}"/>
                </div>
            </div>

            <c:if test="${serviceRequest.status == 'CANCELLED'}">
                <div class="mb-4">
                    <div class="info-label mb-2 text-danger">Lý do hủy</div>
                    <div class="alert alert-danger mb-0">
                        <c:out value="${serviceRequest.cancellationReason}"/>
                    </div>
                </div>
            </c:if>

            <div class="border-top pt-4 d-flex justify-content-end gap-2">

                <c:if test="${serviceRequest.status == 'ASSIGNED'}">
                    <form action="${pageContext.request.contextPath}/staff/service-requests/start"
                          method="post">
                        <input type="hidden"
                               name="id"
                               value="${serviceRequest.requestId}">

                        <button type="submit" class="btn btn-primary">
                            <i class="bi bi-play-fill me-1"></i>
                            Bắt đầu thực hiện
                        </button>
                    </form>
                </c:if>

                <c:if test="${serviceRequest.status == 'IN_PROGRESS'}">
                    <form action="${pageContext.request.contextPath}/staff/service-requests/complete"
                          method="post">
                        <input type="hidden"
                               name="id"
                               value="${serviceRequest.requestId}">

                        <button type="submit" class="btn btn-success">
                            <i class="bi bi-check-lg me-1"></i>
                            Xác nhận hoàn thành
                        </button>
                    </form>
                </c:if>
            </div>

            <c:if test="${serviceRequest.status == 'ASSIGNED'
                    || serviceRequest.status == 'IN_PROGRESS'}">
                <form action="${pageContext.request.contextPath}/staff/service-requests/cancel"
                      method="post"
                      class="border-top pt-4 mt-4">

                    <input type="hidden"
                           name="id"
                           value="${serviceRequest.requestId}">

                    <label for="cancellationReason" class="form-label text-danger fw-bold">
                        Hủy yêu cầu dịch vụ
                    </label>

                    <textarea id="cancellationReason"
                              name="cancellationReason"
                              class="form-control"
                              rows="3"
                              maxlength="1000"
                              required
                              placeholder="Nhập lý do hủy yêu cầu..."></textarea>

                    <div class="text-end mt-3">
                        <button type="submit" class="btn btn-outline-danger">
                            <i class="bi bi-x-circle me-1"></i>
                            Xác nhận hủy yêu cầu
                        </button>
                    </div>
                </form>
            </c:if>

        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>