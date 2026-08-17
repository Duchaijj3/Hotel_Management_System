<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Service Staff - Quản lý công việc</title>
    <!-- Giả định dự án dùng Bootstrap hoặc CSS có sẵn -->
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .alert-success { color: green; padding: 10px; border: 1px solid green; background: #e6ffe6; margin-bottom: 20px;}
        .alert-error { color: red; padding: 10px; border: 1px solid red; background: #ffe6e6; margin-bottom: 20px;}
        table { width: 100%; border-collapse: collapse; margin-bottom: 30px; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
        .btn { padding: 5px 10px; cursor: pointer; border-radius: 3px; }
        .btn-accept { background-color: #007bff; color: white; border: none; }
        .btn-start { background-color: #ffc107; border: none; }
        .btn-complete { background-color: #28a745; color: white; border: none; }
        /* CSS cho nút Hủy và ô nhập lý do */
        .btn-cancel { background-color: #dc3545; color: white; border: none; }
        .input-reason { padding: 4px; border: 1px solid #ccc; border-radius: 3px; }
    </style>
</head>
<body>

    <h2>Khu vực dành cho Nhân viên Dịch vụ (Staff)</h2>

    <!-- Hiển thị thông báo (Flash Messages) -->
    <c:if test="${not empty success}">
        <div class="alert-success">${success}</div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert-error">${error}</div>
    </c:if>

    <hr/>

    <!-- Danh sách 1: Công việc đang chờ (PENDING) -->
    <h3>Đơn đặt dịch vụ mới (Chờ tiếp nhận)</h3>
    <table>
        <thead>
            <tr>
                <th>Mã YC</th>
                <th>Dịch vụ</th>
                <th>Số lượng</th>
                <th>Thời gian gọi</th>
                <th>Hành động</th>
            </tr>
        </thead>
        <tbody>
            <c:choose>
                <c:when test="${empty pendingTasks}">
                    <tr><td colspan="5" style="text-align: center;">Hiện không có yêu cầu nào đang chờ.</td></tr>
                </c:when>
                <c:otherwise>
                    <c:forEach var="task" items="${pendingTasks}">
                        <tr>
                            <td>${task.requestId()}</td>
                            <td><strong>${task.serviceName()}</strong></td>
                            <td>${task.quantity()}</td>
                            <td>${task.requestedAt()}</td>
                            <td>
                                <!-- Nút Nhận việc (Accept) -->
                                <form action="${pageContext.request.contextPath}/staff/tasks/accept" method="post" style="display:inline;">
                                    <input type="hidden" name="requestId" value="${task.requestId()}">
                                    <button type="submit" class="btn btn-accept">Nhận việc</button>
                                </form>

                                <!-- Nút Hủy (Cancel) -->
                                <form action="${pageContext.request.contextPath}/staff/tasks/cancel" method="post" style="display:inline; margin-left: 10px;">
                                    <input type="hidden" name="requestId" value="${task.requestId()}">
                                    <input type="text" name="reason" class="input-reason" placeholder="Lý do hủy..." required>
                                    <button type="submit" class="btn btn-cancel">Hủy</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </tbody>
    </table>

    <!-- Danh sách 2: Công việc của tôi (ASSIGNED & IN_PROGRESS) -->
    <h3>Công việc của tôi</h3>
    <table>
        <thead>
            <tr>
                <th>Mã YC</th>
                <th>Dịch vụ</th>
                <th>Số lượng</th>
                <th>Trạng thái</th>
                <th>Hành động</th>
            </tr>
        </thead>
        <tbody>
            <c:choose>
                <c:when test="${empty myTasks}">
                    <tr><td colspan="5" style="text-align: center;">Bạn chưa nhận công việc nào.</td></tr>
                </c:when>
                <c:otherwise>
                    <c:forEach var="myTask" items="${myTasks}">
                        <tr>
                            <td>${myTask.requestId()}</td>
                            <td><strong>${myTask.serviceName()}</strong></td>
                            <td>${myTask.quantity()}</td>
                            <td>
                                <c:if test="${myTask.status() == 'ASSIGNED'}">Đã nhận</c:if>
                                <c:if test="${myTask.status() == 'IN_PROGRESS'}">Đang làm</c:if>
                            </td>
                            <td>
                                <!-- Nút Bắt đầu (Start) nếu đang ở trạng thái ASSIGNED -->
                                <c:if test="${myTask.status() == 'ASSIGNED'}">
                                    <form action="${pageContext.request.contextPath}/staff/tasks/start" method="post" style="display:inline;">
                                        <input type="hidden" name="requestId" value="${myTask.requestId()}">
                                        <button type="submit" class="btn btn-start">Bắt đầu thực hiện</button>
                                    </form>
                                </c:if>

                                <!-- Nút Hoàn thành (Complete) nếu đang ở trạng thái IN_PROGRESS -->
                                <c:if test="${myTask.status() == 'IN_PROGRESS'}">
                                    <form action="${pageContext.request.contextPath}/staff/tasks/complete" method="post" style="display:inline;">
                                        <input type="hidden" name="requestId" value="${myTask.requestId()}">
                                        <button type="submit" class="btn btn-complete">Hoàn thành</button>
                                    </form>
                                </c:if>

                                <!-- Nút Hủy (Cancel) dành cho việc đã nhận nhưng muốn hủy -->
                                <form action="${pageContext.request.contextPath}/staff/tasks/cancel" method="post" style="display:inline; margin-left: 10px;">
                                    <input type="hidden" name="requestId" value="${myTask.requestId()}">
                                    <input type="text" name="reason" class="input-reason" placeholder="Lý do hủy..." required>
                                    <button type="submit" class="btn btn-cancel">Hủy</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </tbody>
    </table>

</body>
</html>