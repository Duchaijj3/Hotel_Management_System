<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Quản lý dọn phòng</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
          rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css"
          rel="stylesheet">

    <style>
        body {
            background: #f4f6f9;
            font-family: "Segoe UI", sans-serif;
        }

        .page-card,
        .stat-card {
            border: 0;
            border-radius: 12px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, .04);
        }

        .border-left-danger {
            border-left: 4px solid #dc3545 !important;
        }

        .border-left-warning {
            border-left: 4px solid #ffc107 !important;
        }

        .border-left-success {
            border-left: 4px solid #198754 !important;
        }

        .table th {
            color: #6c757d;
            font-size: .8rem;
            text-transform: uppercase;
            white-space: nowrap;
        }

        .badge-soft-danger {
            background: #f8d7da;
            color: #b02a37;
        }

        .badge-soft-warning {
            background: #fff3cd;
            color: #8a6500;
        }

        .badge-soft-success {
            background: #d1e7dd;
            color: #146c43;
        }

        .badge-soft-primary {
            background: #e7f1ff;
            color: #0d6efd;
        }
    </style>
</head>

<body>
<div class="container-fluid py-4 px-4">

    <div class="mb-4">
        <h3 class="fw-bold mb-1">Quản lý công việc dọn phòng</h3>
        <p class="text-muted mb-0">
            Tiếp nhận và hoàn thành các công việc vệ sinh buồng phòng.
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

    <div class="row g-3 mb-4">
        <div class="col-md-4">
            <div class="card stat-card border-left-danger p-3 h-100">
                <div class="text-muted small fw-bold text-uppercase mb-1">
                    Phòng chờ nhận
                </div>
                <div class="fs-3 fw-bold text-danger">${totalPending}</div>
            </div>
        </div>

        <div class="col-md-4">
            <div class="card stat-card border-left-warning p-3 h-100">
                <div class="text-muted small fw-bold text-uppercase mb-1">
                    Đang dọn
                </div>
                <div class="fs-3 fw-bold text-warning">${totalInProgress}</div>
            </div>
        </div>

        <div class="col-md-4">
            <div class="card stat-card border-left-success p-3 h-100">
                <div class="text-muted small fw-bold text-uppercase mb-1">
                    Đã hoàn thành
                </div>
                <div class="fs-3 fw-bold text-success">${totalCompleted}</div>
            </div>
        </div>
    </div>

    <ul class="nav nav-pills mb-3" id="housekeepingTabs" role="tablist">
        <li class="nav-item" role="presentation">
            <button class="nav-link active"
                    data-bs-toggle="pill"
                    data-bs-target="#my-tasks"
                    type="button">
                Công việc của tôi
            </button>
        </li>

        <li class="nav-item ms-2" role="presentation">
            <button class="nav-link"
                    data-bs-toggle="pill"
                    data-bs-target="#pending-tasks"
                    type="button">
                Phòng chờ nhận
            </button>
        </li>
    </ul>

    <div class="tab-content">

        <div class="tab-pane fade show active" id="my-tasks">
            <div class="card page-card">
                <div class="card-body p-4">
                    <c:choose>
                        <c:when test="${empty myTasks}">
                            <p class="text-center text-muted py-4 mb-0">
                                Bạn chưa có công việc dọn phòng nào.
                            </p>
                        </c:when>

                        <c:otherwise>
                            <div class="table-responsive">
                                <table class="table table-hover align-middle mb-0">
                                    <thead>
                                    <tr>
                                        <th>Phòng</th>
                                        <th>Tình trạng phòng</th>
                                        <th>Loại công việc</th>
                                        <th>Trạng thái task</th>
                                        <th>Ưu tiên</th>
                                        <th class="text-end">Hành động</th>
                                    </tr>
                                    </thead>

                                    <tbody>
                                    <c:forEach items="${myTasks}" var="task">
                                        <tr>
                                            <td class="fw-bold">
                                                Phòng <c:out value="${task.roomNumber}"/>
                                            </td>

                                            <td>
                                                <c:choose>
                                                    <c:when test="${task.cleaningStatus == 'CLEANING'}">
                                                        <span class="badge badge-soft-warning rounded-pill px-3 py-2">
                                                            CLEANING — Đang dọn
                                                        </span>
                                                    </c:when>

                                                    <c:when test="${task.cleaningStatus == 'CLEAN'}">
                                                        <span class="badge badge-soft-success rounded-pill px-3 py-2">
                                                            CLEAN — Sạch
                                                        </span>
                                                    </c:when>

                                                    <c:otherwise>
                                                        <span class="badge badge-soft-danger rounded-pill px-3 py-2">
                                                            <c:out value="${task.cleaningStatus}"/>
                                                        </span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>

                                            <td>
                                                <c:out value="${task.taskType}"/>
                                            </td>

                                            <td>
                                                <c:choose>
                                                    <c:when test="${task.statusCode == 'IN_PROGRESS'}">
                                                        <span class="badge badge-soft-warning rounded-pill px-3 py-2">
                                                            Đang thực hiện
                                                        </span>
                                                    </c:when>

                                                    <c:when test="${task.statusCode == 'COMPLETED'}">
                                                        <span class="badge badge-soft-success rounded-pill px-3 py-2">
                                                            Hoàn thành
                                                        </span>
                                                    </c:when>
                                                </c:choose>
                                            </td>

                                            <td>
                                                <c:choose>
                                                    <c:when test="${task.priorityCode == 'URGENT'}">
                                                        <span class="text-danger fw-bold">
                                                            <i class="bi bi-exclamation-triangle-fill me-1"></i>
                                                            Khẩn cấp
                                                        </span>
                                                    </c:when>

                                                    <c:when test="${task.priorityCode == 'HIGH'}">
                                                        <span class="text-danger fw-bold">
                                                            Cao
                                                        </span>
                                                    </c:when>

                                                    <c:otherwise>
                                                        <span class="text-secondary">
                                                            <c:out value="${task.priorityCode}"/>
                                                        </span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>

                                            <td class="text-end">
                                                <a href="${pageContext.request.contextPath}/staff/housekeeping/view?id=${task.taskId}"
                                                   class="btn btn-sm btn-outline-primary">
                                                    Chi tiết
                                                </a>
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

        <div class="tab-pane fade" id="pending-tasks">
            <div class="card page-card">
                <div class="card-body p-4">
                    <c:choose>
                        <c:when test="${empty pendingTasks}">
                            <p class="text-center text-muted py-4 mb-0">
                                Hiện không có phòng nào chờ dọn.
                            </p>
                        </c:when>

                        <c:otherwise>
                            <div class="table-responsive">
                                <table class="table table-hover align-middle mb-0">
                                    <thead>
                                    <tr>
                                        <th>Phòng</th>
                                        <th>Tình trạng phòng</th>
                                        <th>Loại công việc</th>
                                        <th>Ưu tiên</th>
                                        <th>Ghi chú</th>
                                        <th class="text-end">Hành động</th>
                                    </tr>
                                    </thead>

                                    <tbody>
                                    <c:forEach items="${pendingTasks}" var="task">
                                        <tr>
                                            <td class="fw-bold">
                                                Phòng <c:out value="${task.roomNumber}"/>
                                            </td>

                                            <td>
                                                <span class="badge badge-soft-danger rounded-pill px-3 py-2">
                                                    <c:out value="${task.cleaningStatus}"/> — Cần dọn
                                                </span>
                                            </td>

                                            <td>
                                                <c:out value="${task.taskType}"/>
                                            </td>

                                            <td>
                                                <c:choose>
                                                    <c:when test="${task.priorityCode == 'URGENT'
                                                            || task.priorityCode == 'HIGH'}">
                                                        <span class="text-danger fw-bold">
                                                            <c:out value="${task.priorityCode}"/>
                                                        </span>
                                                    </c:when>

                                                    <c:otherwise>
                                                        <span class="text-secondary">
                                                            <c:out value="${task.priorityCode}"/>
                                                        </span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>

                                            <td class="text-muted">
                                                <c:out value="${empty task.notes
                                                        ? 'Không có ghi chú.'
                                                        : task.notes}"/>
                                            </td>

                                            <td class="text-end">
                                                <form action="${pageContext.request.contextPath}/staff/housekeeping/accept"
                                                      method="post"
                                                      class="d-inline">
                                                    <input type="hidden"
                                                           name="id"
                                                           value="${task.taskId}">

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

    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>