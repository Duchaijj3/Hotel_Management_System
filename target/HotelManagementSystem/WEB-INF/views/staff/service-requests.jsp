<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Yêu cầu dịch vụ</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
          rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css"
          rel="stylesheet">

    <style>
        body {
            background: #f4f6f9;
            font-family: "Segoe UI", sans-serif;
        }

        .page-card {
            border: 0;
            border-radius: 12px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, .04);
        }

        .table th {
            color: #6c757d;
            font-size: .8rem;
            text-transform: uppercase;
            white-space: nowrap;
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

        .badge-soft-secondary {
            background: #e9ecef;
            color: #495057;
        }
    </style>
</head>

<body>
<div class="container-fluid py-4 px-4">

    <div class="mb-4">
        <h3 class="fw-bold mb-1">Yêu cầu dịch vụ</h3>
        <p class="text-muted mb-0">
            Tiếp nhận và thực hiện các yêu cầu dịch vụ của khách.
        </p>
    </div>

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

    <div class="card page-card mb-4">
        <div class="card-header bg-white border-bottom-0 pt-4 px-4">
            <div class="d-flex justify-content-between align-items-center">
                <div>
                    <h5 class="fw-bold mb-1">Yêu cầu của tôi</h5>
                    <p class="text-muted small mb-0">
                        Các yêu cầu bạn đã tiếp nhận hoặc đã xử lý.
                    </p>
                </div>

                <span class="badge text-bg-primary">
                    ${myRequests.size()} yêu cầu
                </span>
            </div>
        </div>

        <div class="card-body px-4 pb-4">
            <c:choose>
                <c:when test="${empty myRequests}">
                    <p class="text-center text-muted py-4 mb-0">
                        Bạn chưa tiếp nhận yêu cầu dịch vụ nào.
                    </p>
                </c:when>

                <c:otherwise>
                    <div class="table-responsive">
                        <table class="table table-hover align-middle mb-0">
                            <thead>
                            <tr>
                                <th>Mã</th>
                                <th>Dịch vụ</th>
                                <th>Số lượng</th>
                                <th>Tổng tiền</th>
                                <th>Thời điểm cần</th>
                                <th>Trạng thái</th>
                                <th class="text-end">Hành động</th>
                            </tr>
                            </thead>

                            <tbody>
                            <c:forEach items="${myRequests}" var="serviceRequest">
                                <tr>
                                    <td>
                                        <strong>#<c:out value="${serviceRequest.requestId}"/></strong>
                                    </td>

                                    <td>
                                        <i class="bi bi-bell-fill text-primary me-1"></i>
                                        <c:out value="${serviceRequest.serviceName}"/>
                                    </td>

                                    <td>
                                        <c:out value="${serviceRequest.quantity}"/>
                                    </td>

                                    <td>
                                        <c:out value="${serviceRequest.totalAmount}"/> VND
                                    </td>

                                    <td>
                                        <c:out value="${serviceRequest.requestedForAt}"/>
                                    </td>

                                    <td>
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

                                            <c:otherwise>
                                                <span class="badge badge-soft-secondary rounded-pill px-3 py-2">
                                                    <c:out value="${serviceRequest.status}"/>
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>

                                    <td class="text-end">
                                        <a class="btn btn-sm btn-outline-primary"
                                           href="${pageContext.request.contextPath}/staff/service-requests/view?id=${serviceRequest.requestId}">
                                            Chi tiết
                                        </a>

                                        <c:if test="${serviceRequest.status == 'ASSIGNED'}">
                                            <form class="d-inline"
                                                  action="${pageContext.request.contextPath}/staff/service-requests/start"
                                                  method="post">
                                                <input type="hidden"
                                                       name="id"
                                                       value="${serviceRequest.requestId}">
                                                <button type="submit"
                                                        class="btn btn-sm btn-primary">
                                                    Bắt đầu
                                                </button>
                                            </form>
                                        </c:if>

                                        <c:if test="${serviceRequest.status == 'IN_PROGRESS'}">
                                            <form class="d-inline"
                                                  action="${pageContext.request.contextPath}/staff/service-requests/complete"
                                                  method="post">
                                                <input type="hidden"
                                                       name="id"
                                                       value="${serviceRequest.requestId}">
                                                <button type="submit"
                                                        class="btn btn-sm btn-success">
                                                    Hoàn thành
                                                </button>
                                            </form>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <div class="card page-card">
        <div class="card-header bg-white border-bottom-0 pt-4 px-4">
            <div class="d-flex justify-content-between align-items-center">
                <div>
                    <h5 class="fw-bold mb-1">Yêu cầu chờ tiếp nhận</h5>
                    <p class="text-muted small mb-0">
                        Các order mới từ khách chưa có nhân viên xử lý.
                    </p>
                </div>

                <span class="badge text-bg-danger">
                    ${pendingRequests.size()} yêu cầu
                </span>
            </div>
        </div>

        <div class="card-body px-4 pb-4">
            <c:choose>
                <c:when test="${empty pendingRequests}">
                    <p class="text-center text-muted py-4 mb-0">
                        Không có yêu cầu dịch vụ mới.
                    </p>
                </c:when>

                <c:otherwise>
                    <div class="table-responsive">
                        <table class="table table-hover align-middle mb-0">
                            <thead>
                            <tr>
                                <th>Mã</th>
                                <th>Dịch vụ</th>
                                <th>Số lượng</th>
                                <th>Tổng tiền</th>
                                <th>Thời điểm cần</th>
                                <th>Ghi chú khách</th>
                                <th class="text-end">Hành động</th>
                            </tr>
                            </thead>

                            <tbody>
                            <c:forEach items="${pendingRequests}" var="serviceRequest">
                                <tr>
                                    <td>
                                        <strong>#<c:out value="${serviceRequest.requestId}"/></strong>
                                    </td>

                                    <td>
                                        <i class="bi bi-bell-fill text-danger me-1"></i>
                                        <c:out value="${serviceRequest.serviceName}"/>
                                    </td>

                                    <td>
                                        <c:out value="${serviceRequest.quantity}"/>
                                    </td>

                                    <td>
                                        <c:out value="${serviceRequest.totalAmount}"/> VND
                                    </td>

                                    <td>
                                        <c:out value="${serviceRequest.requestedForAt}"/>
                                    </td>

                                    <td class="text-muted">
                                        <c:out value="${empty serviceRequest.notes
                                                ? 'Không có ghi chú.'
                                                : serviceRequest.notes}"/>
                                    </td>

                                    <td class="text-end">
                                        <form class="d-inline"
                                              action="${pageContext.request.contextPath}/staff/service-requests/accept"
                                              method="post">
                                            <input type="hidden"
                                                   name="id"
                                                   value="${serviceRequest.requestId}">
                                            <button type="submit"
                                                    class="btn btn-sm btn-primary">
                                                <i class="bi bi-check2-circle me-1"></i>
                                                Nhận việc
                                            </button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>