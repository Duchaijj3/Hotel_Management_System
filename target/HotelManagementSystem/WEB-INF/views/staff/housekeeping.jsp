<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản Lý Dọn Phòng</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet">
    <style>
        body { background-color: #f4f6f9; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; }
        .stat-card { border-radius: 12px; border: none; box-shadow: 0 2px 10px rgba(0,0,0,0.03); }
        .border-left-danger { border-left: 4px solid #dc3545; }
        .border-left-warning { border-left: 4px solid #ffc107; }
        .border-left-info { border-left: 4px solid #0dcaf0; }
        .table-custom { background: #fff; border-radius: 12px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.03); }
        .table-custom th { text-transform: uppercase; font-size: 0.8rem; color: #6c757d; font-weight: 600; padding: 1rem; border-bottom: 2px solid #edf2f9; }
        .table-custom td { padding: 1rem; vertical-align: middle; border-bottom: 1px solid #edf2f9; color: #333; }
        .badge-soft-danger { background-color: #fdeaea; color: #dc3545; }
        .badge-soft-warning { background-color: #fff8e6; color: #f5a623; }
        .badge-soft-info { background-color: #e0f6fb; color: #0dcaf0; }
        .badge-soft-success { background-color: #e6f9f0; color: #198754; }
        .action-link { font-weight: 600; text-decoration: none; font-size: 0.9rem; }
        .action-link:hover { text-decoration: underline; }

        /* ===== Khung Sidebar + Topbar (chỉ thêm mới, không đụng CSS cũ ở trên) ===== */
        .hms-shell { display: flex; min-height: 100vh; }
        .hms-sidebar { width: 260px; flex-shrink: 0; background: #0b1f4b; color: #fff; padding: 1.5rem 1rem; display: flex; flex-direction: column; }
        .hms-sidebar .hms-brand { font-size: 1.2rem; font-weight: 700; padding: 0 .5rem; margin-bottom: 1.5rem; }
        .hms-sidebar .hms-userbox { display: flex; align-items: center; gap: .7rem; background: rgba(255,255,255,.06); border-radius: 10px; padding: .7rem; margin-bottom: 1.5rem; }
        .hms-sidebar .hms-userbox .hms-avatar { width: 36px; height: 36px; border-radius: 50%; background: #3a5bbf; display: flex; align-items: center; justify-content: center; font-weight: 700; font-size: .85rem; flex-shrink: 0; }
        .hms-sidebar .hms-userbox .hms-name { font-weight: 600; font-size: .88rem; line-height: 1.2; }
        .hms-sidebar .hms-userbox .hms-role { font-size: .72rem; opacity: .7; }
        .hms-nav { list-style: none; padding: 0; margin: 0; flex: 1; }
        .hms-nav li a { display: flex; align-items: center; gap: .7rem; color: #c7d0e8; text-decoration: none; padding: .65rem .75rem; border-radius: 8px; margin-bottom: .25rem; font-size: .92rem; }
        .hms-nav li a:hover { background: rgba(255,255,255,.08); color: #fff; }
        .hms-nav li a.hms-active { background: #f4a623; color: #1a1a1a; font-weight: 600; }
        .hms-main { flex: 1; min-width: 0; display: flex; flex-direction: column; }
        .hms-topbar { background: #fff; border-bottom: 1px solid #e9edf3; padding: .85rem 1.5rem; display: flex; align-items: center; justify-content: space-between; gap: 1rem; }
        .hms-topbar .hms-search { flex: 1; max-width: 420px; position: relative; }
        .hms-topbar .hms-search input { width: 100%; padding: .55rem .9rem .55rem 2.3rem; border-radius: 8px; border: 1px solid #e3e7ee; background: #f4f6f9; font-size: .9rem; }
        .hms-topbar .hms-search i { position: absolute; left: .8rem; top: 50%; transform: translateY(-50%); color: #9aa4b2; }
        .hms-topbar .hms-icon-btn { width: 38px; height: 38px; border-radius: 50%; background: #f4f6f9; display: flex; align-items: center; justify-content: center; color: #555; text-decoration: none; flex-shrink: 0; }
        @media (max-width: 900px) { .hms-sidebar { display: none; } }
    </style>
</head>
<body>

<div class="hms-shell">

    <!-- ===== SIDEBAR (chỉ phần vỏ, chưa có route thật ngoài Housekeeping) ===== -->
    <aside class="hms-sidebar">
        <div class="hms-brand">🏨 LuxeStay HMS</div>

        <div class="hms-userbox">
            <div class="hms-avatar">NV</div>
            <div>
                <div class="hms-name">Nhân viên Buồng phòng</div>
                <div class="hms-role">Housekeeping Staff</div>
            </div>
        </div>

        <ul class="hms-nav">
            <li><a href="#"><i class="bi bi-grid-1x2"></i> Dashboard</a></li>
            <li><a href="#"><i class="bi bi-calendar-check"></i> Reservations</a></li>
            <li><a href="#"><i class="bi bi-door-open"></i> Reception</a></li>
            <li><a href="${pageContext.request.contextPath}/staff/housekeeping" class="hms-active">
                <i class="bi bi-bucket"></i> Housekeeping</a></li>
            <li><a href="${pageContext.request.contextPath}/staff/tasks"><i class="bi bi-bell"></i> Dịch vụ</a></li>
            <li><a href="#"><i class="bi bi-gear"></i> Settings</a></li>
        </ul>
    </aside>

    <div class="hms-main">

        <!-- ===== TOPBAR (chỉ vỏ, chưa gắn chức năng tìm kiếm/thông báo thật) ===== -->
        <header class="hms-topbar">
            <div class="hms-search">
                <i class="bi bi-search"></i>
                <input type="text" placeholder="Search rooms, staff, or guests...">
            </div>
            <div class="d-flex align-items-center">
                <a href="#" class="hms-icon-btn"><i class="bi bi-bell"></i></a>
                <a href="#" class="hms-icon-btn"><i class="bi bi-question-circle"></i></a>
            </div>
        </header>

        <div class="container-fluid py-4 px-4">
            <!-- Header -->
            <div class="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h3 class="fw-bold mb-1">Quản Lý Công Việc Dọn Phòng</h3>
                    <p class="text-muted mb-0">Theo dõi và tiếp nhận các yêu cầu dọn dẹp buồng phòng.</p>
                </div>
            </div>

            <!-- Thông báo -->
            <c:if test="${not empty success}">
                <div class="alert alert-success alert-dismissible fade show border-0 shadow-sm" role="alert">
                    <i class="bi bi-check-circle-fill me-2"></i>${success}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>
            <c:if test="${not empty error}">
                <div class="alert alert-danger alert-dismissible fade show border-0 shadow-sm" role="alert">
                    <i class="bi bi-exclamation-triangle-fill me-2"></i>${error}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>

            <!-- Thẻ Thống kê (Mô phỏng Dashboard) -->
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
                    <div class="card stat-card border-left-info p-3 h-100">
                        <div class="text-muted small fw-bold text-uppercase mb-1">
                            Đã hoàn thành
                        </div>
                        <div class="fs-3 fw-bold text-info">${totalCompleted}</div>
                    </div>
                </div>
            </div>

            <!-- Điều hướng Tabs -->
            <ul class="nav nav-pills mb-3" id="task-tabs" role="tablist">
                <li class="nav-item" role="presentation">
                    <button class="nav-link active px-4 rounded-pill fw-bold" data-bs-toggle="pill" data-bs-target="#my-tasks" type="button">
                        Công việc của tôi
                    </button>
                </li>
                <li class="nav-item ms-2" role="presentation">
                    <button class="nav-link px-4 rounded-pill fw-bold" data-bs-toggle="pill" data-bs-target="#pending-tasks" type="button">
                        Phòng chờ dọn
                    </button>
                </li>
            </ul>

            <!-- Nội dung Bảng -->
            <div class="tab-content table-custom">
                <!-- TAB 1: CÔNG VIỆC CỦA TÔI -->
                <div class="tab-pane fade show active" id="my-tasks" role="tabpanel">
                    <table class="table table-borderless mb-0">
                        <thead>
                        <tr>
                            <th>Phòng</th>
                            <th>Tình trạng phòng</th>
                            <th>Loại yêu cầu</th>
                            <th>Trạng thái công việc</th>
                            <th>Độ ưu tiên</th>
                            <th class="text-end">Hành động</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="task" items="${myTasks}">
                            <tr>
                                <td class="fw-bold">Phòng <c:out value="${task.roomNumber}"/></td>

                                <td>
                                    <c:choose>
                                        <c:when test="${task.cleaningStatus == 'CLEANING'}">
                                    <span class="badge badge-soft-warning px-3 py-2 rounded-pill">
                                        Đang dọn
                                    </span>
                                        </c:when>
                                        <c:when test="${task.cleaningStatus == 'CLEAN'}">
                                    <span class="badge badge-soft-success px-3 py-2 rounded-pill">
                                        Sạch
                                    </span>
                                        </c:when>
                                        <c:otherwise>
                                    <span class="badge badge-soft-danger px-3 py-2 rounded-pill">
                                        <c:out value="${task.cleaningStatus}"/>
                                    </span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>

                                <td><c:out value="${task.taskType}"/></td>

                                <td>
                                    <c:choose>
                                        <c:when test="${task.statusCode == 'IN_PROGRESS'}">
                                    <span class="badge badge-soft-warning px-3 py-2 rounded-pill">
                                        Đang dọn
                                    </span>
                                        </c:when>
                                        <c:when test="${task.statusCode == 'COMPLETED'}">
                                    <span class="badge badge-soft-success px-3 py-2 rounded-pill">
                                        Hoàn thành
                                    </span>
                                        </c:when>
                                    </c:choose>
                                </td>

                                <td>
                                    <c:if test="${task.priorityCode == 'HIGH' || task.priorityCode == 'URGENT'}">
                                        <span class="text-danger fw-bold"><i class="bi bi-exclamation-circle-fill me-1"></i>Cao</span>
                                    </c:if>
                                    <c:if test="${task.priorityCode != 'HIGH' && task.priorityCode != 'URGENT'}">
                                        <span class="text-secondary">Bình thường</span>
                                    </c:if>
                                </td>

                                <td class="text-end">
                                    <a href="${pageContext.request.contextPath}/staff/housekeeping/view?id=${task.taskId}"
                                       class="action-link text-primary">
                                        Chi tiết
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty myTasks}">
                            <tr><td colspan="6" class="text-center py-5 text-muted">Không có công việc nào đang xử lý.</td></tr>
                        </c:if>
                        </tbody>
                    </table>
                </div>

                <!-- TAB 2: PHÒNG CHỜ DỌN -->
                <div class="tab-pane fade" id="pending-tasks" role="tabpanel">
                    <table class="table table-borderless mb-0">
                        <thead>
                        <tr>
                            <th>Phòng</th>
                            <th>Tình trạng phòng</th>
                            <th>Loại yêu cầu</th>
                            <th>Trạng thái công việc</th>
                            <th>Độ ưu tiên</th>
                            <th class="text-end">Hành động</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="pTask" items="${pendingTasks}">
                            <tr>
                                <td class="fw-bold">Phòng ${pTask.roomNumber}</td>

                                <td>
                            <span class="badge badge-soft-danger px-3 py-2 rounded-pill">
                                <c:out value="${pTask.cleaningStatus}"/>
                            </span>
                                </td>

                                <td>${pTask.taskType}</td>

                                <td>
                                    <span class="badge badge-soft-danger px-3 py-2 rounded-pill">Chờ dọn</span>
                                </td>

                                <td>
                                    <c:if test="${pTask.priorityCode == 'HIGH' || pTask.priorityCode == 'URGENT'}">
                                        <span class="text-danger fw-bold"><i class="bi bi-exclamation-circle-fill me-1"></i>Cao</span>
                                    </c:if>
                                    <c:if test="${pTask.priorityCode != 'HIGH' && pTask.priorityCode != 'URGENT'}">
                                        <span class="text-secondary">Bình thường</span>
                                    </c:if>
                                </td>

                                <td class="text-end">
                                    <form action="${pageContext.request.contextPath}/staff/housekeeping/accept"
                                          method="post"
                                          class="d-inline">
                                        <input type="hidden" name="id" value="${pTask.taskId}">
                                        <button type="submit"
                                                class="btn btn-link action-link text-primary p-0 m-0 text-decoration-none">
                                            Nhận việc
                                        </button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty pendingTasks}">
                            <tr><td colspan="6" class="text-center py-5 text-muted">Hiện tại không có phòng nào cần dọn.</td></tr>
                        </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

    </div><!-- /.hms-main -->
</div><!-- /.hms-shell -->

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>